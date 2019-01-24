#--------------------------------------------------------------
#
# Modified example from
# https://www.youtube.com/watch?v=XFwi3xpphCs
# https://pastebin.com/nrh2xNNT
#
# Sample images, extract to images subfolder
# https://1drv.ms/u/s!AiflSitw5xTJ3j6o1ob8J64d3ZVB
#
#--------------------------------------------------------------

import cv2
import json
import os 
import datetime
import numpy as np
import time

width = 640
height = 480
processing_scale = 2
processing_width = int(width/processing_scale)
processing_height = int(height/processing_scale)
video_fps = 15 # 1-30
trackbar_factor = 10 # no floats in trackbar
frame_position = None
paused_frame_position = None

class Source:
    Photo = 1
    Video = 2

class HoughCircleParams():
    hmin = 0
    hmax = 0
    smin = 0
    smax = 0
    vmin = 0
    vmax = 0
    dp = 0
    min_dist = 0
    param1 = 0
    param2 = 0
    min_radius = 0
    max_radius = 0

    def __init__(self, hmin, hmax, smin, smax, vmin, vmax, dp, min_dist, param1, param2, min_radius, max_radius):
        self.hmin = hmin
        self.hmax = hmax
        self.smin = smin
        self.smax = smax
        self.vmin = vmin
        self.vmax = vmax
        self.dp = dp
        self.min_dist = min_dist
        self.param1 = param1
        self.param2 = param2
        self.min_radius = min_radius
        self.max_radius = max_radius

def nothing(x):
    pass

def onChange(trackbarValue):
    global paused_frame_position
    cap.set(cv2.CAP_PROP_POS_FRAMES, trackbarValue)

    frame_position = trackbarValue
    if cv2.getTrackbarPos('pause', 'tracking') == 1:
        paused_frame_position = trackbarValue

    # err, paused_frame = cap.read()

    # paused_frame = cv2.resize(paused_frame, (processing_width, processing_height))
    # cv2.imshow("tracking", paused_frame)
    pass

def process(frame, hue, sat, val, kernel, hmin, hmax, smin, smax, vmin, vmax, dp, mindist, param1, param2, minrad, maxrad):
    # Apply thresholding
    hthresh = cv2.inRange(np.array(hue),np.array(hmin),np.array(hmax))
    sthresh = cv2.inRange(np.array(sat),np.array(smin),np.array(smax))
    vthresh = cv2.inRange(np.array(val),np.array(vmin),np.array(vmax))

    # AND h s and v
    tracking = cv2.bitwise_and(hthresh,cv2.bitwise_and(sthresh,vthresh))

    # Some morpholigical filtering
    dilation = cv2.dilate(tracking,kernel,iterations = 1)
    closing = cv2.morphologyEx(dilation, cv2.MORPH_CLOSE, kernel)
    closing = cv2.GaussianBlur(closing,(5,5),0)

    # Detect circles using HoughCircles
    # circles = cv2.HoughCircles(closing,cv2.HOUGH_GRADIENT,2,120,param1=120,param2=30,minRadius=5,maxRadius=0)
    # circles = cv2.HoughCircles(closing,cv2.HOUGH_GRADIENT,1.4,50,param1=120,param2=30,minRadius=5,maxRadius=0)
    circles = cv2.HoughCircles(closing,cv2.HOUGH_GRADIENT,dp/trackbar_factor,mindist,param1=param1,param2=param2,minRadius=minrad,maxRadius=maxrad)

    # circles = np.uint16(np.around(circles))

    #Draw Circles
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
    
    return frame, hthresh, sthresh, vthresh, closing


