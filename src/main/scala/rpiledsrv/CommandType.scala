package rpiledsrv

sealed trait CommandType {
  val ordinal: Int
}

object CommandType {
  case object Online extends CommandType {
    override val ordinal = 0
  }
  case object Request extends CommandType {
    override val ordinal = 1
  }
  case object LivenessSuccess extends CommandType {
    override val ordinal = 2
  }
  case object LivenessFailure extends CommandType {
    override val ordinal = 3
  }
  case object RedinessSuccess extends CommandType {
    override val ordinal = 4
  }
  case object RedinessFailure extends CommandType {
    override val ordinal = 5
  }

  def parse(s: String): Option[CommandType] = s match {
    case "o" => Some(Online)
    case "r" => Some(Request)
    case "ls" => Some(LivenessSuccess)
    case "lf" => Some(LivenessFailure)
    case "rs" => Some(RedinessSuccess)
    case "rf" => Some(RedinessFailure)
    case _ => None
  }
}
