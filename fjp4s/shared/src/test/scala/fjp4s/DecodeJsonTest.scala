package fjp4s

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DecodeJsonTest extends AnyFlatSpec with Matchers {
  val j = Json("key":="value")
  val j2 = Json("key":=1)

  val c = Cursor.root(j)
  val c2 = Cursor.root(j2)
  val decoder = DecodeJson.of[String]
  val decoder2 = DecodeJson.of[Int]
  case class Bar(id:Int,name:String)
  given DecodeJson[Bar] with
    def decode(c:Cursor) = for {
        id <- c.downfield("id").get.as[Int]
        name <- c.downfield("name").get.as[String]
    } yield Bar(id,name)
  val decoder3 = DecodeJson.of[Bar]
  decoder3.decode(Cursor.root(Json("id":=1,"name":="bar"))) shouldBe Right(Bar(1,"bar"))
  val bar = Cursor.root(Json("id":=1,"name":="bar"))
  bar.as[Bar] shouldBe Right(Bar(1,"bar"))
  decoder.decode(c.downfield("key").get) shouldBe Decoding.DecodeResult.ok("value")
  decoder2.decode(c2.downfield("key").get) shouldBe Decoding.DecodeResult.ok(1)

}