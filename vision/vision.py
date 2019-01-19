import cv2 as cv
import numpy as np

capture = cv.VideoCapture(1)

while True:
    # Take each frame
    _, frame = capture.read()

    # Convert BGR to HSV
    hsv = cv.cvtColor(frame, cv.COLOR_BGR2HSV)

    # Define range of stripes in HSV
    lower_target = np.array([30, 100, 200])
    upper_target = np.array([40, 255, 255])

    # Threshold the HSV image to get only desired color
    mask_target = cv.inRange(hsv, lower_target, upper_target)
    ret, filter_target = cv.threshold(cv.blur(mask_target, (5, 5)), 0, 255, cv.THRESH_BINARY+cv.THRESH_OTSU)

    # Contours
    contours, hierarchy = cv.findContours(filter_target, cv.RETR_TREE, cv.CHAIN_APPROX_SIMPLE)

    result = cv.bitwise_and(frame, frame, mask=filter_target)

    # Find target
    if contours:
        areas = [(i, cv.contourArea(cnt)) for i, cnt in enumerate(contours)]
        found = 0
        for area in areas:
            if area[1] > 1000:
                cv.drawContours(frame, [contours[area[0]]], 0, (0,255,0), 3)
                found += 1
            if found == 2:
                break
            

    if contours:
        areas = [(i, cv.contourArea(cnt)) for i, cnt in enumerate(contours)]
        
        for area in areas:
            if area[1] > 1000:
                cv.putText(frame, str(area[1]), (0, 10), cv.FONT_HERSHEY_PLAIN, 1, (0, 255, 0))
                cv.drawContours(frame, [contours[area[0]]], 0, (0,255,0), 3)
                # box = cv.boxPoints(cv.minAreaRect(contours[0]))
                # box = np.int0(box)
                # cv.drawContours(frame, [box], 0, (0, 255, 0), 2)
                break

    cv.imshow('Sinks', np.hstack([frame, result]))

    if cv.waitKey(5) & 0xFF == 27:
        break

cv.destroyAllWindows()