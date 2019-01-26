#--------------------------------------------------------------
#
# Sample images, extract to images subfolder
# https://1drv.ms/u/s!AiflSitw5xTJ4Qh_MO-GWMFQwN2d
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
video_fps = 30 # 1-30
trackbar_factor = 10 # no floats in trackbar
frame_position = None
paused_frame_position = None

class Source:
    Photo = 1
    Video = 2

class lineParams():

    def __init__(self):
        pass

def nothing(x):
    pass

def onChange(trackbarValue):
    global paused_frame_position
    cap.set(cv2.CAP_PROP_POS_FRAMES, trackbarValue)

    frame_position = trackbarValue
    if cv2.getTrackbarPos('pause', 'tracking') == 1:
        paused_frame_position = trackbarValue
    pass

def lineDetection(file, img, src_type):
    global paused_frame_position
    global video_fps
    global cap 

    # Creating a windows for later use
    cv2.namedWindow('threshold')
    cv2.namedWindow('tracking')
    cv2.namedWindow('approx')
    cv2.createTrackbar('threshold', 'threshold', 200, 255, nothing)

    # kernel = np.ones((5,5),np.uint8)

    if src_type == Source.Video:
        cap = cv2.VideoCapture(file)
        length = int(cap.get(cv2.CAP_PROP_FRAME_COUNT))
        cv2.createTrackbar('fps', 'tracking', video_fps, 30, nothing)
        cv2.createTrackbar('position', 'tracking', 0, length, onChange)       
        cv2.createTrackbar('pause', 'tracking', 0, 1, nothing)

    

    while(True):
        if src_type == Source.Video:
            video_paused = cv2.getTrackbarPos('pause', 'tracking')
        
        # print("frame pos: {}, frame cnt: {}, paused_frame_position: {}".format(cap.get(cv2.CAP_PROP_POS_FRAMES), cap.get(cv2.CAP_PROP_FRAME_COUNT), paused_frame_position))

        if src_type == Source.Video and cap.get(cv2.CAP_PROP_POS_FRAMES) >= cap.get(cv2.CAP_PROP_FRAME_COUNT):
            # Restart video if at end
            cap.set(cv2.CAP_PROP_POS_FRAMES, 0)
            flag, frame= cap.retrieve()
        elif src_type == Source.Video and cap.grab() and video_paused == 1:
            # If video is paused, resume from paused position and keep reprocessing the paused frame
            if paused_frame_position is None:
                paused_frame_position = cap.get(cv2.CAP_PROP_POS_FRAMES)
            cap.set(cv2.CAP_PROP_POS_FRAMES, paused_frame_position)
            flag, frame= cap.retrieve()
        elif src_type == Source.Video and cap.grab() and video_paused == 0:
            if paused_frame_position is not None:
                cap.set(cv2.CAP_PROP_POS_FRAMES, paused_frame_position)
                paused_frame_position = None
            flag, frame= cap.retrieve()
        elif src_type == Source.Video and not cap.grab():
            print("frame pos: {}, frame cnt: {}, paused_frame_position: {}".format(cap.get(cv2.CAP_PROP_POS_FRAMES), cap.get(cv2.CAP_PROP_FRAME_COUNT), paused_frame_position))
            # cap.set(cv2.CAP_PROP_POS_FRAMES, cap.get(cv2.CAP_PROP_POS_FRAMES))
            print("I don't know why a frame can't be captured ¯\_(ツ)_/¯")
            break
        elif src_type == Source.Photo:
            frame= cv2.imread(file, cv2.COLOR_BGR2HSV)
            gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

        video_fps = cv2.getTrackbarPos('fps', 'tracking')
        threshold_val = cv2.getTrackbarPos('threshold', 'threshold')
        
        frame = cv2.resize(frame, (processing_width, processing_height))
        frame_gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
        
        ret, threshold = cv2.threshold(frame_gray, threshold_val, 255, cv2.THRESH_BINARY)
        
        contours, hierarchy = cv2.findContours(threshold, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_NONE)

        # cv2.drawContours(frame, contours, -1, (0,255,0), 3)
        # cv2.drawContours(threshold, contours, -1, (0,255,0), 3)

        for contour in contours:
            # https://www.pyimagesearch.com/2016/02/08/opencv-shape-detection/
            
            approx = cv2.approxPolyDP(contour, 0.04 * cv2.arcLength(contour,True), True)
            
            if approx is not None:
                approx_frame = cv2.drawContours(frame_gray, approx, 0, (0,255, 0), 3)

            if len(approx) == 4:
                (x, y, w, h) = cv2.boundingRect(approx)
                aspect_ratio = w / float(h)
                if aspect_ratio < 0.95 or aspect_ratio > 1.05:

                    rect = cv2.minAreaRect(contour)
                    box = cv2.boxPoints(rect)
                    box = np.int0(box)
                    # cv2.drawContours(threshold, [box], 0, (0,255,0), 3)      
                    area = cv2.contourArea(contour)
                    print("area: {}", area)

                    # https://namkeenman.wordpress.com/2015/12/18/open-cv-determine-angle-of-rotatedrect-minarearect/
                    # if rect[1][0] * rect[1][1] > 20.0:
                    if area > 15.0:
                        cv2.drawContours(frame, [box], 0, (0,255,0), 3)
                        
                        if rect[1][0] < rect[1][1]:
                            print("top left point: {}, width, height: {}, angle of rotation: {}, area: {}".format(rect[0], rect[1], rect[2]-90.0, area))
                        else:
                            print("top left point: {}, width, height: {}, angle of rotation: {}, area: {}".format(rect[0], rect[1], rect[2], area))
            # elif len(approx) == 3:
            #     print("triangle")
            # else:
            #     print("some other shape")

        if video_fps > 0:
            time.sleep(1/video_fps)

        # Show the result in frames
        cv2.imshow('threshold', threshold)
        cv2.imshow('approx', approx_frame)
        cv2.imshow('tracking', frame)

        k = cv2.waitKey(5) & 0xFF
        if k == 27:
            break


if __name__ == "__main__":
    img = np.zeros(shape=(processing_height, processing_width, 3), dtype=np.uint8)
    dir_path = os.path.dirname(os.path.realpath(__file__))

    # test_src = "\\images\\WIN_20190125_17_50_14_Pro.mp4"
    # test_src = "\\images\\WIN_20190125_17_50_46_Pro.mp4"
    # test_src = "\\images\\WIN_20190125_17_50_59_Pro.mp4"
    test_src = "\\images\\WIN_20190125_17_51_15_Pro.mp4"
    lineDetection(dir_path + test_src, img, Source.Video)
    
    cv2.destroyAllWindows()