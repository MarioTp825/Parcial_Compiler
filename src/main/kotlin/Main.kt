import java.util.Scanner

fun main(args: Array<String>) {
        val sc = Scanner(System.`in`)
        val compiler = Compiler()
        println("Ingrese los datos a compilar:")
        val content = sc.nextLine()

        println(compiler.stepOne(content))
        println(compiler.stepTwo())
        println(compiler.stepThree())
    }
//posicion = inicial + velocidad * 60
