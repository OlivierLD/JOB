# JavaHard

Deeply inspired by the work [Gottfried Haider](https://github.com/gohai/processing) has done for the Raspberry PI to communicate with [Processing](http://processing.org).

---

## To build it
You will need `javac` and `javah`.
To know if they are available, type
```bash
 $> which javac
 $> which javah
```
If at least one of the commands above returns nothing, then you need to update your `PATH`.
If your `JAVA_HOME` variable is not set, set it, and update your `PATH`, as follow:
```bash
 $> JAVA_HOME=/opt/jdk/jdk1.8.0_112
 $> PATH=$JAVA_HOME/bin:$PATH
```
After that, the commands above should return the expected values. You can check if thei is correct by typing
```bash
 $> javac -version
 $> javah -version
```

Compile the Java interface to the native code, from the project root:
```bash
 $> cd core
 $> mkdir build 2> /dev/null
 $> mkdir build/classes 2> /dev/null
 $> javac -sourcepath ./src/java -d ./build/classes -classpath ./build/classes -g ./src/java/jhard/io/JHardNativeInterface.java
```
Then generate the native library:
```bash
 $> cd src/C
 $> make
```
By then, you should see a `libjavahard-io.so` library in the `C` directory.

Finally, do the `gradle` build from the project root:
```bash
 $> cd ../..
 $> ../gradlew clean shadowJar
```
This generates a `core-0.1-all.jar` in the `build/libs` directory. This jar contains all the required dependencies.

## To run it
This is a work in progress... See the sample scripts `sample.0*`, see ho the `java.library.path` variable is set.
This is the one used to refer to the location of `libjavahard-io.so`.

## TODO
A lot!

---
