import json
import time
import sys
import os
import cv2 as cv
import numpy as np
import util

from cscore import CameraServer, VideoSource, VideoMode
from networktables import NetworkTablesInstance, NetworkTables
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
fps = 30
processingScale = 2
processWidth = int(width / processingScale)
processHeight = int(height / processingScale)
xOffset = int(width / 2)
yOffset = 240
showAllReturnedObjects = False

config = {"properties":[
    {"name":"connect_verbose","value":1},
    {"name":"raw_brightness","value":100},
    {"name":"brightness","value":randint(20, 40)}, 
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
    print("Config error in '" + configFile + "': " + str, file=sys.stderr)

# Read configuration file
def readConfig():
    global team

    # Parse file
    try:
        with open(configFile, "rt") as f:
            parsed = json.load(f)
    except OSError as err:
        print("could not open '{}': {}".format(configFile, err), file=sys.stderr)
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

def findTargetTop(frame, minHSV, maxHSV, kernel, tables, outputStream):
    mask = cv.inRange(frame, minHSV, maxHSV)
    mask = cv.morphologyEx(mask, cv.MORPH_CLOSE, kernel)

    # Draw contours
    contours, hierarchy = cv.findContours(mask, cv.RETR_TREE, cv.CHAIN_APPROX_SIMPLE)

    if contours:
        # Construct dictionary that maps indices to contour areas, then discard areas that are too small or too big.
        areas = {i: cv.contourArea(cnt) for i, cnt in enumerate(contours)}
        areas = util.clamp(areas, 100, 16000)
        if not areas:
            outputStream.putFrame(frame)
            return 0

        # Construct rectangular boxes around each contour and store their coordinates in a dictionary.
        boxes = {}
        for i, area in areas.items():
            epsilon = 0.02 * cv.arcLength(contours[i], True)
            box = [(point[0][0], point[0][1]) for point in cv.approxPolyDP(contours[i], epsilon, True)]
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
            outputStream.putFrame(frame)
            return 0

        # Calculate average width of targets. Separate list of targets by left or right targets.
        approxWidth /= len(boxes)
        targetPairs = []
        leftTargets = [(i, target) for i, target in targets.items() if target[0] == 'L']
        rightTargets = [(i, target) for i, target in targets.items() if target[0] == 'R']

        # Find pairs of left and right targets that pair to form a valid vision target
        for i, left in leftTargets:
            for j, right in rightTargets:
                if left[1][0] < right[1][0] and util.approx(util.distance(left[1], right[1]), approxWidth*4, error=0.2) and inRange(abs(left[1][1]-right[1][1]), 0, 5):
                    targetPairs.append((left, right))

        # Calculate center of vision target by drawing diagonals
        centers = list(filter(lambda c : c != 0, [util.midpoint(left[1], right[1]) for left, right in targetPairs]))
        if centers:
            for c in centers:
                cv.circle(frame, c, 2, (0, 255, 0), -1)
                cv.putText(frame, "X", (c[0]+3, c[1]+3), cv.FONT_HERSHEY_PLAIN, 1, (0, 255, 0))

            outputStream.putFrame(frame)
            return centers

    outputStream.putFrame(frame)
    return 0


########## MAIN ##########

if __name__ == "__main__":
    if len(sys.argv) >= 2:
        configFile = sys.argv[1]

    # read configuration
    if not readConfig():
        sys.exit(1)

    # start NetworkTables
    ntinst = NetworkTablesInstance.getDefault()

    if server:
        print("Setting up NetworkTables server")
        ntinst.startServer()
    else:
        print("Setting up NetworkTables client for team {}".format(team))
        #ntinst.startClientTeam(team)
        ntinst.initialize(server=ntServerIpAddress)

    # setup a cvSource
    cs = CameraServer.getInstance()

    # Returns a cscore.VideoSource, used to automatically start a mjpegstream on port 1181
    camera = cs.startAutomaticCapture(name=cameraConfigs[0].name, path=cameraConfigs[0].path)

    # VideoMode.PixelFormat.kMJPEG, kBGR, kGray, kRGB565, kUnknown, kYUYV
    camera.setVideoMode(VideoMode.PixelFormat.kYUYV, width, height, fps)
    # camera.setResolution(width, height)
    # camera.setFPS(10)

    # Load camera properties config
    camera.setConfigJson(json.dumps(config))

    # Get a CvSink. This will capture images from the camera
    cvSink = cs.getVideo()

    # Let the camera initialize/warm up
    time.sleep(2.0)

    # Preallocate a numpy empty array
    img = np.zeros(shape=(processHeight, processWidth, 3), dtype=np.uint8)

    # (Optional) setup a CvSource. This will send images back to the Dashboard
    # Useful to see output from any image processing
    # outputStream = cs.putVideo("NameOfStream", processWidth, processHeight) 

    kernel = np.ones((5,5),np.uint8)

    # Target params
    targetOutputStream = cs.putVideo("TargetDetection", processWidth, processHeight)
    minTargetHSV = np.array([70, 170, 100])
    maxTargetHSV = np.array([75, 255, 255])

    while 1:
        time2, frame = cvSink.grabFrame(img)
        frame = cv.resize(frame, (processWidth, processHeight))
        frame = cv.cvtColor(frame, cv2.COLOR_BGR2HSV)

        findTargetTop(frame, minTargetHSV, maxTargetHSV, kernel, ntinst.getTable("TargetDetection"), targetOutputStream)