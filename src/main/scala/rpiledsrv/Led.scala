package rpiledsrv

import com.pi4j.io.gpio.{GpioPinDigitalOutput, PinState}

import scala.concurrent.duration.Duration

class Led(name: String, pin: GpioPinDigitalOutput, isNegativeLogic: Boolean = false) {
  if (isNegativeLogic) {
    pin.high()
  } else {
    pin.low()
  }

  def blink(dur: Duration) {
    pin.pulse(dur.toMillis, if (isNegativeLogic) PinState.LOW else PinState.HIGH)
  }

  override def toString: String =
    "Led[" + name + "(" + pin.getName() + ")]";
}

