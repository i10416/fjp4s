package fjp4s

object EncodeJsonMacro {
  inline def derive[A]: EncodeJson[A] =
    internal.Macros.summonEncoder[A]
}
