package fjp4s

extension (s:String){
  def :=[A:EncodeJson](a:A):(String,Json) = (s,summon[EncodeJson[A]].apply(a))

  def :?=[A: EncodeJson](a:Option[A]):(String,Json)= (s,summon[EncodeJson[Option[A]]].apply(a))

  def parseOption :Option[Json] = JsonParser.parse(s).toOption
}