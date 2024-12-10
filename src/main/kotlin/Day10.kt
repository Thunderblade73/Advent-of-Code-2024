import java.io.File

fun day10() {
    val file = File("src/main/resources/Day10Input.txt")

    val input = file.useLines { it.map { it.map { it.digitToInt() } }.toList() }

    var sumScore = 0
    var sumRating = 0

    for ((y, lines) in input.withIndex()) {
        for ((x, cell) in lines.withIndex()) {
            if (cell == 0) {
                sumScore += input.findHeads(0,x,y).size
                sumRating += input.findHeadsRating(0,x,y)
            }
        }
    }

    println(sumScore)
    println(sumRating)
}

private fun List<List<Int>>.get(x: Int, y: Int) = getOrNull(y)?.getOrNull(x)
private fun List<List<Int>>.get(pos: Pair<Int, Int>) = get(pos.first, pos.second)

private fun List<List<Int>>.findHeads(value: Int, x: Int, y: Int): Set<Pair<Int,Int>> {
    val target = value+1
    val neighbours = sequenceOf((x + 1 to y), (x - 1 to y), (x to y + 1), (x to y - 1)).mapNotNull { get(it)?.to(it) }
        .filter { it.first == target }.toList()
    if(target == 9) return neighbours.map { it.second }.toSet()
    return neighbours.map { findHeads(target,it.second.first,it.second.second) }.flatten().toSet()
}

private fun List<List<Int>>.findHeadsRating(value: Int, x: Int, y: Int): Int {
    val target = value+1
    val neighbours = sequenceOf((x + 1 to y), (x - 1 to y), (x to y + 1), (x to y - 1)).mapNotNull { get(it)?.to(it) }
        .filter { it.first == target }.toList()
    if(target == 9) return neighbours.size
    return neighbours.sumOf { findHeadsRating(target,it.second.first,it.second.second) }
}