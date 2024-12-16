import java.io.File
import kotlin.math.sqrt
import java.util.PriorityQueue
import kotlin.math.absoluteValue

fun day16() {
    val file = File("src/main/resources/Day16Input.txt")

    val rawInput = file.readLines()

    val input = rawInput.map {
        it.map {
            when (it) {
                'S' -> '.'
                'E' -> '.'
                else -> it
            }
        }
    }

    val xMax = input.first().size - 1
    val yMax = input.size - 1

    val crossingMap = mutableMapOf<VecI, Crossing>()

    input.drop(1).dropLast(1).forEachIndexed { y, row ->
        val ry = y + 1
        row.drop(1).dropLast(1).forEachIndexed { x, c ->
            val rx = x + 1
            val pos = VecI(rx, ry)
            val n = input[pos.second - 1][pos.first]
            val s = input[pos.second + 1][pos.first]
            val w = input[pos.second][pos.first - 1]
            val e = input[pos.second][pos.first + 1]
            val cross = crossing(n, s, w, e, c, "$rx , $ry ")
            if (cross != null) {
                crossingMap[pos] = cross
            }
        }
    }

    val lineNodes = mutableMapOf<VecI, Node>()

    crossingMap.forEach { (pos, cross) ->
        cross.second.forEach side@{ side ->
            var cPos = pos + side.vec
            while (true) {
                val v = crossingMap[cPos]
                if (v != null) break
                cPos += side.vec
                if (!cPos.inBounds(xMax, yMax)) return@side
            }
            val neighbour = crossingMap[cPos]!!
            if (!neighbour.second.contains(side.anti)) return@side
            neighbour.second.remove(side.anti)
            val distance = (pos - cPos).taxLength()
            val c: Node
            val n: Node
            when (side) {
                Side.NORTH -> {
                    c = cross.first[0]
                    n = neighbour.first[2]
                }

                Side.EAST -> {
                    c = cross.first[3]
                    n = neighbour.first[1]
                }

                Side.SOUTH -> {
                    c = cross.first[2]
                    n = neighbour.first[0]
                }

                Side.WEST -> {
                    c = cross.first[1]
                    n = neighbour.first[3]
                }
            }
            var lastNode = c
            for (i in 1..<distance) {
                val vPos = pos + side.vec * i
                val node = Node("${vPos.first} , ${vPos.second}")
                lineNodes[vPos] = node
                node.neighbours[lastNode] = 1
                lastNode.neighbours[node] = 1
                lastNode = node
            }
            lastNode.neighbours[n] = 1
            n.neighbours[lastNode] = 1

            //c.neighbours[n] = distance
            //n.neighbours[c] = distance
        }
        cross.second.clear()
    }

    val startCord = rawInput.indexOfFirst { it.contains('S') }.let { rawInput[it].indexOf('S') to it }
    val endCord = rawInput.indexOfFirst { it.contains('E') }.let { rawInput[it].indexOf('E') to it }
    val startCrossing = crossingMap[startCord]!!
    val endCrossing = crossingMap[endCord]!!

    val startNode = startCrossing.first[3]

    val optimalDistance = startNode.distanceTo(endCrossing.first)

    println(optimalDistance)

    val startDistances = startNode.allDistances()
    val endDistances = endCrossing.first.map { it.allDistances() }

    var positionCounter = 0

    fun Node.doesCount() : Boolean{
        val startDistance = startDistances[this]!!
        val endDistance = endDistances.map { it[this]!! }
        return endDistance.any { it + startDistance == optimalDistance }
    }

    input.drop(1).dropLast(1).forEachIndexed { y, row ->
        val ry = y + 1
        row.drop(1).dropLast(1).forEachIndexed xLoop@{ x, c ->
            if (c != '.') return@xLoop
            val rx = x + 1
            val pos = VecI(rx, ry)

            val lineNode = lineNodes[pos]
            if (lineNode != null) {
                if (lineNode.doesCount()) {
                    positionCounter++
                }
                return@xLoop
            }
            val cross = crossingMap[pos] ?: return@xLoop
            if (cross.first.any {it.doesCount() }){
                positionCounter++
            }
        }
    }

    println(positionCounter)
}

