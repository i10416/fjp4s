package fjp4s

import Json._

sealed abstract class JsonObject:
  def apply(f: JsonField): Option[Json]
  def +(f: JsonField, j: Json): JsonObject
  def :+(pair: (JsonField, Json)): JsonObject
  def map(f: Json => Json): JsonObject

private case class JsonObjectInstance(
    fieldsMap: Map[JsonField, Json] = Map.empty
) extends JsonObject:

  def apply(f: JsonField) = fieldsMap.get(f)

  def +(key: JsonField, value: Json): JsonObject =
    copy(fieldsMap = fieldsMap.updated(key, value))

  def :+(pair: (JsonField, Json)): JsonObject =
    this.+(pair._1, pair._2)
  def map(f: Json => Json): JsonObject =
    copy(fieldsMap = fieldsMap.foldLeft(Map.empty[JsonField, Json]) {
      case (acc, (key, value)) => acc.updated(key, f(value))
    })

  override def equals(o: Any) =
    o match
      case JsonObjectInstance(otherMap) => fieldsMap == otherMap
      case _                            => false

  override def hashCode = fieldsMap.hashCode

object JsonObject extends JsonObjects:
  def empty: JsonObject = JsonObjectInstance()
  def single(f: JsonField, j: Json): JsonObject = JsonObject.empty + (f, j)
  def fromIterable(js: Iterable[(JsonField, Json)]) = js.foldLeft(empty)(_ :+ _)

trait JsonObjects {}
