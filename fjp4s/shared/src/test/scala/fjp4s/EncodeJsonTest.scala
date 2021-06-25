package fjp4s

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class EncodeJsonSpec extends AnyFlatSpec with Matchers {
  case class Foo(id:Int,name:String)
  val f = Foo(1,"john")
  given EncodeJson[Foo] with
    def encode(a:Foo) = Json("id":=a.id,"name":=a.name)
  f.asJson shouldBe Json("id":=f.id,"name":=f.name)
}