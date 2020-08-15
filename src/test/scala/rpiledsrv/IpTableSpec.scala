package rpiledsrv

import org.specs2.mutable._

class IpTableSpec extends Specification {
  "IpTable" should {
    "Can register ip" in {
      val tbl = new IpTable()

      tbl.registerIpAndGetIndex("9.8.7.6") === 0
      tbl.registerIpAndGetIndex("0.1.2.3") === 1

      tbl.registerIpAndGetIndex("9.8.7.6") === 0
      tbl.registerIpAndGetIndex("0.1.2.3") === 1

      // It should maintain latest two IPs. Oldest "9.8.7.6" should be deleted.
      tbl.registerIpAndGetIndex("5.5.5.5") === 1
      tbl.registerIpAndGetIndex("0.1.2.3") === 0
      tbl.registerIpAndGetIndex("5.5.5.5") == 1
    }
  }
}
