#!/bin/bash
#
LIB_PATH="src/C"
EXIT=false
while [ "$EXIT" = "false" ]
do
  clear
  echo -e "+----------------------+"
  echo -e "| Samples Menu         |"
  echo -e "+----------------------+"
  echo -e "| 1: Led Counter       |"
  echo -e "| 2: Push Button input |"
  echo -e "| 3: MCP3008 (ADC)     |"
  echo -e "| 4: OLED SSD1306      |"
  echo -e "| 5: GPIO Interrupt    |"
  echo -e "| 6: BMP180 (I2C)      |"
  echo -e "| 7: BME280 (I2C)      |"
  echo -e "| 8: Servo             |"
  echo -e "| 9: ADS1015 (I2C ADC) |"
  echo -e "+----------------------+"
  echo -e "| Q: Quit              |"
  echo -e "+----------------------+"
  echo -n " You choose > "
  read choice
  case $choice in
    1)
      echo -e "Led Counter"
      sudo java -cp build/libs/core-0.1-all.jar -Djava.library.path=$LIB_PATH examples.io.led.LedCounter
      echo -en "Hit [return]"
      read a
      ;;
    2)
      echo -e "Push Button"
      sudo NATIVEDEBUG=false java -cp build/libs/core-0.1-all.jar -Djava.library.path=$LIB_PATH examples.io.gpio.SimpleInput
      echo -en "Hit [return]"
      read a
      ;;
    3)
      echo -e "ADC and potentiometer"
      sudo NATIVEDEBUG=false java -cp build/libs/core-0.1-all.jar -Djava.library.path=$LIB_PATH examples.io.spi.MCP3008Sample
      echo -en "Hit [return]"
      read a
      ;;
    4)
      echo -e "Small OLED Screen"
      JAVA_OPTS=
      JAVA_OPTS="$JAVA_OPTS -Dverbose=true"
      JAVA_OPTS="$JAVA_OPTS -Ddump.screen=false"
      sudo NATIVEDEBUG=false java -cp build/libs/core-0.1-all.jar $JAVA_OPTS -Djava.library.path=$LIB_PATH examples.io.i2c.SSD1306Sample
      echo -en "Hit [return]"
      read a
      ;;
    5)
      echo -e "GPIO Interrupt (have a push button connected on pin 27 - physical #13)"
      sudo NATIVEDEBUG=false java -cp build/libs/core-0.1-all.jar -Djava.library.path=$LIB_PATH examples.io.gpio.PinInterrupt
      echo -en "Hit [return]"
      read a
      ;;
    6)
      echo -e "BMP180"
      JAVA_OPTS=
      JAVA_OPTS="$JAVA_OPTS -Dbmp180.verbose=true"
      sudo NATIVEDEBUG=true java -cp build/libs/core-0.1-all.jar $JAVA_OPTS -Djava.library.path=$LIB_PATH examples.io.i2c.BMP180Sample
      echo -en "Hit [return]"
      read a
      ;;
    7)
      echo -e "BME280"
      JAVA_OPTS=
      JAVA_OPTS="$JAVA_OPTS -Dbme280.verbose=true"
      sudo NATIVEDEBUG=true java -cp build/libs/core-0.1-all.jar $JAVA_OPTS -Djava.library.path=$LIB_PATH examples.io.i2c.BME280Sample
      echo -en "Hit [return]"
      read a
      ;;
    8)
      echo -e "Sotfware Servo (have servo connected on pin 5 - physical #29)"
      sudo NATIVEDEBUG=false java -cp build/libs/core-0.1-all.jar -Djava.library.path=$LIB_PATH examples.io.servo.DirectPWMServo 5
      echo -en "Hit [return]"
      read a
      ;;
    9)
      echo -e "ADS1015"
      JAVA_OPTS=
      sudo NATIVEDEBUG=true java -cp build/libs/core-0.1-all.jar $JAVA_OPTS -Djava.library.path=$LIB_PATH examples.io.i2c.ADS1015Sample
      echo -en "Hit [return]"
      read a
      ;;
    [Qq])
      EXIT=true
      ;;
    *)
      echo -en "$choice not supported, hit [return]"
      read a
      ;;
  esac
done
echo "Bye now"
