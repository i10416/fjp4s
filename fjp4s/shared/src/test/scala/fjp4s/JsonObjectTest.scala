package fjp4s

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.{shouldBe}

class JsonObjectSpec extends AnyFlatSpec {
  val empty = JsonObject.empty
  val key = "key"
  val key2 = "key2"
  val value = JNumber(1.asJsonNumber)
  val value2 = JNumber(2.asJsonNumber)
  val json = empty + (key, value) + (key2, value2)
  val json2 = empty :+ (key := 1) :+ (key2 := 2)
  val jobject = Json(key := 1, key2 := "2")
  val jobject2 = Json(key -> JNumber(1.asJsonNumber), key2 -> "2".asJson)
  empty shouldBe JsonObjectInstance()
  json shouldBe JsonObjectInstance(Map(key -> value, key2 -> value2))
  json(key) shouldBe Some(value)
  json shouldBe json2
  json("key") shouldBe Some(JNumber(1))
  jobject shouldBe JObject(
    JsonObjectInstance(Map(key -> value, key2 -> JString("2")))
  )
  jobject shouldBe jobject2
  Json("key" := Json("key2" := true)) shouldBe JObject(
    JsonObject.empty + ("key", Json("key2" := true))
  )
}
