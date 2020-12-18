package rpiledsrv

import scala.collection.{mutable => mut}
import scala.annotation.tailrec

class IpTable {
  sealed trait IpSlot
  object IpSlot {
    case object Vacant extends IpSlot
    case class Element(ip: String, lastUpdated: Long) extends IpSlot
  }

  val MaxEntries = 2
  private[this] var ipTable: mut.Seq[IpSlot] = mut.Seq.fill(MaxEntries)(IpSlot.Vacant)

  def registerIpAndGetIndex(ip: String): Int = {
    @tailrec def findExisting(idx: Int): Int = {
      if (ipTable.length <= idx) -1
      else {
        ipTable(idx) match {
          case IpSlot.Vacant =>
            findExisting(idx + 1)
          case IpSlot.Element(eip, lastUpdated) =>
            if (eip == ip) {
              idx
            } else {
              findExisting(idx + 1)
            }
        }
      }
    }

    val idx = findExisting(0)
    if (idx != -1) {
      ipTable(idx) = IpSlot.Element(ip, System.currentTimeMillis)
      idx
    } else {
      val vacantIdx = ipTable.indexWhere(_ == IpSlot.Vacant)
      if (vacantIdx != -1) {
        ipTable(vacantIdx) = IpSlot.Element(ip, System.currentTimeMillis)
        vacantIdx
      } else {
        @tailrec def findOldest(curIdx: Int, oldestIdx: Int = -1): Int = {
          if (ipTable.length <= curIdx) oldestIdx
          else {
            ipTable(curIdx) match {
              case IpSlot.Vacant => findOldest(curIdx + 1, oldestIdx)
              case IpSlot.Element(eip, lastUpdated) =>
                if (oldestIdx == -1)
                  findOldest(curIdx + 1, curIdx)
                else {
                  if (lastUpdated < ipTable(oldestIdx).asInstanceOf[IpSlot.Element].lastUpdated)
                    findOldest(curIdx + 1, curIdx)
                  else
                    findOldest(curIdx, oldestIdx)
                }
            }
          }
        }

        val oldestIdx = findOldest(0)
        ipTable(oldestIdx) = IpSlot.Element(ip, System.currentTimeMillis)
        oldestIdx
      }
    }
  }

  def indexOf(ip: String): Int = ipTable.indexWhere {
    case IpSlot.Vacant => false
    case IpSlot.Element(eip, _) => eip == ip
  }
}

