#!/usr/bin/env python3

import json
import time
import sys
import os
import cv2 as cv
import numpy as np
import time
import util

from cscore import CameraServer, VideoSource, VideoMode
from networktables import NetworkTablesInstance, NetworkTables
import logging
from datetime import datetime
from random import randint

#   JSON format:
#   {
#       "team": <team number>,
#       "ntmode": <"client" or "server", "client" if unspecified>
#       "cameras": [
#           {
#               "name": <camera name>
#               "path": <path, e.g. "/dev/video0">
#               "pixel format": <"MJPEG", "YUYV", etc>   # optional
#               "width": <video mode width>              # optional
#               "height": <video mode height>            # optional
#               "fps": <video mode fps>                  # optional
#               "brightness": <percentage brightness>    # optional
#               "white balance": <"auto", "hold", value> # optional
#               "exposure": <"auto", "hold", value>      # optional
#               "properties": [                          # optional
#                   {
#                       "name": <property name>
#                       "value": <property value>
#                   }
#               ]
#           }
#       ]
#   }

########## CONFIG ##########

configFile = "/boot/frc.json"

class CameraConfig:
    pass

team = 1477
ntServerIpAddress = "10.14.77.2"
server = False
cameraConfigs = []
width = 320
height = 240
fps = 20
processingScale = 1.5
frameWidth = int(width / processingScale)
frameHeight = int(height / processingScale)
frameCenter = (int(frameWidth/2), int(frameHeight/2))
xOffset = int(width / 2)
yOffset = 240
showAllReturnedObjects = False

config = {"properties":[
    {"name":"connect_verbose","value":1},
    {"name":"raw_brightness","value":100},
    {"name":"brightness","value":0}, 
    {"name":"raw_contrast","value":0},
    {"name":"contrast","value":50},
    {"name":"raw_saturation","value":0},
    {"name":"saturation","value":50},
    {"name":"white_balance_temperature_auto","value":False},
    {"name":"power_line_frequency","value":2},
    {"name":"white_balance_temperature","value":4500},
    {"name":"raw_sharpness","value":0},
    {"name":"sharpness","value":0},
    {"name":"backlight_compensation","value":0},
    {"name":"exposure_auto","value":1},
    {"name":"raw_exposure_absolute","value":0},
    {"name":"exposure_absolute","value":0},
    {"name":"pan_absolute","value":0},
    {"name":"tilt_absolute","value":0},
    {"name":"zoom_absolute","value":0}]}

# Report parse error
def parseError(str):
    print("Config error in '" + configFile + "': " + str)
    # print("Config error in '" + configFile + "': " + str, file=sys.stderr)

# Read configuration file
def readConfig():
    global team

    # Parse file
    try:
        with open(configFile, "rt") as f:
            parsed = json.load(f)
    except OSError as err:
        print("could not open '{}': {}".format(configFile, err))
        # print("could not open '{}': {}".format(configFile, err), file=sys.stderr)
        return False

    # Top level must be an object
    if not isinstance(parsed, dict):
        parseError("Must be JSON object")
        return False

    # Team number
    try:
        team = parsed["team"]
    except KeyError:
        parseError("Could not read team number")
        return False

    # Cameras
    try:
        cameras = parsed["cameras"]
    except KeyError:
        parseError("Could not read cameras")
        return False

    for camera in cameras:
        if not readCameraConfig(camera):
            return False

    return True

# Read single camera configuration
def readCameraConfig(config):
    cam = CameraConfig()

    # Name
    try:
        cam.name = config["name"]
    except KeyError:
        parseError("Could not read camera name")
        return False

    # Path
    try:
        cam.path = config["path"]
    except KeyError:
        parseError("Camera '{}': could not read path".format(cam.name))
        return False

    cam.config = config

    cameraConfigs.append(cam)
    return True

# Start running the camera
def startCamera(config):
    print("Starting camera '{}' on {}".format(config.name, config.path))
    camera = CameraServer.getInstance().startAutomaticCapture(name=config.name, path=config.path)
    camera.setConfigJson(json.dumps(config.config))

    return camera


########## VISION LOGIC ##########

