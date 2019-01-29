#!/usr/bin/env python3
#----------------------------------------------------------------------------
# Copyright (c) 2018 FIRST. All Rights Reserved.
# Open Source Software - may be modified and shared by FRC teams. The code
# must be accompanied by the FIRST BSD license file in the root directory of
# the project.
#----------------------------------------------------------------------------

#----------------------------------------------------------------------------
# 
# This script is a modified version of the python example found on the FRCVision-rPi interface. 
# Currently stripped to only retrieve camera on /dev/video0
# 
# FRCVision-rPi: http://wpilib.screenstepslive.com/s/currentCS/m/85074/l/1027798-the-raspberry-pi-frc-console
# Examples on github: https://github.com/robotpy/examples
#
# sftp://pi:raspberry@10.14.77.19/home/pi/
# 
# If mDNS is working, ping frcvision.local -4 to get the IPv4 address
# To browse the camera stream, go to http://frcvision.local or http://10.14.17.19
# We will want to set a static ip once the rPi is on the rio. mDNS is slow and unreliable
#
# This image automatically kicks off /home/pi/runCamera upon startup. 
# This can be modified to execute another script.
# 
# For developing, go to the web infterface and do the following:
# # Vision Status -> Down to kill runCamera
# # Click the "Writable" button at the top. By default, the rPi boots up in read-only mode
#
# Write: /bin/mount -o remount,rw / && /bin/mount -o remount,rw /boot
# Read-Only: /bin/mount -o remount,ro / && /bin/mount -o remount,ro /boot
#
# To upload files to the rPi, use WinSCP and connect to frcvision.local or the IP.
# If uploading a new script, make sure to chmod to 0755 (set properties on the doc).
# 
# Modify /home/pi/runInteractive and point it at your script.
# Then in putty, ./runInteractive
# 
# For production, update ./runCamera to point at your script
#
# Network Tables
# 
# The RoboRio should always be the "server" and any app or device should be running in "client" mode.
# Although the FRCVision-rPi can emulate server mode, an easier way is to load up 
# Outline Viewer (part of the FRC 2019 suite) and run it as a server.
# Then update the script to point at your IP address as the server.
#
# SmartDashboard
#
# Every cvSink that is setup can accessed via the browser or linked to directly as a mjpgstream in SmartDashboard
# http://10.14.77.19:1181 will be the default stream, 1182, 1183, 1184, etc, for every additional cvSink
# http://10.14.77.19:1181/stream.mjpg - added these streams to SmartDashboard
#
#----------------------------------------------------------------------------

import json
import time
import sys
import cv2
import datetime
import numpy as np

from cscore import CameraServer, VideoSource, VideoMode
#from cscore import *
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
#               "pixel format": <"MJPEG", "YUYV", etc>   // optional
#               "width": <video mode width>              // optional
#               "height": <video mode height>            // optional
#               "fps": <video mode fps>                  // optional
#               "brightness": <percentage brightness>    // optional
#               "white balance": <"auto", "hold", value> // optional
#               "exposure": <"auto", "hold", value>      // optional
#               "properties": [                          // optional
#                   {
#                       "name": <property name>
#                       "value": <property value>
#                   }
#               ]
#           }
#       ]
#   }

configFile = "/boot/frc.json"

class CameraConfig: pass

team = 1477
# ntServerIpAddress = "192.168.253.41"
ntServerIpAddress = "10.14.77.2"
server = False
cameraConfigs = []
printDebugging = False
width = 320
height = 240
fps = 15
processingScale = 2
processingWidth = int(width/processingScale)
processingHeight = int(height/processingScale)
xOffset = 160
yOffset = 240
showAllReturnedObjects = False

# Weird bug where brightness would not readjust in different lighting conditions
config = {"properties":[
    {"name":"connect_verbose","value":1},
    {"name":"raw_brightness","value":100},
    {"name":"brightness","value":randint(20,40)}, 
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
    {"name":"exposure_absolute","value":30},
    {"name":"pan_absolute","value":0},
    {"name":"tilt_absolute","value":0},
    {"name":"zoom_absolute","value":0}]}

"""Report parse error."""
def parseError(str):
    print("config error in '" + configFile + "': " + str, file=sys.stderr)

"""Read single camera configuration."""
def readCameraConfig(config):
    cam = CameraConfig()

    # name
    try:
        cam.name = config["name"]
    except KeyError:
        parseError("could not read camera name")
        return False

    # path
    try:
        cam.path = config["path"]
    except KeyError:
        parseError("camera '{}': could not read path".format(cam.name))
        return False

    cam.config = config

    cameraConfigs.append(cam)
    return True

