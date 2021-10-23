# Driver-Monitoring-System

**Description -** 
Driver Monitoring System is an IOT based Project.
The project includes two applications - 
  1. On Driver Side - To track the driver activities which includes location tracking. 
  2. On Admin Side - To evaluate the activity of the driver which displays rating for a driver based on considered factors and formulae

The project also includes use of sensor like Camera to detect drowsiness.

**Working -**
When the driver starts his journey, he needs to click on the start journey on the app at his side. This event will trigger the code on Raspberry Pi, 
which results in the start of tracking activity.
The data of the tracking are pushed to Google Firebase and fetched in other app for evaluation.
The drowsiness of the driver is detected using the camera placed in the front of driver and mobile device is used to access GPS.
The app on the driver side also shows the source to destination route to the driver which is made possible used Google Api.

The project tracks only one driver for now.
Device used - Raspberry Pi, Mobile Device, Sensor - Camera
Technology used - Python, Android-Java
Development Environment - Android Studio

How to run the Project - 
1. Run the eyeYawn.py file from Raspberry Pi (It will start the drowsiness detection)
2. Open the Driver's mobile App and click on Start Journey and at the end of the journey click on End Journey.
3. Observe the Journey Status from Admin App.

