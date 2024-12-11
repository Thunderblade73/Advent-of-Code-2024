import java.io.File

private fun problem1(inState: List<Long>){
    var firstRun = inState
    for (x in 0..24) {
        firstRun = firstRun.permuteList()
    }

    println(firstRun.size)
    println()
}


suspend fun day11() {
    val file = File("src/main/resources/Day11Input.txt")

    val inState = file.readText().split(" ").map { it.toLong() }

//    val workStack = ConcurrentLinkedDeque<Triple<Int,Int,List<Long>>>()
//    val resultStack = mutableListOf<Triple<Int,Int,List<Long>>>()
//
//    workStack.addAll(firstRun.mapIndexed{i,it -> Triple(i,24,listOf(it))})
//
//    fun doWork(it : Triple<Int,Int,List<Long>>){
//        val a = Triple(it.first,it.second+1,it.third.permute())
//        println("${it.second} : ${it.first}")
//        if(it.second == 73){
//            resultStack.add(a)
//        }else{
//            workStack.add(a)
//        }
//    }
//
//    runBlocking {
//        for (i in 0..63){
//            launch {
//                while (workStack.isNotEmpty()){
//                    val a = workStack.poll() ?: break
//                    doWork(a)
//                }
//            }
//        }
//    }
//
//    while (workStack.isNotEmpty()){
//        delay(10.seconds)
//    }
//    println(resultStack.sumOf { it.third.size })

    //println(inState.map { it.permute(0,75) }.flatten().size)

    //val end = 1L.permute(0, 5)

    println(inState.sumOf { it.permute(75) })
}

private val memo = mutableMapOf<Long, MutableMap<Int, List<Long>>>()

fun Long.calculatePermute(): List<Long> {
    if (this == 0L) return listOf(1L)
    if (this.toString().length % 2 == 0) return this.split()

    return listOf(this * 2024L)
}

private val cache = (1..75).associateWith { mutableMapOf<Long,Long>() }

fun Long.permute(iteration : Int) : Long = if(iteration==0) 1 else{
    val itCache = cache[iteration]!!
    itCache.getOrPut(this){
        when{
            this == 0L -> 1L.permute(iteration-1)
            this.toString().length % 2 == 0 -> this.split().sumOf { it.permute(iteration-1) }
            else -> (this * 2024L).permute(iteration-1)
        }
    }
}


fun Long.permuteOld(iteration: Int, target: Int): List<Long> {
    if (iteration == target) return listOf(this) // TODO prove that is correct
    val memoVal = memo[this] ?: emptyMap()
    val skip = memoVal[target - iteration - 1]
    if (skip != null) return skip
    val comp = memoVal[0] ?: this.calculatePermute().also {
        memo.merge(this, mapOf(0 to it).toMutableMap()) { a, b ->
            a.putAll(b)
            a
        }
        println(this)
    }
    if (comp.size == 1) {
        return comp[0].permuteOld(iteration, target-1)
    }
    memo.merge(this, mapOf(0 to comp).toMutableMap()) { a, b ->
        a.putAll(b)
        a
    }
    for (x in (iteration + 1)..<target) {
        val end = comp.map { it.permuteOld(0, x) }.flatten()
        memo.merge(this, mapOf(x to end).toMutableMap()) { a, b ->
            a.putAll(b)
            a
        }
    }
    return memo[this]!![target - iteration - 1]!!

    /*
    1

    0 2024
    1 20 24
    2 2 0 2 4
    3 4048 1 4048 8096
    4 40 48 2024 40 48 80 96


     */

}

fun List<Long>.permuteList() = buildList<Long> {
    val (zero, zeroRemain) = this@permuteList.partition { it == 0L }

    // Zero Rule
    addAll(zero.map { 1L })

    val (even, odd) = zeroRemain.partition { it.toString().length % 2 == 0 }

    // Even Split
    addAll(even.map { it.split() }.flatten())

    // Multiply 2024
    addAll(odd.map { it * 2024L })
}

fun Long.split(): List<Long> {
    val asString = toString()

    val one = asString.substring(0, asString.length / 2).toLong()

    val two = asString.substring(asString.length / 2).toLong()

    return listOf(one, two)
}
