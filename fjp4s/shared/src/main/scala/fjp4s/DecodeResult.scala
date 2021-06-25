package fjp4s

object Decoding{
  
  object DecodeResult {
    def fail[A](e:String):DecodeResult[A] = Left(e)
    def ok[A](value:A):DecodeResult[A] = Right(value)
    def unapply[A](result:Either[String,A]) = result
  }
  opaque type DecodeResult[A]  = Either[String,A]
  extension [A](i:DecodeResult[A]){
    def flatMap[B](f: A => DecodeResult[B]): DecodeResult[B] =
      i.flatMap(f(_))
    def map[B](f: A => B): DecodeResult[B] =
      i.map(f)
  }
}