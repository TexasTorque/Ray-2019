import cv2 as cv
import numpy as np

WIDTH = 320
HEIGHT = 240

########## VISION LOGIC ##########

def findTarget(frame, mask):
    # Draw contours
    contours, hierarchy = cv.findContours(mask, cv.RETR_TREE, cv.CHAIN_APPROX_SIMPLE)

    if contours:
        # Construct dictionary that maps indices to contour areas, then discard areas that are too small.
        areas = {i: cv.contourArea(cnt) for i, cnt in enumerate(contours)}
        areas = minValue(areas, 200)
        if not areas:
            return 0

        # Construct rectangular boxes around each contour and store their coordinates in a dictionary.
        boxes = {}
        for i, area in areas.items():
            box = cv.boxPoints(cv.minAreaRect(contours[i]))
            boxes[i] = list(box)

        # For each box, find top two and bottom coordinates. If ratio of side edge to top edge is about right, then confirm box is a target and analyze if it is a left or right target. Add all targets into a dictionary.
        targets = {}
        approxWidth = 0
        for i, box in boxes.items():
            box = sorted(box, key=lambda b : b[1])
            top1 = box[0]
            top2 = box[1]
            bottom = box[3]

            if inRange(distance(top2, bottom) / distance(top1, top2), 2, 5):
                cv.drawContours(frame, [np.int0(boxes[i])], 0, (0, 255, 0), 2)
                cv.circle(frame, (top1[0], top1[1]), 2, (255, 0, 0), -1)
                cv.circle(frame, (top2[0], top2[1]), 2, (0, 0, 255), -1)
                cv.circle(frame, (bottom[0], bottom[1]), 2, (0, 0, 255), -1)
                approxWidth += distance(top1, top2)

                if top1[0] < top2[0]:
                    targets[i] = ('L', top2, bottom)
                elif top1[0] > top2[0]:
                    targets[i] = ('R', top2, bottom)
                else:
                    targets[i] = ('X')

        # Calculate average width of targets. Separate list of targets by left or right targets.
        approxWidth /= len(boxes)
        targetPairs = []
        leftTargets = [(i, target) for i, target in targets.items() if target[0] == 'L']
        rightTargets = [(i, target) for i, target in targets.items() if target[0] == 'R']

        # Find pairs of left and right targets that pair to form a valid vision target
        for i, left in leftTargets:
            for j, right in rightTargets:
                if left[1][0] < right[1][0] and approx(distance(left[1], right[1]), approxWidth*4, error = 0.2):
                    targetPairs.append((left, right))

        # Calculate center of vision target by drawing diagonals
        centers = list(filter(lambda c : c != 0, [centerPoint(left[1], left[2], right[1], right[2]) for left, right in targetPairs]))
        if centers:
            for c in centers:
                cv.circle(frame, c, 2, (0, 255, 0), -1)
                cv.putText(frame, 'A', c, cv.FONT_HERSHEY_PLAIN, 1, (0, 255, 0))
            return centers
    return 0

def findHatch():
    pass

def findCargo():
    pass


########## UTIL ##########

def adjustGamma(image, gamma=1.0):
	# build a lookup table mapping the pixel values [0, 255] to
	# their adjusted gamma values
	invGamma = 1.0 / gamma
	table = np.array([((i / 255.0) ** invGamma) * 255
		for i in np.arange(0, 256)]).astype("uint8")
 
	# apply gamma correction using the lookup table
	return cv.LUT(image, table)

def minValue(dict, minVal):
    return {i: value for i, value in dict.items() if value > minVal}

def outliers(dict, minVal=100, s=2):
    mean = np.mean(list(dict.values()))
    std = np.std(list(dict.values()))
    return {i: value for i, value in dict.items() if value-mean > s*std and value > minVal}

def slope(tuple1, tuple2):
    if tuple1[0] == tuple2[0]:
        return 0
    return (tuple2[1] - tuple1[1]) / (tuple2[0] - tuple1[0])

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

def inRange(num, lower, upper):
    return num >= lower and num <= upper


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
        # frame = adjustGamma(frame, gamma=0.7)
        hsv = cv.cvtColor(frame, cv.COLOR_BGR2HSV)

        # Green LED on refletive tape
        # lowerTarget = np.array([45, 0, 245])
        # upperTarget = np.array([55, 10, 255])

        # Good webcam, blue
        lowerTarget = np.array([90, 150, 80])
        upperTarget = np.array([105, 255, 255])

        # Crappy webcam, blue
        lowerTarget = np.array([90, 60, 120])
        upperTarget = np.array([110, 90, 155])

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