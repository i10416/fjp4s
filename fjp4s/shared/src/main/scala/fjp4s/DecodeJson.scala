package fjp4s
trait DecodeJson[A]:
  def apply(c: Cursor): Decoding.DecodeResult[A] = decode(c)
  def decode(c: Cursor): Decoding.DecodeResult[A]

object DecodeJson extends DecodeJsons:
  def of[T: DecodeJson]: DecodeJson[T] = summon[DecodeJson[T]]

trait DecodeJsons:
  import MyJsons.*
  given DecodeJson[String] with
    def decode(c: Cursor) = c.focus.string match
      case None    => Decoding.DecodeResult.fail("decode string failed")
      case Some(s) => Decoding.DecodeResult.ok(s)
  given DecodeJson[Int] with
    def decode(c: Cursor) = c.focus.number match
      case None         => Decoding.DecodeResult.fail("decode int failed")
      case Some(i: Int) => Decoding.DecodeResult.ok(i)
      case Some(_)      => Decoding.DecodeResult.fail("decode number failed")