"""Read configuration file."""
def readConfig():
    global team

    # parse file
    try:
        with open(configFile, "rt") as f:
            j = json.load(f)
    except OSError as err:
        print("could not open '{}': {}".format(configFile, err), file=sys.stderr)
        return False

    # top level must be an object
    if not isinstance(j, dict):
        parseError("must be JSON object")
        return False

    # team number
    try:
        team = j["team"]
    except KeyError:
        parseError("could not read team number")
        return False

    # ntmode (optional)
    # if "ntmode" in j:
    #     str = j["ntmode"]
    #     if str.lower() == "client":
    #         server = False
    #     elif str.lower() == "server":
    #         server = True
    #     else:
    #         parseError("could not understand ntmode value '{}'".format(str))

    # cameras
    try:
        cameras = j["cameras"]
    except KeyError:
        parseError("could not read cameras")
        return False
    for camera in cameras:
        if not readCameraConfig(camera):
            return False

    return True

"""Start running the camera."""
def startCamera(config):
    print("Starting camera '{}' on {}".format(config.name, config.path))
    camera = CameraServer.getInstance() \
        .startAutomaticCapture(name=config.name, path=config.path)

    camera.setConfigJson(json.dumps(config.config))
    print(config.config)

    return camera

class HSV:
    hmin = 0
    hmax = 0
    smin = 0
    smax = 0
    vmin = 0
    vmax = 0

    def __init__(self, hmin, hmax, smin, smax, vmin, vmax):
        self.hmin = hmin
        self.hmax = hmax
        self.smin = smin
        self.smax = smax
        self.vmin = vmin
        self.vmax = vmax

class HLS:
    hmin = 0
    hmax = 0
    smin = 0
    smax = 0
    lmin = 0
    lmax = 0

    def __init__(self, hmin, hmax, smin, smax, lmin, lmax):
        self.hmin = hmin
        self.hmax = hmax
        self.smin = smin
        self.smax = smax
        self.lmin = lmin
        self.lmax = lmax

class HoughCircleParams():
    dp = 0
    min_dist = 0
    param1 = 0
    param2 = 0
    min_radius = 0
    max_radius = 0

    def __init__(self, dp, min_dist, param1, param2, min_radius, max_radius):
        self.dp = dp
        self.min_dist = min_dist
        self.param1 = param1
        self.param2 = param2
        self.min_radius = min_radius
        self.max_radius = max_radius

def CargoDetection(cvSink, cs, frame, nt, outputStream, hsvParams, params, kernel):
    hsv = cv2.cvtColor(frame,cv2.COLOR_BGR2HSV)
    hue, sat, val = cv2.split(hsv)
    
    # Apply thresholding
    hthresh = cv2.inRange(np.array(hue),np.array(hsvParams.hmin),np.array(hsvParams.hmax))
    sthresh = cv2.inRange(np.array(sat),np.array(hsvParams.smin),np.array(hsvParams.smax))
    vthresh = cv2.inRange(np.array(val),np.array(hsvParams.vmin),np.array(hsvParams.vmax))

    # AND h s and v
    tracking = cv2.bitwise_and(hthresh,cv2.bitwise_and(sthresh,vthresh))

    # Some morpholigical filtering
    dilation = cv2.dilate(tracking,kernel,iterations = 1)
    closing = cv2.morphologyEx(dilation, cv2.MORPH_CLOSE, kernel)
    closing = cv2.GaussianBlur(closing,(5,5),0)

    # Detect circles using HoughCircles
    # circles = cv2.HoughCircles(closing,cv2.HOUGH_GRADIENT,2,120,param1=120,param2=30,minRadius=5,maxRadius=0)
    # circles = cv2.HoughCircles(closing,cv2.HOUGH_GRADIENT,1.4,50,param1=120,param2=30,minRadius=5,maxRadius=0)
    circles = cv2.HoughCircles(closing, cv2.HOUGH_GRADIENT, params.dp, params.min_dist,
        param1=params.param1, param2=params.param2, minRadius=params.min_radius, maxRadius=params.max_radius)

    # circles = np.uint16(np.around(circles))
  
    if circles is not None:
        if showAllReturnedObjects:
            if circles is not None:
                for i in circles[0,:]:
                    # If the ball is far, draw it in pink
                    if int(round(i[2])) < 30:
                        cv2.circle(frame, (int(round(i[0])), int(round(i[1]))), int(round(i[2])), (255,0,255), 5)
                        cv2.circle(frame, (int(round(i[0])), int(round(i[1]))), 2, (255,0,255, 10))
                    # else draw it in green
                    elif int(round(i[2])) > 30:
                        cv2.circle(frame, (int(round(i[0])), int(round(i[1]))), int(round(i[2])), (0,255,0), 5)
                        cv2.circle(frame, (int(round(i[0])), int(round(i[1]))), 2, (0,255,0), 10)
        else:
            circles2 = sorted(circles[0],key=lambda x:x[2],reverse=True)
            circle = circles2[0]
            if circle[2] > 30:
                cv2.circle(frame, (int(round(circle[0])), int(round(circle[1]))), int(round(circle[2])), (0,255,0), 5)
                cv2.circle(frame, (int(round(circle[0])), int(round(circle[1]))), 2, (0,255,0), 10)
                nt.putNumber("x", int(circle[0]) - int(xOffset))
                nt.putNumber("y", abs(int(circle[0]) - int(yOffset)))

    outputStream.putFrame(frame)
    
