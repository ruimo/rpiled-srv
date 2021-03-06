package rpiledsrv

import java.util.concurrent.TimeUnit

import scala.concurrent.duration.Duration
import com.pi4j.io.gpio.{GpioController, GpioFactory, RaspiPin}

abstract class LedTable {
  protected val ipTable: IpTable = new IpTable()
  def blink(command: Command): Unit
}

class MockLedTable extends LedTable {
  override def blink(command: Command) {
    val idx = ipTable.registerIpAndGetIndex(command.ip)
    println("Blink LED(" + idx + ")")
  }
}

class LedTableImpl extends LedTable {
  private[this] val gpio: GpioController = GpioFactory.getInstance()
  private[this] val ledTable: Array[Led] = Array(
    new Led("online-0", gpio.provisionDigitalOutputPin(RaspiPin.GPIO_08)),
    new Led("access-0", gpio.provisionDigitalOutputPin(RaspiPin.GPIO_09)),
    new Led("liveness-success-0", gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07), true),
    new Led("liveness-failure-0", gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00), true),
    new Led("readiness-success-0", gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02)),
    new Led("readiness-failure-0", gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03)),

    new Led("online-1", gpio.provisionDigitalOutputPin(RaspiPin.GPIO_15)),
    new Led("access-1", gpio.provisionDigitalOutputPin(RaspiPin.GPIO_16)),
    new Led("liveness-success-1", gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04), true),
    new Led("liveness-failure-1", gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05), true),
    new Led("readiness-success-1", gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06)),
    new Led("readiness-failure-1", gpio.provisionDigitalOutputPin(RaspiPin.GPIO_10)),
  )

  override def blink(command: Command) {
    blinkLed(getLed(command))
  }

  def blinkLed(led: Led): Unit = {
    println("Blink led: " + led)
    led.blink(Duration.apply(200, TimeUnit.MILLISECONDS))
  }

  def getLed(command: Command): Led = {
    val idx = ipTable.registerIpAndGetIndex(command.ip)
    ledTable(idx * 6 + command.commandType.ordinal)
  }
}

