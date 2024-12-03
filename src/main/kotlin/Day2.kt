import java.io.File
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sign

private val regex = "(\\d+)".toRegex()

fun day2() {
    val file = File("src/main/resources/Day2Input.txt")

    var safeData = 0

    val rawData = file.readText().split("\r\n")

    rawData.forEach {
        if(safeCheck(it)){
            safeData++
        }
    }

    println(safeData)

    safeData = 0

    val badList = mutableListOf<String>()
    val veryBadList = mutableListOf<String>()

    rawData.forEach {
        if(safeCheckWithRemove(it)){
            safeData++
        }else{
            val converted = regex.findAll(it).map { it.groupValues[1].toInt() }.groupBy { it }
            if(converted.all { it.value.size < 3 }) {
                badList.add(it)
            }else{
                veryBadList.add(it)
            }
        }
    }

    println(safeData)
}

private fun safeCheck(it: String) : Boolean {
    val lineData = regex.findAll(it).map { it.groupValues[1].toInt() }
    val diffs = lineData.zipWithNext { a, b -> b - a }
    if(diffs.any{it == 0}) return false
    if(diffs.any { it > 3 || it < -3 }) return false
    val sign = diffs.first()
    if(diffs.any { it * sign < 0 }) return false
    return true
}

private val convData = mutableListOf<String>()
private val failedWithAbs = mutableListOf<String>()

enum class Fail{
    Max,
    Zero,
    Flow,
}

fun check(list : List<Int>) : Fail?{
    val diffs = list.zipWithNext{a,b -> b - a}
    val signGroups = diffs.groupBy { it.sign }
    if(signGroups[0] != null) return Fail.Zero
    if(signGroups.size > 1) return Fail.Flow
    val abs = diffs.map { abs(it) }
    if(abs.any { it > 3 }) return Fail.Max
    return null
}


private fun safeCheckWithRemove(it: String) : Boolean {
    val input = regex.findAll(it).map { it.groupValues[1].toInt() }.let{
        val min = it.min()
        it.map { it - min }
    }.toList().let {
        if(it[0] != 0 && it[1] != 0){
            it.reversed()
        }else{
            it
        }
    }

    convData.add(input.joinToString(" "))

    val c1 = check(input)
    if(c1 != null){
        val c2 = input.withIndex().any { (i, it) ->
            check(input.subList(0,i) + input.subList(i+1,input.lastIndex+1)) == null
        }
        return c2
    }
    return true


    /*val lineData = input.toMutableList()
    fun diffsGenerate() : List<Int> = lineData.zipWithNext { a, b -> b - a }
    var diffs : List<Int> = diffsGenerate()
    var joker = false


    if(diffs.any{it == 0}){
        val remove = diffs.indexOfFirst{it == 0}
        lineData.removeAt(remove)
        joker = true
        diffs = diffsGenerate()
        if(diffs.any{it == 0}) return false
    }

    val abs = diffs.map { abs(it) }

    val remove = abs.indexOfFirst { it > 3 }
    if(remove != -1){
        if(joker) return false
        lineData.removeAt(if(remove==0) 0 else remove+1)
        joker = true
        diffs = diffsGenerate()
        val sign2 = diffs.first().sign

        val abs2 = diffs.map { it * sign2 }

        val remove2 = abs2.indexOfFirst { it > 3 }
        if(remove2 != -1){
            failedWithAbs.add(input.joinToString(" "))
            return false
        }
    }

    val signGroups = diffs.groupBy { it.sign }

    if(signGroups.size > 1){
        if(joker) return false
        val pos = signGroups[1]!!
        val neg = signGroups[-1]!!
        if(pos.size >= 2 && neg.size >=2) return false
        val remove : Int
        if(pos.size > neg.size){
            remove = neg[0]
        }else{
            remove = pos[0]
        }
        val removeIndex = diffs.indexOf(remove)
        lineData.removeAt(removeIndex+1)
        joker = true
        diffs = diffsGenerate()
        val signGroups2 = diffs.groupBy { it.sign }
        if(signGroups2.size > 1 ) return false
    }*/



    return true
}