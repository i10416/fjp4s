package fjp4s
package internal
import scala.deriving.Mirror
import scala.compiletime.{constValue, erasedValue, summonFrom,summonAll}
object Macros {
  inline def summonEncoder[T]: EncodeJson[T] =
    summonFrom {
      // if EncodeJson[T] exists in implicit scope,returns it
      case x: EncodeJson[T] => x
      // if T is defined as case class, recursively derive EncodeJson[T]
      case _:Mirror.ProductOf[T] => Macros.derivedEncoder[T]
    }
  // derivedEncoder returns EncodeJson[T<:Product] derived by macro
  // T => EncodeJson[T]
  inline given productEncodeJson[A  <: Product](using m: Mirror.ProductOf[A]) :EncodeJson[A] with {
    type F = Tuple.Map[m.MirroredElemTypes,EncodeJson]
    val encoders = summonAll[F].toList.asInstanceOf[List[EncodeJson[Any]]]
    def encode(a:A) :Json = {
      JObject(
        a.productElementNames
          .zip(encoders)
          .zip(a.productIterator)
          .map{ case ((key,encoder),member)=> (key,encoder.apply(member))}
          .foldLeft(JsonObject.empty){(acc,field)=> acc :+field}
      )
    }
  }


  def derivedEncoder[T](using A: Mirror.ProductOf[T]) : EncodeJson[T]= {
    inline val elemEncoders:Array[EncodeJson[Any]]= Macros.summonEncodeJsonRec[A.MirroredElemTypes].toArray
    given encoder :EncodeJson[T]  with {
      override def encode(a:T):Json ={
        val p = a.asInstanceOf[Product]
        JObject(createJsonObject(p))
      }
      def createJsonObject(value:Product,jsonObject:JsonObject = JsonObject.empty):JsonObject = {
        def encodeWith(index: Int)(p: Any): (String, Json) = {
          (value.productElementName(index), elemEncoders(index).asInstanceOf[EncodeJson[Any]].apply(p))
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
  inline def summonEncodeJsonRec[T<:Tuple] :List[EncodeJson[Any]] = {
    inline erasedValue[T] match {
      case _:EmptyTuple => Nil
      case _: (t *: ts) => summonEncoder[t] :: summonEncodeJsonRec[ts]
    }
  }

}