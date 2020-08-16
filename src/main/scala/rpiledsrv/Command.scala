package rpiledsrv

case class Command(ip: String, commandType: CommandType)

object Command {
  def parse(line: String): Option[Command] = {
    val split = line.split(":")
    if (split.size != 2) None
    else
      CommandType.parse(split(1)).map { ct =>
        Command(split(0), ct)
      }
  }
}

