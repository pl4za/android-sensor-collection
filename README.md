# Accelerometry-sensor-collection 
Records accelerometry data in a CSV file for information extraction with auto file upload capabilities.

********************************************************************************
This version POSTS user info and accelerometer data to:

http://193.136.67.246/~jason/sensorsDataTese/fileUpload.php

User information JSON is encrypted using RSA encryption (public key in phone / private key in server)
********************************************************************************

Developed for my Master's Thesis: "Towards an Integrated Solution to Physical Assessment of Students".

**Accelerometry collection**

Records accelerometry data in a CSV file for information extraction.
A sensor calibration is performed and required prior to usage.
The collected datasets can be used for activity recognition, energy expenditure estimation and other applications.

**User information**

User information is sent for energy expenditure calculations or other usages in JSON format.

**Data upload**

Data upload is started automatically if any files exist when conencting to WI-fi networks.

**Screenshots**

![Main screen during capture](http://i.imgur.com/8cZfNOm.png)
![Calibration step](http://i.imgur.com/kqGWti2.png)~
![Notification during collection](http://i.imgur.com/cjlz3v5.png)
![Drawer with user information](http://i.imgur.com/lyNWfFv.png)

# Installation
Import project into Android studio.

# License
GPLv3
