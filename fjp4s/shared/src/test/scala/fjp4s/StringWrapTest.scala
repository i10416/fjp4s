package fjp4s

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class StringWrapTest extends AnyFlatSpec with Matchers:
  ("key1" :=1) shouldBe ("key1",JNumber(1.asJsonNumber))
  val n :Option[Int] = None
  ("key":?= Some(1)) shouldBe ("key",JNumber(1.asJsonNumber))
  ("key":?= n) shouldBe ("key",JNull)
