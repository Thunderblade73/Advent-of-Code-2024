import java.io.File

fun day14() {
    val file = File("src/main/resources/Day14Input.txt")

    val xSize = 101
    val ySize = 103

    val size = VecI(xSize, ySize)

    val regex = "p=(?<px>[-+\\d]+),(?<py>[-+\\d]+)\\s*v=(?<vx>[-+\\d]+),(?<vy>[-+\\d]+)".toRegex()

    val input = file.useLines { line ->
        line.map {
            regex.find(it)!!.let {
                VecI(it.getGroupInt("px"), it.getGroupInt("py")) to VecI(it.getGroupInt("vx"), it.getGroupInt("vy"))
            }
        }.toList()
    }


    val after100Sec = input.map {
        robotPos(it.first, it.second, size, 100)
    }

    val quads = after100Sec.groupBy {
        if (it.first == size.first / 2) {
            return@groupBy 4
        }
        if (it.second == size.second / 2) {
            return@groupBy 4
        }
        if (it.first < size.first / 2) {
            if (it.second < size.second / 2) {
                0
            } else {
                1
            }
        } else {
            if (it.second < size.second / 2) {
                2
            } else {
                3
            }
        }
    }

    val result = mul(quads[0],quads[1],quads[2],quads[3])

    println(result)

    for(i in 23..10000000 step 101){
        println()
        input.map {
            robotPos(it.first, it.second, size, i)
        }.printBoard(size)
        println()
        println(i)
        readLine()
    }

}

private fun List<VecI>.printBoard(size: VecI){
    val set = groupBy { it }.mapValues { it.value.size }
    repeat(size.second){ y ->
        repeat(size.first){ x->
            val v = set[VecI(x,y)] ?: 0
            if(v == 0){
                print(".")
            }else{
                print(v)
            }
        }
        print("\n")
    }
}

private fun <T> mul(a: List<T>?, b: List<T>?, c: List<T>?, d: List<T>?): Int =
    (a?.size ?: 0) * (b?.size ?: 0) * (c?.size ?: 0) * (d?.size ?: 0)

private fun robotPos(p: VecI, v: VecI, size: VecI, s: Int): VecI = ((p + (v * s)) % size).makePositive(size)

private fun VecI.makePositive(size : VecI) : VecI{
    val nFirst = if(first < 0){
        size.first + first
    }else first
    val nSecond = if(second < 0){
        size.second + second
    }else second
    return VecI(nFirst,nSecond)
}
operator fun VecI.times(t: Int): VecI = VecI(first * t, second * t)
private operator fun VecI.rem(m: VecI): VecI = VecI(first % m.first, second % m.second)

typealias VecI = Pair<Int, Int>

fun MatchResult.getGroupInt(name: String) = groups[name]!!.value.toInt()