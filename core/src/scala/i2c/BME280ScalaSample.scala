package i2c

import job.devices.BME280

/*
 * Requires a BME280
 * If you see errors about that, check your sensors...
 *
 * run with:
 * $ scala -cp build/classes/main:build/libs/core-0.1-all.jar i2c.BME280ScalaSample
 */
object BME280ScalaSample {
  def main(args: Array[String]): Unit = {
    println("Hello, Scala world! Reading sensors.")
    val bme280  = new BME280()
    println("Device ready")
    try {
      val data  = bme280.getAllData
      println(s"Temp:${data.getTemp} \u00baC, Press:${data.getPress} hPa, Hum:${data.getHum} %")
    } catch {
      case ex: Exception =>
        println(ex.toString)
    }
  }
}
