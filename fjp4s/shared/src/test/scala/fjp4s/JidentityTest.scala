package fjp4s

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.language.implicitConversions

class JidentitySpec extends AnyFlatSpec with Matchers {
  val s = 2
  s.asJsonNumber shouldBe MyJsons.MyInt(2)
  s.asJsonNumber shouldBe s.asJsonNumber
  s.asJson shouldBe JNumber(s.asJsonNumber)
}