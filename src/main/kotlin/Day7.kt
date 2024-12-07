import java.io.File
import kotlin.math.pow

fun day7() {
    val file = File("src/main/resources/Day7Input.txt")

    val regex = "(\\d+)".toRegex()

    val raw = file.readLines().map {
        regex.findAll(it).map { it.groupValues.drop(1).map { it.toLong() } }.flatten().toList()
    }

    val calculations = raw.map { it.first() to findOperator(it.first(), it.drop(1)) }
    val possible = calculations.filterNot { it.second.contains(Operators.INVALID) }

    println(possible.sumOf { it.first })

    val calculations2 = raw.map { it.first() to findOperator2(it.first(), it.drop(1)) }
    val possible2 = calculations2.filterNot { it.second.contains(Operators.INVALID) }

    println(possible2.sumOf { it.first })
}

enum class Operators {
    ADD,
    MUL,
    CONCAT,
    INVALID
}

fun findOperator(result: Long, remainingValues: List<Long>): List<Operators> {
    if (remainingValues.size == 1) {
        if (result == remainingValues[0]) {
            return emptyList()
        } else {
            return listOf(Operators.INVALID)
        }
    }
    val addList = findOperator(result, listOf(remainingValues[0] + remainingValues[1]) + remainingValues.drop(2))
    if (addList.lastOrNull() != Operators.INVALID) {
        return listOf(Operators.ADD) + addList
    }
    val mulList = findOperator(result, listOf(remainingValues[0] * remainingValues[1]) + remainingValues.drop(2))
    if (mulList.lastOrNull() != Operators.INVALID) {
        return listOf(Operators.MUL) + mulList
    }
    return listOf(Operators.INVALID)
}

fun findOperator2(result: Long, remainingValues: List<Long>): List<Operators> {
    if (remainingValues.size == 1) {
        if (result == remainingValues[0]) {
            return emptyList()
        } else {
            return listOf(Operators.INVALID)
        }
    }
    val addList = findOperator2(result, listOf(remainingValues[0] + remainingValues[1]) + remainingValues.drop(2))
    if (addList.lastOrNull() != Operators.INVALID) {
        return listOf(Operators.ADD) + addList
    }
    val mulList = findOperator2(result, listOf(remainingValues[0] * remainingValues[1]) + remainingValues.drop(2))
    if (mulList.lastOrNull() != Operators.INVALID) {
        return listOf(Operators.MUL) + mulList
    }
    val concatList =
        findOperator2(result, listOf(remainingValues[0] concatNumber remainingValues[1]) + remainingValues.drop(2))
    if (concatList.lastOrNull() != Operators.INVALID) {
        return listOf(Operators.CONCAT) + concatList
    }
    return listOf(Operators.INVALID)
}

infix fun Long.concatNumber(other: Long): Long {
    val digits = other.toString().length
    val result = 10.0.pow(digits).toLong() * this + other
    return result
}