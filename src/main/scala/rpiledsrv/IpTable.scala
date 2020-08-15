package rpiledsrv

import scala.collection.{mutable => mut}

class IpTable {
  val MaxEntries = 2
  private[this] var ipTable = new mut.LinkedHashSet[String]

  def registerIpAndGetIndex(ip: String): Int = {
    ipTable.add(ip)
    if (MaxEntries < ipTable.size) {
      ipTable = ipTable.drop(1)
    }
    ipTable.toVector.indexOf(ip)
  }
}

