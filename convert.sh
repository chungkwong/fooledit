#!/bin/sh
java_file_path=$1/javaFiles
class_file_path=$1/classFiles
#mode=`echo $1 | sed "s/\/.*\///"`
mkdir ${java_file_path}
mkdir ${class_file_path}
java -cp lib/antlr-4.7-complete.jar org.antlr.v4.Tool -o ${java_file_path} $1/*.g4
mv ${java_file_path}/*.tokens $1
javac -cp lib/antlr-4.7-complete.jar:${java_file_path} -d ${class_file_path} ${java_file_path}/*.java
jar cvf $1/parser.jar -C ${class_file_path} .
rm -rf ${class_file_path}
rm -rf ${java_file_path}
