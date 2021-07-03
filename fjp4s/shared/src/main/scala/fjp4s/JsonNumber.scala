package fjp4s

object MyJsons {
  type MyJsonNumber = Int | Double | Float
  opaque type MyInt <: MyJsonNumber = Int
  opaque type MyDouble <: MyJsonNumber = Double
  opaque type MyFloat <: MyJsonNumber = Float
  object MyInt {
    def apply(i: Int): MyInt = i
  }

  trait ToBigDecimal[T <: MyJsonNumber] {
    def apply(i: T): BigDecimal
  }
  given ToBigDecimal[Int] with
    def apply(i: Int) = BigDecimal(i)

  given ToBigDecimal[MyInt] with
    def apply(i: MyInt) = BigDecimal(i)

  extension [T <: MyJsonNumber: ToBigDecimal](i: T)
    def asJson: Json = JNumber(i)
    def toBigDecimal = summon[ToBigDecimal[T]].apply(i)
}
