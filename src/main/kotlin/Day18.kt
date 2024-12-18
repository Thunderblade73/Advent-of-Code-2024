import java.io.File

fun day18(){
    val file = File("src/main/resources/Day18Input.txt")

    val size = (70 to 70) as VecI

    val start = (0 to 0 ) as VecI
    val end = size

    val steps = 1024

    val instructions = file.readLines().map { it.split(",").map { it.toInt() }.let { VecI(it[0],it[1]) } }

    val pathSteps = simulate(instructions, steps, size, start, end)

    println(pathSteps)

    for(i in steps..instructions.size) {
        val path = simulate(instructions,i,size,start,end)
        if(path == null){
            println(instructions[i-1])
            break
        }
    }

}

private fun simulate(
    instructions: List<VecI>,
    steps: Int,
    size: VecI,
    start: VecI,
    end: VecI,
): Int? {
    val validInstructions = instructions.take(steps).toSet()

    val board = (0..size.first).map { (0..size.first).map { '.' }.toMutableList() }

    val nodeMap = mutableMapOf<VecI, Node>()

    board.forEachIndexed { y, row ->
        row.forEachIndexed { x, c ->
            val pos = VecI(x, y)
            if (validInstructions.contains(pos)) {
                board[y][x] = '#'
            } else {
                nodeMap[pos] = Node("${pos.first} , ${pos.second}")
            }
        }
    }

    nodeMap.forEach { (pos, node) ->
        val n = nodeMap[pos.addY(-1)]?.addTo(node, 1)
        val s = nodeMap[pos.addY(1)]?.addTo(node, 1)
        val w = nodeMap[pos.addX(-1)]?.addTo(node, 1)
        val e = nodeMap[pos.addX(1)]?.addTo(node, 1)
    }

    return nodeMap[start]!!.allDistances()[nodeMap[end]!!]
}

private fun Node.addTo(origin: Node,distance : Int): Node {
    origin.neighbours[this] = distance
    return this
}
