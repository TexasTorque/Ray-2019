import cv2 as cv
import numpy as np

def adjustGamma(image, gamma=1.0):
	# build a lookup table mapping the pixel values [0, 255] to
	# their adjusted gamma values
	invGamma = 1.0 / gamma
	table = np.array([((i / 255.0) ** invGamma) * 255
		for i in np.arange(0, 256)]).astype("uint8")
 
	# apply gamma correction using the lookup table
	return cv.LUT(image, table)

def limitValue(dict, minVal, maxVal):
    return {i: value for i, value in dict.items() if value >= minVal and value <= maxVal}

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
