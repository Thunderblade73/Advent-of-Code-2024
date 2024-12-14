import java.io.File
import java.lang.Exception

fun day13() {
    val file = File("src/main/resources/Day13Input.txt")

    val regex =
        "Button A: X(?<ax>[-+\\d]+), Y(?<ay>[-+\\d]+)\\s+Button B: X(?<bx>[-+\\d]+), Y(?<by>[-+\\d]+)\\s+Prize: X=(?<px>[-+\\d]+), Y=(?<py>[-+\\d]+)".toRegex()

    val input = regex.findAll(file.readText()).map {
        with(it) {
            Triple(
                (getGroupLong("ax") to getGroupLong("ay")) as VecL,
                (getGroupLong("bx") to getGroupLong("by")) as VecL,
                (getGroupLong("px") to getGroupLong("py")) as VecL
            )
        }
    }.toList()

    val costs = input.map { getPoint(it.first,it.second,it.third,true) }

    println(costs.filterNotNull().sum())

    val costs2 = input.map { getPoint(it.first,it.second,it.third + 10000000000000L,false) }

    println(costs2.filterNotNull().sum())
}

fun MatchResult.getGroupLong(name: String) = groups[name]!!.value.toLong()

typealias VecL = Pair<Long, Long>

operator fun VecL.plus(other: Long) = first + other to second + other

private fun getPoint(a: VecL, b: VecL, p: VecL, cap : Boolean): Long? {
    try {
        val (q, r1) = (p.second * a.first - a.second * p.first).divWithR(b.second * a.first - a.second * b.first)
        if (r1 != 0L) return null
        if(cap && q > 100L) return null
        val (l, r2) = (p.first - b.first * q).divWithR(a.first)
        if (r2 != 0L) return null
        if(cap && l > 100L) return null
        return q + l*3L
    } catch (e: Exception) {
        return null
    }
}

private fun Long.divWithR(other: Long): Pair<Long, Long> =
    this / other to this % other

