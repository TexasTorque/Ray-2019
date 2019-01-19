import cv2 as cv
import numpy as np

capture = cv.VideoCapture(0)

while True:
    # Take each frame
    _, frame = capture.read()

    # Convert BGR to HSV
    hsv = cv.cvtColor(frame, cv.COLOR_BGR2HSV)

    # Define range of yellow color in HSV
    lower = np.array([20, 150, 100])
    upper = np.array([30, 255, 255])

    # Threshold the HSV image to get only blue colors
    mask = cv.inRange(hsv, lower, upper)
    ret, filtered = cv.threshold(cv.blur(mask, (5, 5)), 0, 255, cv.THRESH_BINARY+cv.THRESH_OTSU)

    # Contours
    contours, hierarchy = cv.findContours(filtered, cv.RETR_TREE, cv.CHAIN_APPROX_SIMPLE)

    result = cv.bitwise_and(frame, frame, mask=filtered)

    if contours:
        areas = [(i, cv.contourArea(cnt)) for i, cnt in enumerate(contours)]
        
        for area in areas:
            if area[1] > 10000:
                cv.putText(frame, str(area[1]), (0, 10), cv.FONT_HERSHEY_PLAIN, 1, (0, 255, 0))
                cv.drawContours(frame, [contours[area[0]]], 0, (0,255,0), 3)
                # box = cv.boxPoints(cv.minAreaRect(contours[0]))
                # box = np.int0(box)
                # cv.drawContours(frame, [box], 0, (0, 255, 0), 2)
                break

    cv.imshow('Result', np.hstack([frame, result]))

    if cv.waitKey(5) & 0xFF == 27:
        break

cv.destroyAllWindows()