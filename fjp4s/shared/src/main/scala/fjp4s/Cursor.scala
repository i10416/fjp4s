package fjp4s

import Json._
enum Cursor extends Serializable {
  
  private case CJson(j:Json) 
  private case CObject(p:Cursor,u:Boolean,o:JsonObject,x:(JsonField,Json))

     def context: Context  = this match {
      case CJson(_) => Context.empty
    //  case CArray(prev,_,l,j,r) => ArrayContext(l.length,j) +: prev.context
      case CObject(p,_,_,(f,j)) => ContextElement.ObjectContext(f,j) +: p.context 
    }

    def downfield(q:JsonField):Option[Cursor] = for{
        subtree <- focus.obj
        jsonValue   <- subtree(q)
       } yield CObject(this,false,subtree,(q,jsonValue))

  
    def focus= this match {
        case CJson(j) => j
     //   case CArray(_,_,_,j,_) => j
        case CObject(_,_,o,(q,j)) => j
      }

    def as[T:DecodeJson] = summon[DecodeJson[T]].decode(this) 
    
}
object Cursor {
  def root(j:Json) = Cursor.CJson(j)
  def obj(p:Cursor,u:Boolean,o:JsonObject,x:(JsonField,Json)) 
    = Cursor.CObject(p:Cursor,u:Boolean,o:JsonObject,x:(JsonField,Json))
}
