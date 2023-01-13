import java.nio.file.{Files, Path, Paths}
import scala.io.StdIn

def interpreter(): Unit = {
  val scanner = NaiveScanner
  println("interpreter")
  while (true) {
    val line = StdIn.readLine("> ")
    //println(s"will run >$line<")
    val tokens = scanner.scanTokens(line)
    println(s"tokens:\n$tokens")
    if (line == "quit")
      return
  }
}


def run(path: Path) = {
  val scanner = NaiveScanner

  println(s"running $path")
  if (Files.exists(path)) {
    println("exists")
    val content = Files.readString(path)
    println(s"content:\n$content")
    val tokens = scanner.scanTokens(content)
    println(s"tokens:\n$tokens")
  }
  else
     println("not found")
}

@main def main(args: String*) = {
  if (args.isEmpty) interpreter()
  else args.map(Path.of(_)).foreach(run)
}