def HatchDetection(cvSink, cs, frame, nt, lower, upper, outputStream):
    # Convert BGR to HSV
    hsv = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)

    # Threshold the HSV image to get only blue colors
    mask = cv2.inRange(hsv, lower, upper)
    ret, filtered = cv2.threshold(cv2.blur(mask, (5, 5)), 0, 255, cv2.THRESH_BINARY+cv2.THRESH_OTSU)

    # Contours
    _, contours, hierarchy = cv2.findContours(filtered, cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)

    result = cv2.bitwise_and(frame, frame, mask=filtered)

    if contours:
        areas = [(i, cv2.contourArea(cnt)) for i, cnt in enumerate(contours)]
        
        for area in areas:
            if area[1] > 200:
                cv2.putText(frame, str(area[0]), (0, 10), cv2.FONT_HERSHEY_PLAIN, 1, (0, 255, 0))
                cv2.drawContours(frame, [contours[area[0]]], 0, (0,255,0), 3)
                # box = cv.boxPoints(cv.minAreaRect(contours[0]))
                # box = np.int0(box)
                # cv.drawContours(frame, [box], 0, (0, 255, 0), 2)
                break

    # cv.imshow('Result', np.hstack([frame, result]))
    outputStream.putFrame(frame) 

def LineDetection(cvSink, cs, frame, nt, outputStream, hls, min_area):
    hlsVals = cv2.cvtColor(frame,cv2.COLOR_BGR2HLS)
    hue, lig, sat = cv2.split(hlsVals)
    kernel = np.ones((5,5),np.uint8)

    # Apply thresholding
    hthresh = cv2.inRange(np.array(hue),np.array(hls.hmin),np.array(hls.hmax))
    lthresh = cv2.inRange(np.array(lig),np.array(hls.lmin),np.array(hls.lmax))
    sthresh = cv2.inRange(np.array(sat),np.array(hls.smin),np.array(hls.smax))            

    # AND h l and s
    tracking = cv2.bitwise_and(hthresh,cv2.bitwise_and(sthresh,lthresh))

    # Some morpholigical filtering
    dilation = cv2.dilate(tracking,kernel,iterations = 1)
    threshold = cv2.morphologyEx(dilation, cv2.MORPH_CLOSE, kernel)
    threshold = cv2.GaussianBlur(threshold,(5,5),0)

    _, contours, hierarchy = cv2.findContours(threshold, 1, 2)

    for contour in contours:
        # https://www.pyimagesearch.com/2016/02/08/opencv-shape-detection/
        
        approx = cv2.approxPolyDP(contour, 0.02 * cv2.arcLength(contour,True), cv2.CHAIN_APPROX_NONE)

        if len(approx) == 4:
            if showAllReturnedObjects:
                (x, y, w, h) = cv2.boundingRect(approx)
                aspect_ratio = w / float(h)

                if aspect_ratio < 0.95 or aspect_ratio > 1.05:

                    rect = cv2.minAreaRect(contour)
                    box = cv2.boxPoints(rect)
                    box = np.int0(box)
                    # cv2.drawContours(threshold, [box], 0, (0,255,0), 3)      
                    area = cv2.contourArea(contour)

                    if area > min_area:
                        # print("area: {}", area)
                        cv2.drawContours(frame, [box], 0, (0,255,0), 3)
                        
                        # if rect[1][0] < rect[1][1]:
                        #     print("top left point: {}, width, height: {}, angle of rotation: {}, area: {}".format(rect[0], rect[1], rect[2]-90.0, area))
                        # else:
                        #     print("top left point: {}, width, height: {}, angle of rotation: {}, area: {}".format(rect[0], rect[1], rect[2], area))
            else:
                c = max(contours, key = cv2.contourArea)
                area = cv2.contourArea(c)
                rect = cv2.minAreaRect(c)
                box = cv2.boxPoints(rect)
                box = np.int0(box)
                
                max_side = max(rect[1][0], rect[1][1])
                min_side = min(rect[1][0], rect[1][1])

                # img: 320x240 = 76800 / ##. tape should not be this large within image?
                if area > min_area and area < (processingHeight * processingWidth)/10 and abs(max_side/min_side) > 3.0:
                    # print('width: {}, height: {}, angle: {}, area: {}'.format(rect[1][0], rect[1][1], rect[2], area))
                    # sd.putString('center', str(rect[0][0]) + ',' + str(rect[0][1]))
                    nt.putNumber('width', rect[1][0])
                    nt.putNumber('height', rect[1][1])
                    nt.putNumber('angle', rect[2])

                    # https://namkeenman.wordpress.com/2015/12/18/open-cv-determine-angle-of-rotatedrect-minarearect/
                    # no real way of determining which way it is angled
                    # if width > height then tape is angled left (to the robot)
                    if rect[2] >= -15.0 or rect[2] <= -75.0:
                        nt.putString("tape_direction", "centered")
                    elif rect[1][1] > rect[1][0]:
                        nt.putString("tape_direction", "left")
                    else:
                        nt.putString("tape_direction", "right")
                    
                    cv2.drawContours(frame, [box], 0, (255,0,255), 3)
                else:
                    nt.putNumber('width', -1)
                    nt.putNumber('height', -1)
                    nt.putNumber('angle', -1)
                    nt.putString("tape_direction", "")
                    
    outputStream.putFrame(frame)

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

    # start cameras
    # cameras = []
    # for cameraConfig in cameraConfigs:
    #     cameras.append(startCamera(cameraConfig))

    # setup a cvSource
    cs = CameraServer.getInstance()

    # Returns a cscore.VideoSource, used to automatically start a mjpegstream on port 1181
    camera = cs.startAutomaticCapture(name=cameraConfigs[0].name, path=cameraConfigs[0].path) 
    #print("cameraConfig: .name: {}, cameraConfigs.path{}".format(cameraConfigs[0].name, cameraConfigs[0].path))

    #VideoMode.PixelFormat.kMJPEG, kBGR, kGray, kRGB565, kUnknown, kYUYV
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
    img = np.zeros(shape=(processingHeight, processingWidth, 3), dtype=np.uint8)

    # (optional) Setup a CvSource. This will send images back to the Dashboard
    # Useful to see output from any image processing
    # outputStream = cs.putVideo("NameOfStream", processingWidth, processingHeight) 

    # Cargo default params
    cargoOutputStream = cs.putVideo("Cargo Detection", processingWidth, processingHeight)  
    cargoHSV = HSV(0, 7, 120, 255, 160, 255,)
    cargoParams = HoughCircleParams(1.4, 50, 120, 30, 5, 0)
    kernel = np.ones((5,5),np.uint8)

    # hatchOutputStream = cs.putVideo("Hatch Detection", processingWidth, processingHeight)  
    # hsv_hatch_lower = np.array([20, 150, 100])
    # hsv_hatch_upper = np.array([30, 255, 255])

    lineOutputStream = cs.putVideo("Line Detection", processingWidth, processingHeight)   
    hls = HLS(0, 40, 0, 100, 225, 255)

    while True:
        time2, frame = cvSink.grabFrame(img)

        frame = cv2.resize(frame, (processingWidth, processingHeight))

        CargoDetection(cvSink, cs, frame, ntinst.getTable('CargoDetection'), cargoOutputStream, cargoHSV, cargoParams, kernel)
        
        LineDetection(cvSink, cs, frame.copy(), ntinst.getTable('LineDetection'), lineOutputStream, hls, 500.0)

        # HatchDetection(cvSink, cs, frame.copy(), ntinst.getTable('HatchDetection'), hsv_hatch_lower, hsv_hatch_upper, hatchOutputStream)