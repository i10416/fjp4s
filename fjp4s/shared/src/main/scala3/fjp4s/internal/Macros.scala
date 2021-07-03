package fjp4s
package internal
import scala.deriving.Mirror
import scala.compiletime.{constValue, erasedValue, summonFrom, summonAll}
import scala.quoted.Type
import scala.quoted.Quotes
import scala.quoted.Expr
object Macros {
  inline def summonEncoder[T]: EncodeJson[T] =
    summonFrom {
      case x: Mirror.ProductOf[T] => Macros.deriveEncoder[T]
      case x: EncodeJson[T]       => x

    }
  // derivedEncoder returns EncodeJson[T<:Product] derived by macro
  // T => EncodeJson[T]

  inline def deriveEncoder[T](using m: Mirror.ProductOf[T]): EncodeJson[T] = {
    inline given en: EncodeJson[T] with {
      type F = Tuple.Map[m.MirroredElemTypes, EncodeJson]
      def encoders = summonAll[F].toList.asInstanceOf[List[EncodeJson[?]]]
      def encode(a: T): Json = {
        JObject(
          a.asInstanceOf[Product]
            .productElementNames
            .zip(encoders)
            .zip(a.asInstanceOf[Product].productIterator)
            .map { case ((key, encoder), member) =>
              (key, encoder.asInstanceOf[EncodeJson[Any]].apply(member))
            }
            .foldLeft(JsonObject.empty) { (acc, field) => acc :+ field }
        )
      }
    }
    en
  }

  inline def summonEncoders[T <: Tuple]: Array[EncodeJson[?]] = {
    summonEncodeJsonRec[T].toArray
  }

  inline def derivedEncoder[T](using
      inline A: Mirror.ProductOf[T]
  ): EncodeJson[T] = {
    given encoder: EncodeJson[T] with {
      val elemEncoders: Array[EncodeJson[?]] =
        summonEncoders[A.MirroredElemTypes]
      override def encode(a: T): Json = {
        val p = a.asInstanceOf[Product]
        JObject(createJsonObject(p))
      }
      def createJsonObject(
          value: Product,
          jsonObject: JsonObject = JsonObject.empty
      ): JsonObject = {
        def encodeWith(index: Int)(p: Any): (String, Json) = {
          (
            value.productElementName(index),
            elemEncoders(index).asInstanceOf[EncodeJson[Any]].apply(p)
          )
        }
        val elems: Iterator[Any] = value.productIterator
        def loop(i: Int, acc: JsonObject): JsonObject = {
          if (elems.hasNext) {
            val field = encodeWith(i)(elems.next())
            loop(i + 1, acc :+ field)
          } else {
            acc
          }
        }
        loop(0, JsonObject.empty)
      }
    }
    encoder
  }

  // map Types in tuple to list of EncodeJson[Type_k]
  // (v_1,v_2,...,v_n): (T_1,T_2,...,T_n)  ==> List(EncodeJson[T_1],EncodeJson[T_2],...,EncodeJson[T_n])
  inline def summonEncodeJsonRec[T <: Tuple]: List[EncodeJson[Any]] = {
    inline erasedValue[T] match {
      case _: EmptyTuple => Nil
      case _: (t *: ts) => /*summonEncoder[t].asInstanceOf[EncodeJson[Any]] ::*/
        summonEncodeJsonRec[ts]
    }
  }

}
