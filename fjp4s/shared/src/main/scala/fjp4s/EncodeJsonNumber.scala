package fjp4s

object EncodeJsonNumber {

  given EncodeJsonNumber[Int] with
    override def encodeJsonNumber(value: Int): MyJsons.MyJsonNumber =
      MyJsons.MyInt(value)
}
trait EncodeJsonNumber[T] { self =>
  def encodeJsonNumber(value: T): MyJsons.MyJsonNumber
}
