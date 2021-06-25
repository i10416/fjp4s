package fjp4s
import scala.collection.mutable.Builder
import java.lang.StringBuilder
object JsonParser {
  import Json._

  private type TokenSource = String
  private val unexpectedTermination = Left("unexpected termination")

  case class JsonObjectBuilder(
      var fieldsMapBuilder: Builder[(JsonField, Json), Map[JsonField, Json]] =
        Map.newBuilder
  ) {
    private var isEmpty: Boolean = true
    def add(key: JsonField, value: Json): JsonObjectBuilder = {
      isEmpty = false
      fieldsMapBuilder += ((key, value))
      this
    }
    def build(): Json = {
      if (isEmpty) Json()
      else JObject(JsonObjectInstance(fieldsMapBuilder.result()))
    }
  }

  def parse(json: String): Either[String, Json] = {
    val jsonLength = json.length

    def validSuffixContent(from: Int): Boolean = {
      if (from >= jsonLength) true
      else
        json(from) match {
          case ' ' | '\r' | '\n' | '\t' => validSuffixContent(from + 1)
          case _                        => false
        }
    }

    val parseResult: ((Int, Json)) => Either[String, Json] = (result) => {
      result match {
        case (`jsonLength`, j)                             => Right(j)
        case (remains, j) if (validSuffixContent(remains)) => Right(j)
        case _ => Left("invalid json")
      }
    }
    expectValue(json, 0).flatMap(parseResult)
  }
  /*private*/
  final def expectValue(
      stream: TokenSource,
      position: Int = 0
  ): Either[String, (Int, Json)] = {
    if (position >= stream.length) unexpectedTermination
    else
      stream(position) match {
        case '[' => expectArray(stream, position + 1)
        case '{' =>
          expectObject(stream, position + 1)
        case '"' =>
          expectStringNoStartBounds(stream, position + 1).map((pos, s) =>
            (pos, JString(s))
          )
        case 't' if stream.startsWith("true", position) =>
          Right((position + 4, JBool(true)))
        case 'f' if stream.startsWith("false", position) =>
          Right((position + 5), JBool(false))
        case 'n' if stream.startsWith("null", position) =>
          Right((position + 4), JNull)
        case ' ' | '\r' | '\n' | '\t' => expectValue(stream, position + 1)
        case _                        => ???
      }
  }

  private[this] final def expectEntrySeparator(
      stream: TokenSource,
      position: Int
  ) =
    expectedSpacerToken(stream, position, ',', "Expected entry separator token")

  private def expectStringBounds(stream: TokenSource, position: Int) =
    expectedSpacerToken(stream, position, '"', "Expected string bounds")

  private[this] final def expectFieldSeparator(
      stream: TokenSource,
      position: Int
  ) =
    expectedSpacerToken(stream, position, ':', "Expected field separator token")

  private def expectedSpacerToken(
      stream: TokenSource,
      position: Int,
      token: Char,
      message: String
  ): Either[String, Int] = {
    if (position >= stream.length) unexpectedTermination
    else
      stream(position) match {
        case ' ' | '\r' | '\n' | '\t' =>
          expectedSpacerToken(stream, position + 1, token, message)
        case `token` => Right(position + 1)
        case _       => Left(message)
      }
  }
  private def expectStringNoStartBounds(
      stream: TokenSource,
      position: Int
  ): Either[String, (Int, String)] = {
    collectStringParts(stream, position).map((pos, sb) => (pos, sb.toString))
  }
  private def expectString(
      stream: TokenSource,
      position: Int
  ): Either[String, (Int, String)] = {
    expectStringBounds(stream, position).flatMap(pos =>
      expectStringNoStartBounds(stream, pos)
    )
  }
  private def collectStringParts(
      stream: TokenSource,
      position: Int,
      workingString: StringBuilder = new StringBuilder()
  ): Either[String, (Int, StringBuilder)] = {
    def unsafeNormalCharIndex(idx: Int): Int = {
      val char = stream(idx)
      if (char == '"') idx
      else unsafeNormalCharIndex(idx + 1)
    }
    if (position >= stream.length) unexpectedTermination
    else
      stream(position) match {
        case '"' => Right(position + 1, workingString)
        case other => {
          val normalCharEnd = unsafeNormalCharIndex(position)
          collectStringParts(
            stream,
            normalCharEnd,
            workingString.append(stream, position, normalCharEnd)
          )
        }
      }
  }
  private def expectArray(
      stream: TokenSource,
      position: Int,
      first: Boolean = true,
      fields: Builder[Json, List[Json]] = List.newBuilder
  ): Either[String, (Int, Json)] = {
    if (position >= stream.length) unexpectedTermination
    else
      stream(position) match {
        case ']' => Right((position + 1, JArray(fields.result())))
        case ' ' | '\r' | '\n' | '\t' =>
          expectArray(stream, position + 1, first, fields)
        case _ => Left("failed")
      }
  }

  private def expectObject(
      stream: TokenSource,
      position: Int,
      first: Boolean = true,
      fields: JsonObjectBuilder = new JsonObjectBuilder()
  ): Either[String, (Int, Json)] = {

    if (position >= stream.length) unexpectedTermination
    else
      stream(position) match {
        case '}' => Right(position + 1, fields.build())
        case ' ' | '\r' | '\n' =>
          expectObject(stream, position + 1, first, fields)
        case _ => {
          val next = for {
            afterEntrySeparator <-
              (if (first) Right(position)
               else expectEntrySeparator(stream, position))
            key <- expectString(stream, afterEntrySeparator)
            afterFieldSeparator <- expectFieldSeparator(
              stream,
              key._1
            )
            maybeValue <- expectValue(stream, afterFieldSeparator)
          } yield (maybeValue._1, fields.add(key._2, maybeValue._2))
          next match {
            case Right((pos, fields)) =>
              expectObject(stream, pos, false, fields)
            case Left(failure) => Left(failure)
          }
        }
      }
  }
}
