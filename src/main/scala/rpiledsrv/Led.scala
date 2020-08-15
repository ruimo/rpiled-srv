package rpiledsrv

import com.pi4j.io.gpio.{GpioPinDigitalOutput, PinState}

import scala.concurrent.duration.Duration

class Led(name: String, pin: GpioPinDigitalOutput) {
  def blink(dur: Duration) {
    pin.pulse(dur.toMillis, PinState.HIGH)
  }

  override def toString: String =
    "Led[" + name + "(" + pin.getName() + ")]";
}