def ballTracking(file, img, src_type, houghParams):
    global paused_frame_position
    global video_fps
    global cap 

    # Creating a windows for later use
    cv2.namedWindow('HueComp')
    cv2.namedWindow('SatComp')
    cv2.namedWindow('ValComp')
    cv2.namedWindow('closing')
    cv2.namedWindow('tracking')
    cv2.namedWindow('hough')

    kernel = np.ones((5,5),np.uint8)

    cv2.createTrackbar('hmin', 'HueComp', houghParams.hmin, 255, nothing)
    cv2.createTrackbar('hmax', 'HueComp', houghParams.hmax, 255, nothing)
    cv2.createTrackbar('smin', 'SatComp', houghParams.smin, 255, nothing)
    cv2.createTrackbar('smax', 'SatComp', houghParams.smax, 255, nothing)
    cv2.createTrackbar('vmin', 'ValComp', houghParams.vmin, 255, nothing)
    cv2.createTrackbar('vmax', 'ValComp', houghParams.vmax, 255, nothing)
    cv2.createTrackbar('dp', 'hough', int(houghParams.dp * trackbar_factor), 5 * trackbar_factor, nothing)
    cv2.createTrackbar('mindist', 'hough', houghParams.min_dist, 255, nothing)
    cv2.createTrackbar('param1', 'hough', houghParams.param1, 255, nothing)
    cv2.createTrackbar('param2', 'hough', houghParams.param2, 255, nothing)
    cv2.createTrackbar('minrad', 'hough', houghParams.min_radius, 255, nothing)
    cv2.createTrackbar('maxrad', 'hough', houghParams.max_radius, 255, nothing)

    if src_type == Source.Video:
        cap = cv2.VideoCapture(file)
        length = int(cap.get(cv2.CAP_PROP_FRAME_COUNT))
        cv2.createTrackbar('fps', 'tracking', video_fps, 30, nothing)
        cv2.createTrackbar('position', 'tracking', 0, length, onChange)       
        cv2.createTrackbar('pause', 'tracking', 0, 1, nothing)
        

    while(True):
        if src_type == Source.Video:
            video_paused = cv2.getTrackbarPos('pause', 'tracking')
        
        # print(str(cap.get(cv2.CAP_PROP_POS_FRAMES)) + '/' + str(cap.get(cv2.CAP_PROP_FRAME_COUNT)))

        if src_type == Source.Video and cap.get(cv2.CAP_PROP_POS_FRAMES) >= cap.get(cv2.CAP_PROP_FRAME_COUNT):
            # Restart video if at end
            cap.set(cv2.CAP_PROP_POS_FRAMES, 0)
        elif src_type == Source.Video and cap.grab() and video_paused == 0:
            if paused_frame_position is not None:
                cap.set(cv2.CAP_PROP_POS_FRAMES, paused_frame_position)
                paused_frame_position = None
            flag, frame = cap.retrieve()
        elif src_type == Source.Video and not cap.grab():
            break
        elif src_type == Source.Video and cap.grab() and video_paused == 1:
            # If video is paused, resume from paused position and keep reprocessing the paused frame
            if paused_frame_position is None:
                paused_frame_position = cap.get(cv2.CAP_PROP_POS_FRAMES)
            cap.set(cv2.CAP_PROP_POS_FRAMES, paused_frame_position)
            flag, frame = cap.retrieve()
        elif src_type == Source.Photo:
            frame = cv2.imread(file, cv2.COLOR_BGR2HSV)
            gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

        frame = cv2.resize(frame, (processing_width, processing_height))
        hsv = cv2.cvtColor(frame,cv2.COLOR_BGR2HSV)
        hue, sat, val = cv2.split(hsv)
    
        # get info from track bar and appy to result
        hmin = cv2.getTrackbarPos('hmin','HueComp')
        hmax = cv2.getTrackbarPos('hmax','HueComp')    
        smin = cv2.getTrackbarPos('smin','SatComp')
        smax = cv2.getTrackbarPos('smax','SatComp')    
        vmin = cv2.getTrackbarPos('vmin','ValComp')
        vmax = cv2.getTrackbarPos('vmax','ValComp')
        dp = cv2.getTrackbarPos('dp', 'hough')
        mindist = cv2.getTrackbarPos('mindist', 'hough')
        param1 = cv2.getTrackbarPos('param1', 'hough')
        param2 = cv2.getTrackbarPos('param2', 'hough')
        minrad = cv2.getTrackbarPos('minrad', 'hough')
        maxrad = cv2.getTrackbarPos('maxrad', 'hough')
        video_fps = cv2.getTrackbarPos('fps', 'tracking')

        frame, hthresh, sthresh, vthresh, closing = process(frame, hue, sat, val, kernel, hmin, hmax, 
            smin, smax, vmin, vmax, dp, mindist, param1, param2, minrad, maxrad)

        if video_fps > 0:
            time.sleep(1/video_fps)

        # Show the result in frames
        cv2.imshow('HueComp',hthresh)
        cv2.imshow('SatComp',sthresh)
        cv2.imshow('ValComp',vthresh)
        cv2.imshow('closing',closing)
        cv2.imshow('tracking',frame)

        k = cv2.waitKey(5) & 0xFF
        if k == 27:
            break


if __name__ == "__main__":
    img = np.zeros(shape=(processing_height, processing_width, 3), dtype=np.uint8)
    dir_path = os.path.dirname(os.path.realpath(__file__))
    
    # test_src = "\\images\\IMG_2886.jpg" #cargo
    # test_src = "\\images\\IMG_2904.jpg" #hatch
    # test_src = "\\images\\IMG_2911.jpg" #multiple cargo on field
    # test_src = "\\images\\IMG_2909.jpg" #cargo and hatch on field from far distance
    # test_src = "\\images\\IMG_2910.jpg" #cargo and hatch on field from far distance, angled
    # test_src = "\\images\\IMG_2914.jpg" #cargo and hatch on field from close distance
    # houghParams = HoughCircleParams(0, 25, 135, 255, 186, 255, 1.4, 50, 120, 30, 5, 0)
    # ballTracking(dir_path + test_src, img, Source.Photo, houghParams)

    # hmin_cargo = 0
    # hmax_cargo = 14
    # smin_cargo = 58
    # smax_cargo = 255
    # vmin_cargo = 160
    # vmax_cargo = 255

    # Cargo default params
    houghParams = HoughCircleParams(0, 7, 120, 255, 160, 255, 1.4, 50, 120, 30, 5, 0)

    # Hatch default params
    # houghParams = HoughCircleParams(14, 27, 135, 255, 186, 255, 1.4, 50, 120, 30, 5, 0)
    # test_src = "\\images\WIN_20190122_17_07_37_Pro.mp4" # random walk on field with ring light
    test_src = "\\images\\WIN_20190122_17_08_07_Pro.mp4" # video of whatever we have on the field
    # test_src = "\\images\WIN_20190122_17_36_24_Pro.mp4" # after corrections to field
    ballTracking(dir_path + test_src, img, Source.Video, houghParams)
    
    cv2.destroyAllWindows()