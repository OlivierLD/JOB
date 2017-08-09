package gpio

import java.util.function.Consumer

import jhard.io.GPIO

/**
  * Push button (pin 27) interrupt demo.
  *
  * to run with:
  * $ scala -cp build/classes/main:build/libs/core-0.1-all.jar gpio.PinInterruptScalaSample
  */
object PinInterruptScalaSample {

  // TASK: Maybe there is a better way to do this...
  val buttonPressed = new Consumer[Integer] {
    override def accept(pin:Integer): Unit = {
      println(s"Button pressed, pin ${pin}")
    }
  }

  def main(args: Array[String]): Unit = {

    val pin = 27

    val me = Thread currentThread

    sys addShutdownHook {
      println(" <- Oops! Shutdown hook caught.")
      me synchronized {
        me notify
      }
      println("Bye.")
    }

    GPIO.pinMode(pin, GPIO.INPUT)
    GPIO.attachInterrupt(pin, this.buttonPressed, GPIO.RISING)
    println("Ready. Type Ctrl-C to quit.")

    me synchronized {
      try {
        me wait
      } catch {
        case ex: Exception =>
          ex printStackTrace
      }
    }
    println("Done!")
  }
}
