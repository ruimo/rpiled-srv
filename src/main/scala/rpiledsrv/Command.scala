package rpiledsrv

case class Command(ip: String, commandType: CommandType)

object Command {
  def apply(line: String): Command = {
    val split = line.split(":")
    Command(split(0), CommandType.parse(split(1)))
  }
}

