Font Converter
===================

a javafx gui for **[google sfntly](https://github.com/googlei18n/sfntly)** to help batch subset ttf fonts , convert to ttf/woff/eot.

![screenshot](img/screenshot.png)

Download Install
============

* install java 8+
* download from https://github.com/Jijun/font-converter-gui/releases/download/v1.0.1/font-converter-gui-1.0.1-jfx.jar 


Build Requirements
============
* Maven 3.5 (older versions might work too)
* Java Developer Kit 8 with at least Update 40

OS-specific requirements
========================
* (Windows) EXE installers: Inno Setup
* (Windows) MSI installers: WiX (at least version 3.7)
* (Linux) DEB installers: dpkg-deb
* (Linux) RPM installers: rpmbuild
* (Mac) DMG installers: hdiutil
* (Mac) PKG installers: pkgbuild

Build
=====
* build native package

```
 mvn jfx:native
 cd target/jfx/native
 ```
