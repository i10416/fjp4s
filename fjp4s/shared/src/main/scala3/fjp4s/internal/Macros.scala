package fjp4s

object Macros {
  // 再帰的に EncodeJson[T<:Product] が欲しい
  // field 名は p.productElementName(index)でとれる.
  private def createJsonObject(value:Product,jsonObject:JsonObject.empty):JsonObject = {
    val keys = value.productElementNames
    // member の値のリスト
    val elems = value.productIterator
    ???
  }
}