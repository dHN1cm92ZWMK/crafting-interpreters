import Scanner.Token

trait Scanner {
  def scanTokens(s: String): List[Token]
}
object Scanner {
  sealed trait Token
  case object Print extends Token
  case object Var extends Token
  case object False extends Token
  case object True extends Token
  case object Assignment extends Token
  case object Plus extends Token
  case object Minus extends Token
  case object LeftPar extends Token
  case object RightPar extends Token
  case object Mul extends Token
  case object Div extends Token
  case object Eq extends Token
  case object NonEq extends Token
  case object Not extends Token
  case object Semicolon extends Token
  case class StringToken(value: String) extends Token
  case class Identifier(id: String) extends Token
  case class Number(value: Double) extends Token

}
