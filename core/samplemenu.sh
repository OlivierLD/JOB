#!/bin/bash
#
LIB_PATH="src/C"
EXIT=false
while [ "$EXIT" = "false" ]
do
  clear
  echo -e "+----------------------+"
  echo -e "| Sample Menu          |"
  echo -e "+----------------------+"
  echo -e "| 1: Led Counter       |"
  echo -e "| 2: Push Button input |"
  echo -e "| 3: MCP3008 (ADC)     |"
  echo -e "| 4: OLED SSD1306      |"
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
      sudo java -cp build/libs/core-0.1-all.jar -Djava.library.path=$LIB_PATH examples.io.gpio.SimpleInput
      echo -en "Hit [return]"
      read a
      ;;
    3)
      echo -e "ADC and potentiometer"
      sudo java -cp build/libs/core-0.1-all.jar -Djava.library.path=$LIB_PATH examples.io.spi.MCP3008Sample
      echo -en "Hit [return]"
      read a
      ;;
    4)
      echo -e "Small OLED Screen"
      JAVA_OPTS=
      JAVA_OPTS="$JAVA_OPTS -Dverbose=true"
      JAVA_OPTS="$JAVA_OPTS -Ddump.screen=false"
      sudo java -cp build/libs/core-0.1-all.jar $JAVA_OPTS -Djava.library.path=$LIB_PATH examples.io.i2c.SSD1306Sample
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
