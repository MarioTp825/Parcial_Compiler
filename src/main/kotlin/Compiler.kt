import kotlin.math.sin

class Compiler {
    var content = ""
    var signTable = mutableMapOf<Int, String>()
    val sign = mutableListOf<String>()
    val tree = mutableMapOf<String, Root>()
    var firstIndex = ""
    var lastIndex = ""

    fun stepOne(code: String): String {
        val words = mutableListOf<String>()
        val ids = mutableListOf<Int>()
        var pos = 0
        for (letter in code.trim()) {
            if (letter.let { !it.isLetter() && !it.isDigit() && it != ' ' }) {
                sign.add(letter.toString())
                pos++
                continue
            }

            if (pos < words.size) {
                words[pos] += letter.toString().trim()
            } else {
                words.add(letter.toString().trim())
                ids.add(pos + 1)
            }
        }
        var content = ""
        for (i in 0 until words.size) {
            content += words[i].let {
                signTable[i] = it
                if (it.isNum()) "<$it>" else "<$i,$it>"
            } + if (i < sign.size) " ${sign[i]} " else ""
        }
        this.content = content
        return "Analisis lexico:\n$content\n"
    }

    fun stepTwo(): String {
        var answer = "Analisis sintactico \n"

        var anchor = sign[0]
        for (i in 0 until (signTable.size - 1)) {
            if((i + 1) < sign.size) {
                tree[anchor] = Root(signTable[i]!!, sign[i + 1])
                anchor = sign[i + 1]
            } else
                tree[anchor] =
                    Root(signTable[i]!!, signTable[i + 1]!!)
        }
        for (value in tree) {
            answer += value.key + " -> [" + value.value.leafOne + "] [" + value.value.leafTwo + "] \n"
        }
        return answer
    }

    fun stepThree(): String {
        var answer = "Analisis semantico \n"

        var anchor = sign[0]
        firstIndex = anchor
        for (i in 0 until (signTable.size - 1)) {
            if((i + 1) < sign.size) {
                tree[anchor] = Root(
                    signTable[i]!!.let {
                        if(it.isNumber()) {
                            tree["ToFloat"] = Root("ToFloat", it.toFloatOrNull().toString())
                            "ToFloat"
                        } else it
                    }
                    , sign[i + 1])
                anchor = sign[i + 1]
            } else
                tree[anchor] =
                    Root(
                        signTable[i]!!.let {
                            if(it.isNumber()) {
                                tree["ToFloat"] = Root("", it.toFloatOrNull().toString())
                                "ToFloat"
                            } else it },
                        signTable[i + 1]!!.let { if(it.isNumber()) {
                            tree["ToFloat"] = Root("", it.toFloatOrNull().toString())
                            "ToFloat"
                        } else it })
        }
        for (value in tree) {
            answer += value.key + " -> [" + value.value.leafOne + "] [" + value.value.leafTwo + "] \n"
            lastIndex = value.key
        }
        return answer
    }

    fun stepFour(): String {
        var ans = "Código Intermedio \n"
        var anchor = lastIndex
        var i = 1
        while (i <= tree.size) {
            val node = tree[anchor]!!
            ans += if(anchor != "=") {
                "t$i = "
            }else {
                ""
            } + if(node.singleLeaf()) {
                "$anchor(${node.getSingleLeaf()})"
            }
            else
                "${node.leafOne} $anchor t${i - 1}"
            ans += "\n"
            i++
            anchor = findNode(anchor)
        }
        return ans
    }

    private fun findNode(node: String):String {
        for(value in tree) {
            if(value.value.leafOne == node || value.value.leafTwo == node)
                return value.key
        }
        return ""
    }

    private fun String.isNum(): Boolean = this
        .removePrefix("-")
        .removePrefix("+")
        .all { it in '0'..'9' }

    private fun String.isNumber(): Boolean = this.toFloatOrNull() != null
}