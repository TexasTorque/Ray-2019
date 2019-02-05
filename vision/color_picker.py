import cv2 as cv
import numpy as np

mousePos = (-1, -1)

def onClick(event, x, y, flags, param):
    global mousePos
    if event == cv.EVENT_LBUTTONDOWN:
        mousePos = (x, y)

def live():
    capture = cv.VideoCapture(1)
    cv.namedWindow('Color Picker')
    cv.setMouseCallback('Color Picker', onClick)

    while True:
        if cv.waitKey(5) & 0xFF == 27:
            break

        ret, frame = capture.read()
        cv.putText(frame, 'XY: ' + str(mousePos), (0, 10), cv.FONT_HERSHEY_PLAIN, 1, (0, 255, 0))
        
        if (mousePos[0] > -1 and mousePos[1] > -1):
            bgr = frame[mousePos[1], mousePos[0]]
            hsv = cv.cvtColor(frame, cv.COLOR_BGR2HSV)[mousePos[1], mousePos[0]]
            cv.putText(frame, 'BGR: ' + str(bgr), (0, 30), cv.FONT_HERSHEY_PLAIN, 1, (0, 255, 0))
            cv.putText(frame, 'HSV: ' + str(hsv), (0, 50), cv.FONT_HERSHEY_PLAIN, 1, (0, 255, 0))

        cv.circle(frame, mousePos, 2, (255, 255, 255), -1)
        cv.circle(frame, mousePos, 1, (0, 0, 0), -1)
        
        cv.imshow('Color Picker', frame)

    capture.release()
    cv.destroyAllWindows()

def image():
    # img = cv.imread('target.png')
    cv.namedWindow('Color Picker')
    cv.setMouseCallback('Color Picker', onClick)

    while True:
        if cv.waitKey(5) & 0xFF == 27:
            break

        img = cv.imread('target.png')
        
        cv.putText(img, 'XY: ' + str(mousePos), (0, 10), cv.FONT_HERSHEY_PLAIN, 1, (0, 255, 0))
        
        if (mousePos[0] > -1 and mousePos[1] > -1):
            bgr = img[mousePos[1], mousePos[0]]
            hsv = cv.cvtColor(img, cv.COLOR_BGR2HSV)[mousePos[1], mousePos[0]]
            cv.putText(img, 'BGR: ' + str(bgr), (0, 30), cv.FONT_HERSHEY_PLAIN, 1, (0, 255, 0))
            cv.putText(img, 'HSV: ' + str(hsv), (0, 50), cv.FONT_HERSHEY_PLAIN, 1, (0, 255, 0))

        cv.circle(img, mousePos, 2, (255, 255, 255), -1)
        cv.circle(img, mousePos, 1, (0, 0, 0), -1)
        
        cv.imshow('Color Picker', img)

    cv.destroyAllWindows()

if __name__ == "__main__":
    live()