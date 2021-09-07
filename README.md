# Webcam Painting
Utilizes a region-growing algorithm for webcam drawing, using any object as a "paintbrush."

## Execution
Simply run CamPaint.java's main method. Clicking 'w' will display the live webcam. Clicking 'r' will display the recolored image. Clicking 'p' will enable painting (mouse-click an object on the screen to demarcate it as the paintbrush).

## Overview
This program uses a region-growing algorithm (specifically, flood-fill) to enable webcam painting. The goal is for the user to be able to use any (uniformly colored) object as a paintbrush, so that moving this "paintbrush" paints the webcam. The challenging task is detecting the paintbrush. The program accomplishes this by recognizing regions composed of distinct, uniform colors (using region-growing). This allows mouse-clicking one point on the paintbrush to identify the entire paintbrush object.

## Dependencies
[JavaCV](https://github.com/bytedeco/javacv), a Java wrapper of OpenCV.
