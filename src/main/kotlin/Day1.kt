import java.io.File
import kotlin.math.abs

fun day1(){
    val file = File("src/main/resources/Day1Input.txt")

    val list1 = mutableListOf<Int>()
    val list2 = mutableListOf<Int>()

    val regex = "(\\d+) *(\\d+)".toRegex()

    file.forEachLine {
        val matches = regex.matchEntire(it)?.groupValues
        if(matches != null){
            list1.add(matches[1].toInt())
            list2.add(matches[2].toInt())
        }
    }

    list1.sort()
    list2.sort()

    val result1 = list1.zip(list2){a,b-> abs(a-b) }.sum()
    println(result1)

    val mapOf2 = list2.groupBy { it }.mapValues { it.value.count() }

    val result2 = list1.sumOf { it * (mapOf2[it]?: 0) }
    println(result2)
}