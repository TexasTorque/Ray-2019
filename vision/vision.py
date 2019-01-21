import cv2 as cv
import numpy as np

WIDTH = 320
HEIGHT = 240

########## VISION LOGIC ##########

def findTarget(frame, mask):
    ret, contours, hierarchy = cv.findContours(mask, cv.RETR_TREE, cv.CHAIN_APPROX_SIMPLE)

    if contours:
        areas = {i: cv.contourArea(cnt) for i, cnt in enumerate(contours)}
        areas = minValue(areas, 200)
        if not areas:
            return None

        boxes = {}
        for i, area in areas.items():
            box = cv.boxPoints(cv.minAreaRect(contours[i]))
            box = np.int0(box)
            boxes[i] = list(box)
            cv.drawContours(frame, [box], 0, (0, 255, 0), 2)

        approxWidth = 0
        for i, box in boxes.items():
            box = sorted(box, key=lambda b : b[1])
            top1 = box[0]
            cv.circle(frame, (top1[0], top1[1]), 2, (255, 0, 0), -1)
            top2 = box[1]
            cv.circle(frame, (top2[0], top2[1]), 2, (0, 0, 255), -1)
            bottom = box[3]
            cv.circle(frame, (bottom[0], bottom[1]), 2, (0, 0, 255), -1)
            approxWidth += distance(top1, top2)

            if top1[0] < top2[0]:
                boxes[i] = ('L', top2, bottom)
            elif top1[0] > top2[0]:
                boxes[i] = ('R', top2, bottom)
            else:
                boxes[i] = ('X')

        approxWidth /= len(boxes)
        targetPairs = []
        leftTargets = [(i, box) for i, box in boxes.items() if box[0] == 'L']
        rightTargets = [(i, box) for i, box in boxes.items() if box[0] == 'R']

        for i, left in leftTargets:
            for j, right in rightTargets:
                if left[1][0] < right[1][0] and approx(distance(left[1], right[1]), approxWidth*4, error = 0.2):
                    targetPairs.append((left, right))

        centers = list(filter(lambda c : c != 0, [centerPoint(left[1], left[2], right[1], right[2]) for left, right in targetPairs]))
        if centers:
            target = min(centers, key=lambda m : distance(m, (WIDTH/2, HEIGHT/2)))
            cv.circle(frame, target, 4, (0, 255, 0), -1)
            return target

def findHatch():
    pass

def findCargo():
    pass


########## UTIL ##########

def minValue(dict, minVal):
    return {i: value for i, value in dict.items() if value > minVal}

def outliers(dict, minVal=100, s=2):
    mean = np.mean(list(dict.values()))
    std = np.std(list(dict.values()))
    return {i: value for i, value in dict.items() if value-mean > s*std and value > minVal}

def distance(tuple1, tuple2):
    return ((tuple1[0] - tuple2[0])**2 + (tuple1[1] - tuple2[1])**2)**0.5

def midpoint(tuple1, tuple2):
    return ((tuple1[0] + tuple2[0]) // 2, (tuple1[1] + tuple2[1]) // 2)

def centerPoint(tupleA1, tupleA2, tupleB1, tupleB2):
    if tupleA1[0] == tupleB2[0] or tupleA2[0] == tupleB1[0]:
        return 0

    m1 = (tupleA1[1] - tupleB2[1]) / (tupleA1[0] - tupleB2[0])
    b1 = tupleA1[1] - m1*tupleA1[0]
    m2 = (tupleA2[1] - tupleB1[1]) / (tupleA2[0] - tupleB1[0])
    b2 = tupleA2[1] - m2*tupleA2[0]

    if m1 == m2:
        return 0
    else:
        x = int((b2 - b1) / (m1 - m2))
        y = int(m1 * x + b1)
        return (x, y)

def approx(num1, num2, error=0.05):
    return abs(num1-num2) / (num1+num2) <= error


########## MAIN ##########

def main():
    kernel = np.ones((5, 5), np.uint8)

    capture = cv.VideoCapture(0)
    capture.set(3, WIDTH)
    capture.set(4, HEIGHT)

    while 1:
        if cv.waitKey(5) & 0xFF == 27:
            break

        ret, frame = capture.read()

        hsv = cv.cvtColor(frame, cv.COLOR_BGR2HSV)

        lowerTarget = np.array([90, 150, 80])
        upperTarget = np.array([105, 255, 255])

        maskTarget = cv.inRange(hsv, lowerTarget, upperTarget)
        # maskTarget = cv.dilate(maskTarget, kernel, iterations = 1)
        maskTarget = cv.morphologyEx(maskTarget, cv.MORPH_CLOSE, kernel)
        # maskTarget = cv.GaussianBlur(maskTarget,(5,5),0)

        target = findTarget(frame, maskTarget)

        maskFrame = maskTarget
        cv.imshow('Frame', frame)
        cv.imshow('Masks', maskFrame)

    capture.release()
    cv.destroyAllWindows()

if __name__ == '__main__':
    main()