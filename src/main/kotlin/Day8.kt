import java.io.File

fun day8() {
    val file = File("src/main/resources/Day8Input.txt")

    val rawData = file.readLines()

    val groups = rawData.mapIndexed { x, s -> s.mapIndexed { y, c -> c to (x to y) } }.flatten()
        .groupBy({ it.first }, { it.second }).toMutableMap().apply{remove('.')}

    val antiNodes = groups.mapValues { (_, positons) ->
        val remainingPositions = positons.toMutableList()
        val anti = mutableListOf<Pair<Int,Int>>()
        var iterator: MutableIterator<Pair<Int, Int>>
        do {
            iterator = remainingPositions.iterator()
            val start = iterator.next()
            iterator.remove()
            while (iterator.hasNext()){
                val comp = iterator.next()
                val (n1,n2) = antenna(start,comp)
                anti.add(n1)
                anti.add(n2)
            }
        }while (remainingPositions.size>=2)
        anti.clipNodes(rawData)
        anti
    }

    val uniquePositions = antiNodes.values.flatten().toSet()

    println(uniquePositions.size)

    val harmonicAntiNodes = groups.mapValues { (_, positons) ->
        val remainingPositions = positons.toMutableList()
        val anti = mutableListOf<Pair<Int,Int>>()
        var iterator: MutableIterator<Pair<Int, Int>>
        do {
            iterator = remainingPositions.iterator()
            val start = iterator.next()
            iterator.remove()
            while (iterator.hasNext()){
                val comp = iterator.next()
                anti.addAll(harmonicAntenna(start,comp, Pair(49,49)))
            }
        }while (remainingPositions.size>=2)
        anti.clipNodes(rawData)
        anti
    }

    val harmonicPositions = harmonicAntiNodes.values.flatten().toSet()

    val total = uniquePositions + harmonicPositions

    println(total.size)

}

private fun MutableList<Pair<Int,Int>>.clipNodes(origin : List<String>){
    val iterator = iterator()
    while (iterator.hasNext()){
        val v = iterator.next()
        if(origin.getOrNull(v.first)?.getOrNull(v.second) == null){
            iterator.remove()
        }
    }
}

private operator fun Pair<Int,Int>.minus(other : Pair<Int,Int>) = (first - other.first) to (second - other.second)
operator fun Pair<Int,Int>.plus(other : Pair<Int,Int>) = (first + other.first) to (second + other.second)



private fun antenna(first: Pair<Int,Int>,second : Pair<Int,Int>): Pair<Pair<Int, Int>, Pair<Int, Int>> {
    val vector = first - second
    return (first+vector) to (second-vector)
}

private fun harmonicAntenna(first: Pair<Int,Int>,second : Pair<Int,Int>,space : Pair<Int,Int>) : List<Pair<Int,Int>>{
    val vector = first - second
    val outList = mutableListOf<Pair<Int,Int>>(first,second)
    var running = first + vector
    while (running bigger Pair(0,0) && space bigger running){
        outList.add(running)
        running += vector
    }
    running = second - vector
    while (running bigger Pair(0,0) && space bigger running){
        outList.add(running)
        running -= vector
    }
    return outList
}

private infix fun Pair<Int, Int>.bigger(other: Pair<Int, Int>): Boolean {
    if(first < other.first) return false
    if(second < other.second)return false
    return true
}
