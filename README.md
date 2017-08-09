# JavaHard
### An Hardware I/O library for the Raspberry PI.

Deeply inspired by the work [Gottfried Haider](https://github.com/gohai/processing) (with his authorization) has done for the Raspberry PI to communicate with [Processing](http://processing.org).

It is supposed to be as light as possible, and as close the the registers as possible.

This should make the translations from `Python` or `C` code into `Java` a bit easier.

Based on Java 8 (uses lambdas, Streaming API, FunctionalInterfaces, etc).

## How it works
The low level interactions with the pins of the GPIO Header have to be done at the system level, they have to be performed in `C`.
Devices - and their pins - are considered as `Files`, and bits are sent to received from through `ioctl` or similar `C` functions.

To communicate with `C`, Java uses the `javah` utility.

To use `javah`:
- Write a Java class, mentioning the the methods that will need to interact with `C` as `native`.
- Compile this class with `javac`.
- Run `javah` on this compiled class

`javah` will generate the `C` header file with the signatures of the `C` functions to implement. All you need to do is to implement them in a `C` file.
This `C` file will be compiled into a system library (with an `.so` extension), that will be dynamically loaded by Java at run time.

Below are the detailed steps of the process.

See the header file `jhard.h`, and its corresponding implementation `jard.c`.

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
This is a work in progress... The samples can be run from a single script named `samplemenu.h`, see ho the `java.library.path` variable is set.
This is the one used to refer to the location of `libjavahard-io.so`.

Run the script named `samplemenu.sh`:
```bash
 $> ./samplemenu.sh
 +----------------------+
 | Sample Menu          |
 +----------------------+
 | 1: Led Counter       |
 | 2: Push Button input |
 | 3: MCP3008 (ADC)     |
 | 4: OLED SSD1306      |
 |   ...                |
 +----------------------+
 | Q: Quit              |
 +----------------------+
  You choose >

```
Make sure the required devices are correctly wired for the demos.

## Compatibility
Should be comaptible with any JVM-aware languages. Samples to be provided.

## Available device implementations
- SSD1306
- GPIO push-button (with interrupt, or not)
- BMP180
- BME280

## TODO
A lot!

- LSM303
- HTU21DF
- LoRa
- FONA
- Servos & PWM
- ...etc

---