private fun Node.distanceTo(goals: List<Node>): Int {
    val distances = mutableMapOf<Node, Int>()
    val previous = mutableMapOf<Node, Node>()
    val visited = mutableSetOf<Node>()
    val queue = PriorityQueue<Node>(compareBy { distances.getOrDefault(it, Int.MAX_VALUE) })
    var lastVisitedNode: Node = this

    distances[this] = 0
    queue.add(this)

    while (queue.isNotEmpty()) {
        val current = queue.poll()
        lastVisitedNode = current
        if (goals.contains(current)) break

        visited.add(current)

        current.neighbours.forEach { (neighbour, weight) ->
            if (neighbour !in visited) {
                val newDistance = distances.getValue(current) + weight
                if (newDistance < distances.getOrDefault(neighbour, Int.MAX_VALUE)) {
                    distances[neighbour] = newDistance
                    previous[neighbour] = current
                    queue.add(neighbour)
                }
            }
        }
    }

    return distances[lastVisitedNode]!!
}

private fun Node.allDistances(): Map<Node, Int> {
    val distances = mutableMapOf<Node, Int>()
    val previous = mutableMapOf<Node, Node>()
    val visited = mutableSetOf<Node>()
    val queue = PriorityQueue<Node>(compareBy { distances.getOrDefault(it, Int.MAX_VALUE) })

    distances[this] = 0
    queue.add(this)

    while (queue.isNotEmpty()) {
        val current = queue.poll()

        visited.add(current)

        current.neighbours.forEach { (neighbour, weight) ->
            if (neighbour !in visited) {
                val newDistance = distances.getValue(current) + weight
                if (newDistance < distances.getOrDefault(neighbour, Int.MAX_VALUE)) {
                    distances[neighbour] = newDistance
                    previous[neighbour] = current
                    queue.add(neighbour)
                }
            }
        }
    }

    return distances
}

private fun VecI.length(): Double = sqrt(first.toDouble() * first + second.toDouble() * second)

private fun VecI.taxLength(): Int = if (first == 0) second.absoluteValue else first.absoluteValue

val Side.anti
    get() = when (this) {
        Side.NORTH -> Side.SOUTH
        Side.EAST -> Side.WEST
        Side.SOUTH -> Side.NORTH
        Side.WEST -> Side.EAST
    }

private fun VecI.inBounds(xMax: Int, yMax: Int): Boolean = first >= 0 && second >= 0 && first <= xMax && second <= yMax

private class Node(val name: String) {
    val neighbours = mutableMapOf<Node, Int>()

    override fun toString() = name
}

private typealias Crossing = Pair<List<Node>, MutableList<Side>>

private fun crossing(n: Char, s: Char, w: Char, e: Char, c: Char, name: String): Crossing? {
    if (c != '.') return null
    val map = listOf(n, s, w, e).map { it == '.' }
    if (!(map.count { it } == 1 || ((map[0] || map[1]) && (map[2] || map[3])))) return null

    // North, West, South, East
    val nodes = listOf(Node(name + "N"), Node(name + "W"), Node(name + "S"), Node(name + "E"))
    nodes.forEachIndexed { i, it ->
        it.neighbours[nodes[(i + 1) % 4]] = 1000
        it.neighbours[nodes[(i + 3) % 4]] = 1000
        it.neighbours[nodes[(i + 2) % 4]] = 0
    }
    val sides = buildList<Side> {
        if (map[0]) add(Side.NORTH)
        if (map[1]) add(Side.SOUTH)
        if (map[2]) add(Side.WEST)
        if (map[3]) add(Side.EAST)
    }.toMutableList()
    return nodes to sides

}