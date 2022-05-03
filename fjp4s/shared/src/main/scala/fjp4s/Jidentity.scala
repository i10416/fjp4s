package fjp4s

extension [T <: Int | Float | Double: EncodeJsonNumber](j: T)
  def asJsonNumber = summon[EncodeJsonNumber[T]].encodeJsonNumber(j)

extension [T](j: T)
  def asJson(using EncodeJson[T]): Json = summon[EncodeJson[T]].apply(j)
