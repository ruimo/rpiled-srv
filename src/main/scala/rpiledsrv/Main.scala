package rpiledsrv

import java.io.{BufferedReader, FileReader}
import java.nio.file.Paths

import scala.util.Using

object Main {
  val FifoPath = Paths.get("/var/fifo")
  val NoLed = Option(System.getenv("NO_LED")).map(s => s.equalsIgnoreCase("true")).getOrElse(false)

  def main(args: Array[String]) {
    println("Start initialization...")
    val ledTable = createLedTable
    println("End initialization.")

    println("Opening fifo...")
    Using.resource(new BufferedReader(new FileReader(FifoPath.toFile()))) { startWithReader }

    def startWithReader(br: BufferedReader) {
      while (true) {
        println("Waiting fifo...")
        val line = br.readLine()
        if (line != null) {
          println("Get from fifo '" + line + "'")
          handle(line)
        }
        Thread.sleep(100)
      }
    }

    def handle(line: String) {
      Command.parse(line) match {
        case None =>
          println("Cannot parse '" + line + "'. Ignored.")
        case Some(c) =>
          handleWithCommand(c)
      }
    }

    def handleWithCommand(command: Command) {
      ledTable.blink(command)
    }
  }

  private[this] def createLedTable: LedTable =
    if (NoLed) {
      new MockLedTable()
    } else {
      new LedTableImpl()
    }
}
