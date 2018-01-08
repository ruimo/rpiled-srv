package rpiledsrv;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Arrays;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinDirection;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.trigger.GpioCallbackTrigger;
import com.pi4j.io.gpio.trigger.GpioPulseStateTrigger;
import com.pi4j.io.gpio.trigger.GpioSetStateTrigger;
import com.pi4j.io.gpio.trigger.GpioSyncStateTrigger;
import com.pi4j.io.gpio.event.GpioPinListener;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.gpio.event.PinEventType;

enum CommandType {
    ONLINE, REQUEST;

    static CommandType parse(String s) {
        switch (s) {
        case "o": return CommandType.ONLINE;
        case "r": return CommandType.REQUEST;
        default: throw new IllegalArgumentException("Invalid command type '" + s + "'");
        }
    }
}

class Command {
    final String ip;
    final CommandType commandType;

    Command(String line) {
        String[] split = line.split(":");
        ip = split[0];
        commandType = CommandType.parse(split[1]);
    }

    @Override public String toString() {
        return "Command[" + ip + ", " + commandType + "]";
    }
}

class Led {
    final int index;
    final GpioPinDigitalOutput pin;

    Led(int index, GpioPinDigitalOutput pin) {
        this.index = index;
        this.pin = pin;
    }

    void blink(long ms) throws Exception {
        pin.high();
        try {
            Thread.sleep(ms);
        }
        finally {
            pin.low();
        }
    }

    @Override public String toString() {
        return "Led[" + index + "]";
    }
}

public class Main {
    static final String FIFO_PATH = "/tmp/fifo";
    final Path fifoPath;
    final GpioController gpio;
    final Led[] ledTable;
    final SortedSet<String> ipTable = new TreeSet<>();

    Main() {
        System.err.println("Start initialization...");
        fifoPath = Paths.get(FIFO_PATH);
        gpio = GpioFactory.getInstance();
        ledTable = new Led[4];
        ledTable[0] = new Led(0, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00));
        ledTable[1] = new Led(1, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01));
        ledTable[2] = new Led(2, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02));
        ledTable[3] = new Led(3, gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03));
        System.err.println("End initialization.");
    }

    void start() throws Exception {
        System.err.println("Opening fifo...");
        try (BufferedReader br = new BufferedReader(new FileReader(fifoPath.toFile()))) {
            start(br);
        }
    }

    void start(BufferedReader br) throws Exception {
        while (true) {
            System.err.println("Waiting fifo...");
            String line = br.readLine();
            if (line != null) {
                System.err.println("Get from fifo '" + line + "'");
                handle(line);
            }
            Thread.sleep(100);
        }
    }

    void handle(String line) throws Exception {
        handle(new Command(line));
    }

    void handle(Command command) throws Exception {
        blinkLed(command);
    }

    void blinkLed(Command command) throws Exception {
        System.err.println("Get command '" + command + "'");
        blinkLed(getLed(command));
    }

    void blinkLed(Led led) throws Exception {
        System.err.println("Blink led: " + led);
        led.blink(200);
    }

    Led getLed(Command command) throws Exception {
        ipTable.add(command.ip);
        int idx = Arrays.binarySearch(ipTable.toArray(new String[ipTable.size()]), command.ip);
        return ledTable[command.commandType.ordinal() * 2 + (idx % 2)];
    }

    public static void main(String... args) throws Exception {
        new Main().start();
    }
}
