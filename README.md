# simplevsm
Simple application to send Vital Signs from the BioHarness to a Server using an Android phone as a bridge for the Zephyr API

This project consist of 3 main classes:
1. [VitalSignMonitorActivty](app/src/main/java/andresperezl/com/vitalsignmonitor/VitalSignMonitorActivity.java): The main activity that will display a list of which device to connect to.
2. [BioharnessListener](app/src/main/java/andresperezl/com/vitalsignmonitor/BioHarnessListener.java): The class in charge of manipulating the packets coming from the BioHarness sensor.
3. [VitalSignsHandler](app/src/main/java/andresperezl/com/vitalsignmonitor/VitalSignsHandler.java): This class get the packets generated from the BioHarnessListener, and send them to the webserver.
