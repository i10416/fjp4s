package fjp4s

import Json._

sealed abstract class Context {
  val toList: List[ContextElement]
  def +:(e: ContextElement): Context = Context.build(e :: toList)
}

object Context extends Contexts {
  def empty: Context = new Context {
    val toList = Nil
  }
}

trait Contexts {
  def build(x: List[ContextElement]): Context =
    new Context {
      val toList = x
    }
}

private enum ContextElement extends Serializable {
  case ArrayContext(n: Int, j: Json) extends ContextElement
  case ObjectContext(f: JsonField, j: Json) extends ContextElement
  def json: Json = this match {
    case ArrayContext(_, json)  => json
    case ObjectContext(_, json) => json
  }

  def field: Option[JsonField] =
    this match {
      case ArrayContext(_, _)  => None
      case ObjectContext(f, _) => Some(f)
    }
}
