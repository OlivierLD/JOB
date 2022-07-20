#!/bin/bash
#
LIB_PATH="src/C"
NATIVEDEBUG=false
EXIT=false
while [[ "${EXIT}" == "false" ]]; do
  clear
  echo -en "Info: NATIVEDEBUG (${NATIVEDEBUG}) is "
  if [[ "${NATIVEDEBUG}" == "true" ]]; then
    echo -e "ON"
  else
    echo -e "OFF"
  fi
  echo -e "+------------------------------------------------------------+"
  echo -e "|                 S A M P L E S   M E N U                    |"
  echo -e "+----------------------------+-------------------------------+"
  echo -e "|  1: Led Counter            |  6: BMP180 (I2C env sensor)   |"
  echo -e "|  2: Push Button input      |  7: BME280 (I2C env sensor)   |"
  echo -e "|  3: MCP3008 (ADC)          |  8: Servo (Direct GPIO)       |"
  echo -e "|  4: OLED SSD1306           |  9: ADS1015 (I2C ADC)         |"
  echo -e "|  5: GPIO Interrupt         | 10: PCA9685 (I2C Servos)      |"
  echo -e "| 15: GPIO Led flip          | 11: STH10 (Pure GPIO)         |"
  echo -e "|                            | 12: VL53L0X (I2C ToF)         |"
  echo -e "|                            | 13: TSL2561 (I2C LightSensor) |"
  echo -e "|                            | 14: HC_SR04 (GPIO RangeSensor)|"
  echo -e "+----------------------------+-------------------------------+"
  echo -e "| ON: NATIVEDEBUG on         | OFF: NATIVEDEBUG off          |"
  echo -e "+----------------------------+-------------------------------+"
  echo -e "|  Q: Quit                                                   |"
  echo -e "+------------------------------------------------------------+"
  echo -n " You choose > "
  read choice
  case "${choice}" in
    "1")
      echo -e "Led Counter"
      sudo NATIVEDEBUG=${NATIVEDEBUG} java -cp build/libs/core-0.1-all.jar -Djava.library.path=${LIB_PATH} examples.jobio.led.LedCounter
      echo -en "Hit [return]"
      read a
      ;;
    "2")
      echo -e "Push Button"
      EXTRA=
      # EXTRA="-Dpin=12"
      VERBOSE=
      # VERBOSE="-Dverbose=true"
      sudo NATIVEDEBUG=${NATIVEDEBUG} java ${EXTRA} ${VERBOSE} -cp build/libs/core-0.1-all.jar -Djava.library.path=${LIB_PATH} examples.jobio.gpio.SimpleInput
      echo -en "Hit [return]"
      read a
      ;;
    "3")
      echo -e "ADC and potentiometer"
      sudo NATIVEDEBUG=${NATIVEDEBUG} java -cp build/libs/core-0.1-all.jar -Djava.library.path=${LIB_PATH} examples.jobio.spi.MCP3008Sample
      echo -en "Hit [return]"
      read a
      ;;
    "4")
      echo -e "Small OLED Screen"
      JAVA_OPTS=
      JAVA_OPTS="${JAVA_OPTS} -Dverbose=true"
      JAVA_OPTS="${JAVA_OPTS} -Ddump.screen=false"
      sudo NATIVEDEBUG=${NATIVEDEBUG} java -cp build/libs/core-0.1-all.jar ${JAVA_OPTS} -Djava.library.path=${LIB_PATH} examples.jobio.i2c.SSD1306Sample
      echo -en "Hit [return]"
      read a
      ;;
    "5")
      echo -e "GPIO Interrupt (have a push button connected on pin 27 - physical #13)"
      sudo NATIVEDEBUG=${NATIVEDEBUG} java -cp build/libs/core-0.1-all.jar -Djava.library.path=${LIB_PATH} examples.jobio.gpio.PinInterrupt
      echo -en "Hit [return]"
      read a
      ;;
    "6")
      echo -e "BMP180"
      JAVA_OPTS=
      JAVA_OPTS="${JAVA_OPTS} -Dbmp180.verbose=true"
      sudo NATIVEDEBUG=${NATIVEDEBUG} java -cp build/libs/core-0.1-all.jar ${JAVA_OPTS} -Djava.library.path=${LIB_PATH} examples.jobio.i2c.BMP180Sample
      echo -en "Hit [return]"
      read a
      ;;
    "7")
      echo -e "BME280"
      JAVA_OPTS=
      JAVA_OPTS="${JAVA_OPTS} -Dbme280.verbose=true"
      sudo NATIVEDEBUG=${NATIVEDEBUG} java -cp build/libs/core-0.1-all.jar ${JAVA_OPTS} -Djava.library.path=${LIB_PATH} examples.jobio.i2c.BME280Sample
      echo -en "Hit [return]"
      read a
      ;;
    "8")
      echo -e "Sotfware Servo (have servo connected on pin 5 - physical #29)"
      sudo NATIVEDEBUG=${NATIVEDEBUG} java -cp build/libs/core-0.1-all.jar -Djava.library.path=${LIB_PATH} examples.jobio.servo.DirectPWMServo 5
      echo -en "Hit [return]"
      read a
      ;;
    "9")
      echo -e "ADS1015"
      JAVA_OPTS=
      sudo NATIVEDEBUG=${NATIVEDEBUG} java -cp build/libs/core-0.1-all.jar ${JAVA_OPTS} -Djava.library.path=${LIB_PATH} examples.jobio.i2c.ADS1015Sample
      echo -en "Hit [return]"
      read a
      ;;
    "10")
      echo -e "PCA9685"
      JAVA_OPTS=
      sudo NATIVEDEBUG=${NATIVEDEBUG} java -cp build/libs/core-0.1-all.jar ${JAVA_OPTS} -Djava.library.path=${LIB_PATH} examples.jobio.i2c.PCA9685Sample
      echo -en "Hit [return]"
      read a
      ;;
    "11")
      echo -e "STH10"
      JAVA_OPTS=
      sudo NATIVEDEBUG=${NATIVEDEBUG} java -cp build/libs/core-0.1-all.jar ${JAVA_OPTS} -Djava.library.path=${LIB_PATH} examples.jobio.gpio.STH10Sample
      echo -en "Hit [return]"
      read a
      ;;
    "12")
      echo -e "VL53L0X"
      JAVA_OPTS=
      sudo NATIVEDEBUG=${NATIVEDEBUG} java -cp build/libs/core-0.1-all.jar ${JAVA_OPTS} -Djava.library.path=${LIB_PATH} examples.jobio.i2c.VL53L0XSample
      echo -en "Hit [return]"
      read a
      ;;
    "13")
      echo -e "TSL2561"
      JAVA_OPTS=
      sudo NATIVEDEBUG=${NATIVEDEBUG} java -cp build/libs/core-0.1-all.jar ${JAVA_OPTS} -Djava.library.path=${LIB_PATH} examples.jobio.i2c.TSL2561Sample
      echo -en "Hit [return]"
      read a
      ;;
    "14")
      echo -e "HC_SR04"
      JAVA_OPTS=
      sudo NATIVEDEBUG=${NATIVEDEBUG} java -cp build/libs/core-0.1-all.jar ${JAVA_OPTS} -Djava.library.path=${LIB_PATH} examples.jobio.gpio.HC_SR04Sample
      echo -en "Hit [return]"
      read a
      ;;
    "15")
      echo -e "GPIO LED"
      JAVA_OPTS=
      sudo NATIVEDEBUG=${NATIVEDEBUG} java -cp build/libs/core-0.1-all.jar ${JAVA_OPTS} -Djava.library.path=${LIB_PATH} examples.jobio.gpio.GPIOLed
      echo -en "Hit [return]"
      read a
      ;;
    "ON" | "on")
      NATIVEDEBUG=true
      ;;
    "OFF" | "off")
      NATIVEDEBUG=false
      ;;
    "Q" | "q")
      EXIT=true
      ;;
    *)
      echo -en "${choice} not supported, hit [return]"
      read a
      ;;
  esac
done
echo "Bye now"
