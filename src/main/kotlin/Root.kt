data class Root(
    var leafOne: String,
    var leafTwo: String
) {
    fun singleLeaf(): Boolean {
        return leafOne == "" || leafTwo == ""
    }

    fun getSingleLeaf(): String {
        return if(leafOne != "") leafOne else leafTwo
    }
}