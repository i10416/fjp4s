package fjp4s

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class CursorSpec extends AnyFlatSpec with Matchers:
  val j = Json("key":=1)  
  Cursor.root(j).context.toList shouldBe Context.empty.toList
  Cursor.root(j).focus shouldBe j
  Cursor.root(j).downfield("key") == Some(Cursor.obj(Cursor.root(j),false,_,("key",JNumber(1.asJsonNumber))))
  Cursor.root(j).downfield("key").map(_.focus) shouldBe Some(JNumber(1.asJsonNumber))
  val j2 = Cursor.root(Json("key":=Json("key2":=1)))
  for {
    j_0 <-j2.downfield("key")
    j_1 <- j_0.downfield("key2")
  } yield j_1.focus shouldBe 1.asJson