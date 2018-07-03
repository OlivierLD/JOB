# JavaHard
### An Hardware I/O library for the Raspberry PI.

Deeply inspired by the work [Gottfried Haider](https://github.com/gohai/processing) (with his authorization) has done for the Raspberry PI to communicate with [Processing](http://processing.org).

It is supposed to be as light as possible, and as close the the registers as possible.

This should make the translations from `Python` or `C` code into `Java` a bit easier.

Based on Java 8 (uses lambdas, Streaming API, FunctionalInterfaces, etc).

> _Note_: GPIO Pin numbers are the ones available [here](https://www.raspberrypi.org/documentation/usage/gpio/README.md).

## How it works
The low level interactions with the pins of the GPIO Header have to be done at the system level, they have to be performed in `C`.
Devices - and their pins - are considered as `Files`, and bits are sent to received from the devices through `ioctl` or similar `C` functions.

To write a Java class that communicates with `C`, you need to use the `javah` utility.

To use `javah`:
- Write a Java class, mentioning the the methods that will need to interact with `C` as `native`.
- Compile this class with `javac`.
- Run `javah` on this compiled class

`javah` will generate the `C` header file with the signatures of the `C` functions to implement. All you need to do is to implement them in a `C` file.
This `C` file will be compiled into a system library (with an `.so` extension), that will be dynamically loaded by Java at run time.

Below are the detailed steps of the process.

The Java class to start from is `JHardNativeInterface.java`.
See the header file `jhard.h`, and its corresponding implementation `jhard.c`.

---
## To build it
You will need `javac` and `javah`.
To know if they are available, type
```
 $> which javac
 $> which javah
```
If at least one of the commands above returns nothing, then you need to update your `PATH`.
If your `JAVA_HOME` variable is not set, set it, and update your `PATH`, as follow:
```
 $> JAVA_HOME=/opt/jdk/jdk1.8.0_112
 $> PATH=$JAVA_HOME/bin:$PATH
```
After that, the commands above should return the expected values. You can check if this is correct by typing
```
 $> javac -version
 $> javah -version
```

Compile the Java interface to the native code, from the project root:
```
 $> cd core
 $> mkdir build 2> /dev/null
 $> mkdir build/classes 2> /dev/null
 $> javac -sourcepath ./src/java -d ./build/classes -classpath ./build/classes -g ./src/java/jhard/io/JHardNativeInterface.java
```
Then generate the native library:
```
 $> cd src/C
 $> make
```
The make command invokes the `javah` utility. See the content of `Makefile`.
By then, you should see a `libjavahard-io.so` library in the `C` directory.

Finally, do the `gradle` build from the project root:
```
 $> cd ../..
 $> ../gradlew clean shadowJar
```
This generates a `core-0.1-all.jar` in the `build/libs` directory. This jar contains all the required dependencies.

A script summarizes all those operations, just run
```
 $> ./jni.sh
```

## To run it
This is a work in progress... The samples can be run from a single script named `samplemenu.sh`, see how the `java.library.path` variable is set.
This is the one used to refer to the location of `libjavahard-io.so`.

Run the script named `samplemenu.sh`:
```
 $> ./samplemenu.sh
  +---------------------------------------------+
  |           S A M P L E S   M E N U           |
  +----------------------+----------------------+
  | 1: Led Counter       | 6: BMP180 (I2C)      |
  | 2: Push Button input | 7: BME280 (I2C)      |
  | 3: MCP3008 (ADC)     | 8: Servo             |
  | 4: OLED SSD1306      | 9: ADS1015 (I2C ADC) |
  | 5: GPIO Interrupt    |                      |
  +----------------------+----------------------+
  | Q: Quit                                     |
  +---------------------------------------------+
   You choose >

```
Make sure the required devices are correctly wired for the demos.

## Compatibility
Should be compatible with any JVM-aware languages. Samples to be provided.

#### Scala
To compile and run the Scala code:

Make sure the core has been built:
```
 $> ../gradlew shadowJar
```

Compile the Scala classes you want to run:
```
 $> scalac -sourcepath ./src/scala -cp ./build/libs/core-0.1-all.jar ./src/scala/i2c/BME280ScalaSample.scala -d ./build/classes
```
Then to run it:
```
 $> export LIB_PATH="src/C"
 $> scala -cp ./build/libs/core-0.1-all.jar:./build/classes -Djava.library.path=$LIB_PATH i2c.BME280ScalaSample
 Hello, Scala world! Reading sensors.
 Device ready
 Temp:20.4 ºC, Press:1015.6 hPa, Hum:64.0 %
 $>
```

## Available device implementations
- SSD1306 (128x32 I<sup><small>2</small></sup>C oled screen).
- GPIO push-button (with interrupt, or not)
- BMP180 (I<sup><small>2</small></sup>C Pressure, temperature (=> altitude))
- BME280 (I<sup><small>2</small></sup>C Pressure, temperature (=> altitude), humidity)
- Servos (direct, Software Servos)
- ADS1x15 (I<sup><small>2</small></sup>C ADCs)
- PCA9685 (I<sup><small>2</small></sup>C Servo Driver)
- STH10 (GPIO Temperature, Humidity)
- VL53L0X (I<sup><small>2</small></sup>C Time of Flight Distance Sensor)

## TODO
A lot!

- HMC5883L (I<sup><small>2</small></sup>C Magnetometer/Compass)
- TSL2561 (I<sup><small>2</small></sup>C Light Sensor)
- LSM303 (I<sup><small>2</small></sup>C Triple-axis Accelerometer + Magnetometer (Compass) Board)
- HTU21DF (I<sup><small>2</small></sup>C Temperature, Humidity)
- LoRa
- FONA
- ...etc

---
