package fjp4s

import org.scalatest.matchers.should.Matchers
import org.scalatest.flatspec.AnyFlatSpec

class JsonParserSpec extends AnyFlatSpec with Matchers {
  JsonParser.expectValue("{}",0) shouldBe Right(2,JObject(JsonObjectInstance()))
  JsonParser.expectValue("""{"key":true}""",0) shouldBe Right(12,Json("key":=true))
  JsonParser.expectValue("""{"key":false}""",0) shouldBe Right(13,Json("key":=false))
  JsonParser.expectValue("""{"key": true}"""") shouldBe Right(13,Json("key":=true))
  JsonParser.expectValue(""" {"key" : true } """") shouldBe Right(16,Json("key":=true))
  JsonParser.expectValue(""" {"key" : null } """") shouldBe Right(16,Json("key":= null))
  """{"key":null}""".parseOption shouldBe Some(Json("key":=null))
  """{"key":[]}""".parseOption shouldBe Some(Json("key":=null))
  """{"key":null,"key2":true}""".parseOption shouldBe Some(Json("key":=null,"key2":=true))


  JsonParser.expectValue("""{"key":"value"}""",0) shouldBe Right(15,Json("key":="value"))
  JsonParser.expectValue(""" {"key" : { "key2" : true } } """") shouldBe Right(29,JObject(JsonObject.empty + ("key",Json("key2":=true))) )
}