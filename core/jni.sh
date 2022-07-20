#!/bin/bash
# We assume Java is setup.
#if [[ "${JAVA_HOME}" == "" ]]; then
#  JAVA_HOME=/opt/jdk/jdk1.8.0_112
#fi
#PATH=${JAVA_HOME}/bin:${PATH}
mkdir build 2> /dev/null
mkdir build/classes 2> /dev/null
#
JAVA_MAJOR_VERSION=$(java -version 2>&1 | sed -E -n 's/.* version "([^.-]*).*"/\1/p' | awk '{ print $1 }')
if [[ $JAVA_MAJOR_VERSION -le 9 ]]; then
  # For Java 8
  echo -e ">> Compiling Java"
  javac -sourcepath ./src/java -d ./build/classes -classpath ./build/classes -g ./src/java/job/io/JOBNativeInterface.java
  echo -e ">> Running javah"
  javah -jni -cp ./build/classes -o src/C/job.h job.io.JOBNativeInterface
  echo -e ">> Here you should implement job.c, including job.h, and compile it"
else
  # For Java 10+
  echo -e ">> Compiling Java"
  javac -h src/C -sourcepath ./src/java -d ./build/classes -classpath ./build/classes -g ./src/java/job/io/JOBNativeInterface.java
  # trick:
  mv src/C/job_io_JOBNativeInterface.h src/C/job.h
fi
#
cd src/C
echo -e ">> Warning: Library must be named libjob-io.so and not only job-io.so"
echo -e ">> Compiling C (make, for Linux)"
make
cd ../..
#echo \>\> Now running \(java\) the class invoking the native lib:
export LD_LIBRARY_PATH=./src/C
# ls -l $LD_LIBRARY_PATH/*.so
# java -Djava.library.path=$LD_LIBRARY_PATH -cp ./classes jnisample.HelloWorld
echo \>\> Done.
