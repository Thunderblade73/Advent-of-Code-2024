import java.io.File

fun day4() {
    val file = File("src/main/resources/Day4Input.txt")

    val rawData = mutableListOf<String>()
    file.forEachLine { rawData.add(it) }

    var count = 0

    rawData.forEachIndexed { lineIndex, line ->
        line.forEachIndexed { charIndex, c ->
            if (c == 'X') {
                count += rawData.countXmas(lineIndex, charIndex)
            }
        }
    }

    println(count)

    count = 0

    rawData.forEachIndexed { lineIndex, line ->
        line.forEachIndexed { charIndex, c ->
            if (c == 'A') {
                count += rawData.countXthmas(lineIndex, charIndex)
            }
        }
    }

    println(count)

}

private fun List<String>.countXthmas(lineIndex: Int, charIndex: Int): Int {
    val topLeft = getOrNull(lineIndex - 1)?.getOrNull(charIndex - 1)
    val topRight = getOrNull(lineIndex - 1)?.getOrNull(charIndex + 1)
    val botLeft = getOrNull(lineIndex + 1)?.getOrNull(charIndex - 1)
    val botRight = getOrNull(lineIndex + 1)?.getOrNull(charIndex + 1)

    if (topLeft == null || topRight == null || botLeft == null || botRight == null) return 0
    return when(Quad(topLeft,topRight,botLeft,botRight)){
        Quad('M','M','S','S') -> 1
        Quad('S','S','M','M') -> 1
        Quad('S','M','S','M') -> 1
        Quad('M','S','M','S') -> 1
        else -> 0
    }
}

private class Quad<T>(val a: T,val b: T,val c: T,val d: T) {
    override fun hashCode(): Int = a.hashCode() + 31*b.hashCode() + 31*31*c.hashCode()+31*31*31*d.hashCode()

    override fun equals(other: Any?): Boolean {
        if(other !is Quad<*>) return false
        return a == other.a && b == other.b && c == other.c && d == other.d
    }
}

private fun List<String>.countXmas(lineIndex: Int, charIndex: Int): Int {
    var count = 0

    get(lineIndex).let { line ->
        //Forward
        if ((charIndex + 1..charIndex + 3).map { line.getOrNull(it) }.equalsMAS()) count++
        //Backward
        if ((charIndex - 3..<charIndex).map { line.getOrNull(it) }.reversed().equalsMAS()) count++
    }

    //Bottom
    if ((lineIndex + 1..lineIndex + 3).map { getOrNull(it)?.getOrNull(charIndex) }.equalsMAS()) count++

    //BottomRight
    if ((lineIndex + 1..lineIndex + 3).map { getOrNull(it)?.getOrNull(charIndex - lineIndex + it) }.equalsMAS()) count++

    //BottomLeft
    if ((lineIndex + 1..lineIndex + 3).map { getOrNull(it)?.getOrNull(charIndex + lineIndex - it) }.equalsMAS()) count++

    //Top
    if ((lineIndex - 3..<lineIndex).map { getOrNull(it)?.getOrNull(charIndex) }.reversed().equalsMAS()) count++

    //TopLeft
    if ((lineIndex - 3..<lineIndex).map { getOrNull(it)?.getOrNull(charIndex - lineIndex + it) }.reversed()
            .equalsMAS()
    ) count++

    //TopRight
    if ((lineIndex - 3..<lineIndex).map { getOrNull(it)?.getOrNull(charIndex + lineIndex - it) }.reversed()
            .equalsMAS()
    ) count++

    return count
}

private fun List<Char?>.equalsMAS(): Boolean = getOrNull(0) == 'M' && getOrNull(1) == 'A' && getOrNull(2) == 'S'