package rpiledsrv

import org.specs2.mutable._

class IpTableSpec extends Specification {
  "IpTable" should {
    "The last accessed IP should retain pattern 0" in {
      val tbl = new IpTable()

      tbl.registerIpAndGetIndex("9.8.7.6") === 0
      Thread.sleep(1)
      tbl.registerIpAndGetIndex("0.1.2.3") === 1
      Thread.sleep(1)
      tbl.registerIpAndGetIndex("9.8.7.6") === 0 // Last accessed IP.

      // It should maintain latest two IPs. Oldest "0.1.2.3" should be deleted.
      tbl.registerIpAndGetIndex("5.5.5.5") === 1
      tbl.indexOf("0.1.2.3") === -1
      tbl.indexOf("9.8.7.6") === 0
      tbl.indexOf("5.5.5.5") === 1
    }

    "The last accessed IP should retain pattern 1" in {
      val tbl = new IpTable()

      tbl.registerIpAndGetIndex("9.8.7.6") === 0
      Thread.sleep(1)
      tbl.registerIpAndGetIndex("0.1.2.3") === 1
      Thread.sleep(1)
      tbl.registerIpAndGetIndex("9.8.7.6") === 0
      Thread.sleep(1)
      tbl.registerIpAndGetIndex("0.1.2.3") === 1 // Last accessed IP.

      // It should maintain latest two IPs. Oldest "9.8.7.6" should be deleted.
      tbl.registerIpAndGetIndex("5.5.5.5") === 0
      tbl.indexOf("9.8.7.6") === -1
      tbl.indexOf("0.1.2.3") === 1
      tbl.indexOf("5.5.5.5") === 0
    }
  }
}
