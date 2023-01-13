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

  def scanTokens(str: String): List[Token] = {
    def tokenize(str: String, acc: String, flags: Flags): List[Token] = {
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
            tokenize(rest, next, Flags.Id)
          else if (peek.exists(_.isDigit)) // digit -> start number
            tokenize(rest, next, Flags.Num)
          else if (peek.contains('"')) // " -> start string
            tokenize(rest, "", Flags.Str)
          else if (peek.contains('/'))
            tokenize(rest, next, Flags.Slash)
          else if (peek.contains('='))
            tokenize(rest, next, Flags.Eq)
          else if (peek.contains('!'))
            tokenize(rest, next, Flags.Excl)
          else peek match {
            case Some('(') => LeftPar :: tokenize(rest, "", flags)
            case Some(')') => RightPar :: tokenize(rest, "", flags)
            case Some('+') => Plus :: tokenize(rest, "", flags)
            case Some('-') => Minus :: tokenize(rest, "", flags)
            case Some('*') => Mul :: tokenize(rest, "", flags)
            case Some(';') => Semicolon :: tokenize(rest, "", flags)
            case Some(ws) if ws.isWhitespace => tokenize(rest, "", flags)
            case _ => throw new RuntimeException(s"can't resolve >$acc<")
          }
        } else if (flags.number) { // number -> try eat number
          if (peek.exists(_.isDigit) || peek.contains('.'))
            tokenize(rest, s"$acc${peek.get}", flags)
          else
            Number(acc.toDouble) :: tokenize(str, "", Flags.None)
        } else if (flags.string) { // string -> if not ", eat otherwise emit string
          if (peek.contains('"'))
            StringToken(s"$acc") :: tokenize(rest, "", Flags.None)
          else
            tokenize(rest, s"$acc$next", flags)
        } else if (flags.identifier) { // identifier -> eat next if possible otherwise emit identifier
          if (peek.exists(_.isDigit) || peek.exists(_.isLetter) || peek.contains('_'))
            tokenize(rest, s"$acc$next", flags)
          else acc match {
            case "print" =>
              Print :: tokenize(str, "", Flags.None)
            case "var" =>
              Var :: tokenize(str, "", Flags.None)
            case "true" =>
              True :: tokenize(str, "", Flags.None)
            case "false" =>
              False :: tokenize(str, "", Flags.None)
            case _ =>
              Identifier(acc) :: tokenize(str, "", Flags.None)
          }
        } else if (flags.slash) {
          if (peek.contains('/')) // comment
            tokenize(str.dropWhile(_ != '\n'), "", Flags.None)
          else
            Div :: tokenize(str, "", Flags.None)
        } else if (flags.eq) {
          if (peek.contains('='))
            Eq :: tokenize(str, "", Flags.None)
          else
            Assignment :: tokenize(str, "", Flags.None)
        } else if (flags.excl) {
          if (peek.contains('='))
            NonEq :: tokenize(str, "", Flags.None)
          else
            Not :: tokenize(str, "", Flags.None)
        }
        else
          throw new RuntimeException("?")
      }
    }

    tokenize(str, "", Flags.None)
  }
}
