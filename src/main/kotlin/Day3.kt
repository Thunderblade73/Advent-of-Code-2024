import java.io.File

fun day3(){
    val file = File("src/main/resources/Day3Input.txt")

    val rawData = file.readText()

    val regex = "mul\\((\\d{1,3}),(\\d{1,3})\\)".toRegex()

    val result = regex.findAll(rawData).sumOf { it.groupValues[1].toInt() * it.groupValues[2].toInt() }
    println(result)

    val doRegex = "do\\(\\)".toRegex()
    val dontRegex = "don't\\(\\)".toRegex()

    var i = 0
    var enable = true
    var sum = 0

    do {
        val doMatch = doRegex.find(rawData,i)
        val dontMatch = dontRegex.find(rawData,i)
        val mulMatch = regex.find(rawData,i)

        val matchList = listOf<MatchResult?>(doMatch,dontMatch,mulMatch)

        val min = matchList.minBy { it?.range?.start ?: Int.MAX_VALUE } ?: break

        when(min){
            doMatch -> enable = true
            dontMatch -> enable = false
            mulMatch -> if(enable){
                sum += mulMatch.groupValues[1].toInt() * mulMatch.groupValues[2].toInt()
            }
        }

        i = min.range.last

    }while (i < rawData.length)

    println(sum)
}