#!/bin/bash

#-----------------Unzip JCBuildEnv_2.1 directory here--------------#
unzip BuildEnv
buildDir="./JCBuildEnv_2.1"
#------------------------------------------------------------------#

#==================================================================#

echo "|-----------Starting Facebook Applet Build Script-----------|"
check=${buildDir:?"No Javacard Build Directory given"}
#Setup temporary build directory
mkdir    "temp"
mkdir -p "temp/build/classes"
mkdir -p "temp/export/com/simulity/sms/javacard"
mkdir -p "temp/export/com/simulity/fbaplt/javacard/"


######## Build sms applet 
echo "|--------------------Building Key Container Applet----------|"
#compile source
javac -g -classpath "${buildDir}/lib/api21.jar:${buildDir}/tools/interop/expclass/export_standard/sim_api_0319.jar" -target 1.1 -source 1.3 -sourcepath "./src" -d "./temp/build/classes" "./src/main/java/com/simulity/javacard/fortuneapplet/FortuneApplet.java"
java -classpath "${buildDir}/lib/converter.jar" com.sun.javacard.converter.Converter -i -exportpath "${buildDir}/api21_export_files:${buildDir}/tools/interop/expclass/export_standard" -out JCA EXP -classdir "./temp/build/classes" -d "./temp/build/applet" -applet 0x4D:0x75:0x6C:0x74:0x69:0x49:0x4D:0x53:0x49:0x5F:0x45:0x4B:0xAE:0x55:0x55:0x5B com.simulity.javacard.fortuneapplet.FortuneApplet com.simulity.javacard.fortuneapplet 0x4D:0x75:0x6C:0x74:0x69:0x49:0x4D:0x53:0x49:0x5F:0x45:0x4B:0xAE 1.0
cp "./temp/build/applet/com/simulity/javacard/fasw.jca" "./temp/build/applet"
#copy export files to temp
cp "./temp/build/applet/com/simulity/javacard/fasw.exp" "./temp/export/com/simulity/javacard/fasw.exp" 
cp -r "./temp/build/classes/com" "./temp/export"
#make cap file
java -classpath "${buildDir}/lib/converter.jar" com.sun.javacard.jcasm.cap.Main -o "./temp/build/applet/fasw.cap" "./temp/build/applet/com/simulity/javacard/fortuneapplet/javacard/fortuneapplet.jca"
java -classpath "${buildDir}/tools/jctools.jar" com.slb.javacard.jctools.Cap.makecap "./temp/build/applet/fasw.cap"
#copy cap file to build
cp "./temp/build/applet/fasw.cap" "./build/fasw.cap"
rm -r "temp/build/classes" #cleanup classes directory
rm -rf $buildDir
rm -rf "./temp"
