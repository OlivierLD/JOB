# JOB - <u>J</u>ava <u>O</u>n <u>B</u>oards
![Java On Board](./surfing.gif)
### A Hardware I/O library for the Raspberry PI and similar small single-board computers.

Deeply inspired by the work [Gottfried Haider](https://github.com/gohai/processing) (with his authorization) has done for the Raspberry PI to communicate with [Processing](http://processing.org).

It is supposed to be as light as possible, and as close the the registers as possible.

This should make the translations from `Python` or `C` code into `Java` a bit easier.

Based at least on Java 8 (uses lambdas, Streaming API, FunctionalInterfaces, etc).

Seems to work (as is) on 32 and 64 bits OSs (Raspberry Pi)

> _Note_: GPIO Pin numbers are the ones available [here](https://www.raspberrypi.org/documentation/usage/gpio/README.md), or below, in the BCM columns.
```
       +-----+-----+--------------+-----++-----+--------------+-----+-----+
       | BCM | wPi | Name         |  Physical  |         Name | wPi | BCM |
       +-----+-----+--------------+-----++-----+--------------+-----+-----+
       |     |     | 3v3          | #01 || #02 |          5v0 |     |     |
       |  02 |  08 | SDA1         | #03 || #04 |          5v0 |     |     |
       |  03 |  09 | SCL1         | #05 || #06 |          GND |     |     |
       |  04 |  07 | GPCLK0       | #07 || #08 |    UART0_TXD | 15  | 14  |
       |     |     | GND          | #09 || #10 |    UART0_RXD | 16  | 15  |
       |  17 |  00 | GPIO_0       | #11 || #12 | PCM_CLK/PWM0 | 01  | 18  |
       |  27 |  02 | GPIO_2       | #13 || #14 |          GND |     |     |
       |  22 |  03 | GPIO_3       | #15 || #16 |       GPIO_4 | 04  | 23  |
       |     |     | 3v3          | #17 || #18 |       GPIO_5 | 05  | 24  |
       |  10 |  12 | SPI0_MOSI    | #19 || #20 |          GND |     |     |
       |  09 |  13 | SPI0_MISO    | #21 || #22 |       GPIO_6 | 06  | 25  |
       |  11 |  14 | SPI0_CLK     | #23 || #24 |   SPI0_CS0_N | 10  | 08  |
       |     |     | GND          | #25 || #26 |   SPI0_CS1_N | 11  | 07  |
       |  00 |  30 | SDA0         | #27 || #28 |         SCL0 | 31  | 01  |
       |  05 |  21 | GPCLK1       | #29 || #30 |          GND |     |     |
       |  06 |  22 | GPCLK2       | #31 || #32 |         PWM0 | 26  | 12  |
       |  13 |  23 | PWM1         | #33 || #34 |          GND |     |     |
       |  19 |  24 | PCM_FS/PWM1  | #35 || #36 |      GPIO_27 | 27  | 16  |
       |  26 |  25 | GPIO_25      | #37 || #38 |      PCM_DIN | 28  | 20  |
       |     |     | GND          | #39 || #40 |     PCM_DOUT | 29  | 21  |
       +-----+-----+--------------+-----++-----+--------------+-----+-----+
       | BCM | wPi | Name         |  Physical  |         Name | wPi | BCM |
       +-----+-----+--------------+-----++-----+--------------+-----+-----+
```

## How it works
The low level interactions with the pins of the GPIO Header have to be done at the system level, they have to be performed in `C`.
Devices - and their pins - are considered as `Files`, and bits are sent to received from the devices through `ioctl` or similar `C` functions.

To write a Java class that communicates with `C`, you need to use the `javah` (or `javac -h`) utility.

To use `javah`, or its new equivalent `javac -h`:
- Write a Java class, mentioning the methods that will need to interact with `C` as `native`.
- Up to Java 9
  - Compile this class with `javac`.
  - Run `javah` on this compiled class
- With Java 10 and higher
  - Run `javac -h`

`javah` (or `javac -h`) will generate the `C` header file with the signatures of the `C` functions to implement. All you need to do is to implement them in a `C` file.
This `C` file will be compiled into a system library (with an `.so` extension), that will be dynamically loaded by Java at run time.

Below are the detailed steps of the process.

The Java class to start from is `JOBNativeInterface.java`.
See the header file `job.h`, and its corresponding implementation `job.c`.

---
## To build it
You will need `javac` and `javah` (for Java before version 10).
To know if they are available, type
```
 $> which javac
 $> which javah
```
> Note: `javah` has been deprecated since Java 10. Use `javac -h` instead.

```
 $> cd core
 $> mkdir build 2> /dev/null
 $> mkdir build/classes 2> /dev/null
```

### For Java up to 9
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
 $> javac -sourcepath ./src/java -d ./build/classes -classpath ./build/classes -g ./src/java/job/io/JOBNativeInterface.java
```
Then generate the native library:
```
 $> cd src/C
 $> make
```
The `make` command invokes the `javah` utility. See the content of `Makefile`.

### For Java 10 and above
```
$> javac -h src/C -sourcepath ./src/java -d ./build/classes -classpath ./build/classes -g ./src/java/job/io/JOBNativeInterface.java
$> mv src/C/job_io_JOBNativeInterface.h src/C/job.h  # For compatibility with previous versions
```
Then generate the native library:
```
 $> cd src/C
 $> make
```

### And then
By then, you should see a `libjob-io.so` library in the `C` directory.

## Take it easy
A script _**summarizes all**_ those operations, just run
```
 $> ./jni.sh
```

**Finally** (for all Java versions), do the `gradle` build from the `core` module :
```
 $> ../gradlew clean shadowJar
```
This generates a `core-0.1-all.jar` in the `build/libs` directory. This jar contains all the required dependencies.

## To run it, to put it to work...
This is a work in progress... The samples can be run from a single script named `samplemenu.sh`, see how the `java.library.path` variable is set.
This is the one used to refer to the location of `libjob-io.so`.

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
> _Note_: The menu above is a work in progress, you might see more options...

Make sure the required devices are correctly wired for the demos.

## Compatibility
Should be compatible with any *JVM-aware* languages. Some samples to be provided (more to come).

Make sure the core has been built:
```
 $> ../gradlew shadowJar
```

#### [Scala](https://www.scala-lang.org/)
Compile the Scala classes you want to run:
```
 $> scalac -sourcepath ./src/scala -cp ./build/libs/core-0.1-all.jar ./src/scala/i2c/BME280ScalaSample.scala -d ./build/classes
```
Then to run it:
```
 $> export LIB_PATH="src/C"
 $> scala -cp ./build/libs/core-0.1-all.jar:./build/classes -Djava.library.path=$LIB_PATH i2c.BME280ScalaSample
 Hello, Scala world! Reading sensors (BME280).
 Device ready
 Temp:20.4 ºC, Press:1015.6 hPa, Hum:64.0 %
 $>
```

#### [Groovy](http://groovy-lang.org/)
From `Gradle` (located in the `core` directory):
```
 $> ../gradlew runGroovyScript
```

After installing `groovy`, from the `core` directory:

On Mac OS, `GROOVY_HOME` would be like `/usr/local/opt/groovy/libexec`.

On a Raspberry PI, after using `SDKMan` to install `groovy`, `GROOVY_HOME` would be like `/home/pi/.sdkman/candidates/groovy/2.5.0`.

```
 $> export GROOVY_HOME=/home/pi/.sdkman/candidates/groovy/2.5.0
 $> export CLASSPATH=$(find $GROOVY_HOME/lib -name '*.jar' | tr '\n' ':')
 $> export CLASSPATH=$CLASSPATH:$PWD/build/libs/core-0.1-all.jar
 $> export LIB_PATH=$PWD/src/C
 $> export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$LIB_PATH
 $> cd src/groovy
 $> groovy SensorReader
 ==================
 Now running some RPi stuff from Groovy (BME280)
 ==================
 Temperature: 21.32 C
 Pressure   : 1015.65 hPa
 Humidity   : 65.40 %

 $>
```
After setting `GROOVY_HOME` and `CLASSPATH`, you can also run the script from the `core` folder:
```
 $> groovy src/main/SensorReader
 ==================
 Now running some RPi stuff from Groovy (BME280)
 ==================
 Temperature: 21.32 C
 Pressure   : 1015.65 hPa
 Humidity   : 65.40 %

 $>
```

Do also try `groovysh` and `groovyConsole`.

#### Others
`Scala` and `Groovy` are - by far - not the only JVM-compatible languages.
The others should work too, there is no reason not to.

#### REPL
`REPL` stands for `Read-Evaluate-Print-Loop`. It is an interactive command-line console, that allows the user
to type expressions in the corresponding language, and have them evaluated immediately, it is _very_ useful in a development phase.

- Scala has one, just type `scala` in a terminal.
- Groovy has one, type `groovysh` in a terminal.
- Since Java9, Java has one too, type `jshell` in a terminal.

Example of `groovysh`:
```
$> groovysh -Djava.library.path=$LIB_PATH
Groovy Shell (2.5.0, JVM: 1.8.0_144)
Type ':help' or ':h' for help.
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
groovy:000> import job.devices.BME280
===> job.devices.BME280
sensor = new BME280()
===> job.devices.BME280@1c80e49b
groovy:000> sensor.readTemperature()
===> 21.33
groovy:000>
```

## Available device (sensors and actuators) implementations
- SSD1306 (128x32 I<sup><small>2</small></sup>C oled screen).
- GPIO push-button (with interrupt, or not)
- BMP180 (I<sup><small>2</small></sup>C Pressure, temperature (=> altitude))
- BME280 (I<sup><small>2</small></sup>C Pressure, temperature (=> altitude), humidity)
- Servos (direct, Software Servos)
- ADS1x15 (I<sup><small>2</small></sup>C ADCs)
- PCA9685 (I<sup><small>2</small></sup>C Servo Driver)
- STH10 (GPIO Temperature, Humidity)
- VL53L0X (I<sup><small>2</small></sup>C Time of Flight Distance Sensor)
- TSL2561 (I<sup><small>2</small></sup>C Light Sensor)

## TODO
A lot!
- HMC5883L (I<sup><small>2</small></sup>C Magnetometer/Compass)
- LSM303 (I<sup><small>2</small></sup>C Triple-axis Accelerometer + Magnetometer (Compass) Board)
- HTU21DF (I<sup><small>2</small></sup>C Temperature, Humidity)
- HC-SR04 (Ultra sonic Range Sensor)
- LoRa
- Stepper motors drivers
- ...etc

---
- Put it in a Maven repo ?

---
