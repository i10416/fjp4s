package fjp4s

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.language.implicitConversions


class MacroSpec extends AnyFlatSpec with Matchers {
  import EncodeJson._
  final case class Address(name:String,code:Int)
  final case class Person(name:String,address:Address)
  implicit val en:EncodeJson[Address] = EncodeJsonMacro.derive[Address] 
  implicit val pn:EncodeJson[Person] = EncodeJsonMacro.derive[Person]
  val addressJson =   Address("aaa",2).asJson
  
  addressJson shouldBe Json("name":="aaa","code":=2)
  val personJ = Person("taro",Address("aaa",2)).asJson
  personJ.isInstanceOf[Json] shouldBe true
}