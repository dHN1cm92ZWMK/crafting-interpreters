package craftinginterpreters

@main def main() = {
	val s = "nozio"
	println("nozio")

	val s2 = s"${s}-2"
	println(s2)
}
/*
import zio.*

object Main extends ZIOAppDefault {
	def run = for {
		_ <- ZIO.attempt(println("Hello"))

	} yield ()
}

*/