def findTargetTop(hsv, minHSV, maxHSV, kernel):
    mask = cv.inRange(hsv, minHSV, maxHSV)
    mask = cv.morphologyEx(mask, cv.MORPH_CLOSE, kernel)

    # Draw contours
    _, contours, hierarchy = cv.findContours(mask, cv.RETR_TREE, cv.CHAIN_APPROX_SIMPLE)

    frame = cv.cvtColor(hsv, cv.COLOR_HSV2BGR)
    cv.line(frame, (frameCenter[0], frameCenter[1]-5), (frameCenter[0], frameCenter[1]+5), (0,255,0), 1)
    cv.line(frame, (frameCenter[0]-5, frameCenter[1]), (frameCenter[0]+5, frameCenter[1]), (0,255,0), 1)

    if contours:
        # Construct dictionary that maps indices to contour areas, then discard areas that are too small or too big.
        areas = {i: cv.contourArea(cnt) for i, cnt in enumerate(contours)}
        areas = util.clamp(areas, 100, 16000)
        if not areas:
            return (False, 0, frame)

        # Construct rectangular boxes around each contour and store their coordinates in a dictionary.
        boxes = {}
        for i, area in areas.items():
            # epsilon = 0.03 * cv.arcLength(contours[i], True)
            # box = [(point[0][0], point[0][1]) for point in cv.approxPolyDP(contours[i], epsilon, True)]
            box = np.int0(cv.boxPoints(cv.minAreaRect(contours[i])))
            boxes[i] = list(box)

        # For each box, find top two and bottom coordinates. If ratio of side edge to top edge is about right, then confirm box is a target and analyze if it is a left or right target. Add all targets into a dictionary.
        targets = {}
        approxWidth = 0
        for i, box in boxes.items():
            if len(box) > 1:
                box = sorted(box, key=lambda b : b[1])
                top1 = box[0]
                top2 = box[1]

                cv.drawContours(frame, [np.int0(boxes[i])], 0, (0, 255, 0), 2)
                cv.circle(frame, (top1[0], top1[1]), 2, (255, 0, 0), -1)
                cv.circle(frame, (top2[0], top2[1]), 2, (0, 0, 255), -1)
                approxWidth += util.distance(top1, top2)

                if top1[0] < top2[0]:
                    targets[i] = ('L', top2)
                elif top1[0] > top2[0]:
                    targets[i] = ('R', top2)
                else:
                    targets[i] = ('X')
            else:
                continue
        if not targets:
            return (False, 0, frame)

        # Calculate average width of targets. Separate list of targets by left or right targets.
        approxWidth /= len(boxes)
        targetPairs = []
        leftTargets = [(i, target) for i, target in targets.items() if target[0] == 'L']
        rightTargets = [(i, target) for i, target in targets.items() if target[0] == 'R']

        # Find pairs of left and right targets that pair to form a valid vision target
        for i, left in leftTargets:
            for j, right in rightTargets:
                if left[1][0] < right[1][0] and util.approx(util.distance(left[1], right[1]), approxWidth*4, error=0.2) and util.inRange(abs(left[1][1]-right[1][1]), 0, 20):
                    targetPairs.append((left, right))

        # Calculate center of vision target by drawing diagonals
        centers = list(filter(lambda c : c != 0, [util.midpoint(left[1], right[1]) for left, right in targetPairs]))
        if centers:
            target = min(centers, key=lambda m : abs(m[0] - frameCenter[0]))
            cv.circle(frame, target, 2, (0, 255, 0), -1)
            cv.putText(frame, "X", (target[0]+3, target[1]+3), cv.FONT_HERSHEY_PLAIN, 1, (0, 255, 0))

            # (+) if target is to the right, (-) if target is to the left
            offset = 2 * (target[0] - frameCenter[0]) / frameWidth
            return (True, offset, frame)

    return (False, 0, frame)


########## BUFFER OUTPUT ##########

lastValue = 0
updateTime = 0
def bufferOutput(newOutput, bufferTime):
    global lastValue, updateTime
    if newOutput != 0:
        lastValue = newOutput
        updateTime = time.perf_counter()
        
    if time.perf_counter() - updateTime < bufferTime:
        return lastValue
    else:
        return 0 


########## MAIN ##########

if __name__ == "__main__":
    if len(sys.argv) >= 2:
        configFile = sys.argv[1]

    # read configuration
    if not readConfig():
        sys.exit(1)

    # start NetworkTables
    logging.basicConfig(level=logging.DEBUG)
    
    ntinst = NetworkTablesInstance.getDefault()

    if server:
        print("Setting up NetworkTables server")
        ntinst.startServer()
    else:
        print("Setting up NetworkTables client for team {}".format(team))
        # ntinst.initialize(server=ntServerIpAddress)
        # ntinst.startClientTeam(team)
        NetworkTables.initialize(server=ntServerIpAddress)
        targetTable = NetworkTables.getTable("TargetDetection")

    # setup a cvSource
    cs = CameraServer.getInstance()

    # Returns a cscore.VideoSource, used to automatically start a mjpegstream on port 1181
    camera = cs.startAutomaticCapture(name=cameraConfigs[0].name, path=cameraConfigs[0].path)

    # VideoMode.PixelFormat.kMJPEG, kBGR, kGray, kRGB565, kUnknown, kYUYV
    camera.setVideoMode(VideoMode.PixelFormat.kYUYV, width, height, fps)

    # Load camera properties config
    camera.setConfigJson(json.dumps(config))

    # Get a CvSink. This will capture images from the camera
    cvSink = cs.getVideo()

    # Let the camera initialize/warm up
    time.sleep(2.0)

    # Preallocate a numpy empty array
    img = np.zeros(shape=(frameHeight, frameWidth, 3), dtype=np.uint8)

    # (Optional) setup a CvSource. This will send images back to the Dashboard
    # Useful to see output from any image processing
    # outputStream = cs.putVideo("NameOfStream", frameWidth, frameHeight) 

    kernel = np.ones((5,5),np.uint8)

    # Target params
    targetTable.putNumber("frame_width", frameWidth)
    targetTable.putNumber("frame_height", frameHeight)
    targetOutputStream = cs.putVideo("TargetDetection", frameWidth, frameHeight)
    minTargetHSV = np.array([70, 70, 40])
    maxTargetHSV = np.array([80, 255, 255])

    while 1:
        time2, frame = cvSink.grabFrame(img)
        frame = cv.resize(frame, (frameWidth, frameHeight))
        hsv = cv.cvtColor(frame, cv.COLOR_BGR2HSV)

        targetExists, targetOffset, targetFrame = findTargetTop(hsv, minTargetHSV, maxTargetHSV, kernel)
        targetOutputStream.putFrame(targetFrame)
        targetTable.putBoolean("target_exists", targetExists)
        targetTable.putNumber("target_offset", bufferOutput(targetOffset, 1))
