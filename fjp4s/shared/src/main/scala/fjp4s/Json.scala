package fjp4s

sealed abstract class Json extends Product with Serializable {
  def arrayOrObject[X](or: => X, obj: JsonObject => X) = this match {
    case JNull | JBool(_) | JNumber(_) | JString(_) => or
    case JObject(o)                                 => obj(o)
    case JArray(a) => ???
  }
  def string: Option[String] = this match {
    case JNull      => None
    case JBool(b)   => None
    case JNumber(n) => None
    case JString(s) => Some(s)
    case JObject(o) => None
    case JArray(a) =>  None
  }
  def number: Option[MyJsons.MyJsonNumber] = this match {
    case JNull      => None
    case JBool(b)   => None
    case JNumber(n) => Some(n)
    case JString(s) => None
    case JObject(o) => None
    case JArray(a) => None
  }

  def obj: Option[JsonObject] = arrayOrObject(None, Some(_))
}
import Json._
private case class JNumber(n: MyJsons.MyJsonNumber) extends Json
private case class JString(s: String) extends Json
private case class JBool(b: Boolean) extends Json
private case object JNull extends Json
private case class JObject(o: JsonObject) extends Json
private case class JArray(a: JsonArray) extends Json

object Json extends Jsons {
  def apply(fields: (JsonField, Json)*): Json = {
    jObjectAssociationList(fields.toList)
  }
}

trait Jsons {
  type JsonBoolean = Boolean
  type JsonArray = List[Json]
  type JsonString = String
  type JsonField = String
  type JsonAssociation = (JsonField, Json)
  type JsonAssociationList = List[JsonAssociation]

  def jObjectAssociationList(js: JsonAssociationList): Json = {
    JObject(JsonObject.fromIterable(js))
  }
}
