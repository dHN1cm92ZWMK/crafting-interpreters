import Scanner.Token

object NaiveScanner extends Scanner {

  import Scanner._
  case class Flags(identifier: Boolean= false,
                   string: Boolean= false,
                   number: Boolean= false,
                   slash: Boolean= false,
                   eq: Boolean = false,
                   excl: Boolean = false
                  )
  object Flags {
    val None = Flags()
    val Id = Flags(identifier = true)
    val Str = Flags(string = true)
    val Num = Flags(number = true)
    val Slash = Flags(slash = true)
    val Eq = Flags(eq = true)
    val Excl = Flags(excl = true)
  }

  def scanTokens(sourceCode: String): List[Token] = {
    def getNeighborhood(pos: Int) = s"pos=$pos, neighborhood: '${sourceCode.slice(pos - 5, pos + 5)}'";
    def tokenize(str: String, acc: String, flags: Flags, pos: Int): List[Token] = {
      if (str.isEmpty && acc.isEmpty)
        Nil
      else {
        val peek = str.headOption
        val next = peek.map(c => s"$c").getOrElse("")
        val rest = str.drop(1)

        //println(s"next=>$next<, flags:$flags")

        // todo escape \"
        if (flags == Flags.None) {
          if (peek.exists(_.isLetter) || peek.contains('_')) // letter or _ -> start identifier
            tokenize(rest, next, Flags.Id, pos + 1)
          else if (peek.exists(_.isDigit)) // digit -> start number
            tokenize(rest, next, Flags.Num, pos + 1)
          else if (peek.contains('"')) // " -> start string
            tokenize(rest, "", Flags.Str, pos + 1)
          else if (peek.contains('/'))
            tokenize(rest, next, Flags.Slash, pos + 1)
          else if (peek.contains('='))
            tokenize(rest, next, Flags.Eq, pos + 1)
          else if (peek.contains('!'))
            tokenize(rest, next, Flags.Excl, pos + 1)
          else peek match {
            case Some('(') => LeftPar :: tokenize(rest, "", flags, pos + 1)
            case Some(')') => RightPar :: tokenize(rest, "", flags, pos + 1)
            case Some('+') => Plus :: tokenize(rest, "", flags, pos + 1)
            case Some('-') => Minus :: tokenize(rest, "", flags, pos + 1)
            case Some('*') => Mul :: tokenize(rest, "", flags, pos + 1)
            case Some(';') => Semicolon :: tokenize(rest, "", flags, pos + 1)
            case Some(ws) if ws.isWhitespace => tokenize(rest, "", flags, pos + 1)
            case Some(ch) => throw new RuntimeException(s"Unknown character >$ch<, ${getNeighborhood(pos)}")
            case _ => throw new RuntimeException(s"Shouldn't be here")
          }
        } else if (flags.number) { // number -> try eat number
          if (peek.exists(_.isDigit) || peek.contains('.'))
            tokenize(rest, s"$acc${peek.get}", flags, pos + 1)
          else
            Number(acc.toDouble) :: tokenize(str, "", Flags.None, pos + 1)
        } else if (flags.string) { // string -> if not ", eat otherwise emit string
          if (peek.contains('"'))
            StringToken(s"$acc") :: tokenize(rest, "", Flags.None, pos + 1)
          else if (peek.isDefined)
            tokenize(rest, s"$acc$next", flags, pos + 1)
          else throw new RuntimeException(s"Unfinished string ${getNeighborhood(pos)}")
        } else if (flags.identifier) { // identifier -> eat next if possible otherwise emit identifier
          if (peek.exists(_.isDigit) || peek.exists(_.isLetter) || peek.contains('_'))
            tokenize(rest, s"$acc$next", flags, pos + 1)
          else acc match {
            case "print" =>
              Print :: tokenize(str, "", Flags.None, pos + 1)
            case "var" =>
              Var :: tokenize(str, "", Flags.None, pos + 1)
            case "true" =>
              True :: tokenize(str, "", Flags.None, pos + 1)
            case "false" =>
              False :: tokenize(str, "", Flags.None, pos + 1)
            case _ =>
              Identifier(acc) :: tokenize(str, "", Flags.None, pos + 1)
          }
        } else if (flags.slash) {
          if (peek.contains('/')) // comment
            tokenize(str.dropWhile(_ != '\n'), "", Flags.None, pos + 1)
          else
            Div :: tokenize(str, "", Flags.None, pos + 1)
        } else if (flags.eq) {
          if (peek.contains('='))
            Eq :: tokenize(str, "", Flags.None, pos + 1)
          else
            Assignment :: tokenize(str, "", Flags.None, pos + 1)
        } else if (flags.excl) {
          if (peek.contains('='))
            NonEq :: tokenize(str, "", Flags.None, pos + 1)
          else
            Not :: tokenize(str, "", Flags.None, pos + 1)
        }
        else
          throw new RuntimeException(s"Unknown flag state $flags")
      }
    }

    tokenize(sourceCode, "", Flags.None, 0)
  }
}
