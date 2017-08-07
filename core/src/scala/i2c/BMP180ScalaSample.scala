package i2c

import jhard.devices.BMP180

/*
 * Requires a BMP180
 * If you see errors about that, check your sensors...
 *
 * run with:
 * $ scala -cp build/classes/main:build/libs/core-0.1-all.jar i2c.BMP180ScalaSample
 */
object BMP180ScalaSample {
  def main(args: Array[String]) {
    println("Hello, Scala world! Reading sensors.")
    val bmp180  = new BMP180()
    println("Device ready")
    try {
      val temp  = bmp180.readTemperature
      val press = bmp180.readPressure / 100
      println(s"Temp:$temp \u00baC, Press:$press hPa")
    } catch {
      case ex: Exception =>
        println(ex.toString)
    }
  }
}
