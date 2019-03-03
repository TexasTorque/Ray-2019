# TexasTorque2019 Innovation In Control
Texas Torque is proud to present Ray, our robot for the 2019 FIRST Robotics Competition, *Destination: Deep Space*. This season, the 2019 Texas Torque programming team endeavored to incorporate new and powerful features in the software control system for Ray. Here's what we have accomplished.

## Features

### Basics
Texas Torque software has historically been written in Java, and this year is no exception. Because Java is taught in the AP Computer Science course, it is the most familiar language to us.

Throughout each match, Ray operates based on several defined states that are controlled by the driver. The main states are AUTO, TELEOP, and VISION.

Ray's software also receives feedback from many sources:
* Encoders are used on all motors whose positions are important
* Ultrasonics act as invisible bumpers to slow the robot before colliding into obstacles
* Gyroscope provides 3-axis angle measurements that are crucial for many operations
* NetworkTables enable communication between the RoboRIO and a Raspberry Pi

Many of the more advanced software features (to be described later) in Ray are made possible by thse sources of feedback.

### TorqueAuto
This season, we redesigned our autonomous library so that we can easily program complex robot maneuvers during the sandstorm period. 

The smallest unit of TorqueAuto is the COMMAND. Each command is designed to control the action of one subsystem and is terminated by an end condition. Multiple COMMANDS may be placed in a BLOCK, and several BLOCKS may be chained to form a SEQUENCE. At run time, all the COMMANDS in one BLOCK are executed simultaneously, and once all COMMANDS within a block are terminated, the next block begins. COMMANDS may also be delayed within each BLOCK for increased flexibility.

**(Insert diagram here)**

The MANAGER is responsible for selecting the SEQUENCE to be used before each match and running it. All SEQUENCES are synchronized with the base loop, which simplifies the process of programming motion profiles.

Last but not least, we have implemented the Pathfinder library as a COMMAND in TorqueAuto. The ability to generate spline paths onboard and follow them gives Ray the advantages of speed and precision during sandstorm.

### Vision
This year, we invested a lot of time in learning and implementing vision processing. The goal is to enable Ray to make automatic horizontal adjustments when placing hatch panels.

Our setup consists of a ring light and a webcam connected to a Raspberry Pi 3. The Pi is configured to run our own OpenCV-Python vision program, which not only sends a JPEG stream of the camera view, but also puts output values on NetworkTables to be read by the RoboRIO as feedback.

The logic of our vision program is as follows:
1. Isolate vision target shapes from the frame via HSV thresholding and contour areas
2. For each shape, analyze its coordinates to determine if it is left or right
3. Group pairs of left and right shapes to form a whole target
4. Calculate the center x-coordinate of the target, which is used for robot alignment

### Control Loop
We use our own PID library for most control loops. Our library supports the declaration of variable kp, ki, and kd coefficients based on error regions, which allows for greater precision.

We have put our PID library to many uses:
* The climber subsystem, which is capable of putting Ray on habitat level 3, uses pitch angle of the gyroscope as the process variable for a P controller. This helps the robot stay level as it is rising.
* The lift subsystem uses a PI controller with two sets of coefficients to help counteract gravity. During a match, the operator presses buttons to quickly move the lift to setpoints, and the setpoints can be manually adjusted using joysticks
* The rotary of the intake subsystem is implemented in a similar way to the lift

### TorqueLib
TorqueLib, written by Gijs Landwehr, has provided core functionality and utilities for Texas Torque software since 2015. 

TorqueLib includes the TorqueIterative robot base class that is used for Ray. It is similar to IterativeRobot, but implements a second Continuous thread that is scheduled to run at 100 Hz. 

## Contributors
Mentors: William Vo, Jacob Lubecki

Programmers: Daniel Zou, Swathi Mannem, Jacob Carter, Jimmy Harvin