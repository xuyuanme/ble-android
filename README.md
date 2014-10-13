nRF Toolbox
===========

The source code for nRF Toolbox has been separated into 3 projects. The DFU service has been moved to a new library project which may now be easily
integrated with your application. To keep the Android support v4 library up-to-date we have created another project with just one jar file in
the libs folder.

The nrf-logger-v2.0 library has been moved to DFULibrary.
The android-support-v4 library has been moved to AndroidSupportLibrary.

Usage:
Import all projects: nRFToolbox, AndroidSupportLibrary and DFULibrary into Eclipse ADT. 
The projects should compile without any changes if Android 4.4 SDK (API 19) is installed.