import java.awt.Event.DOWN
import java.awt.Event.UP
import java.io.File

fun day6(){
    val file = File("src/main/resources/Day6Input.txt")

    val rawDataInput = file.readLines()

    val outputMap = mutableMapOf<Pair<Int,Int>,Boolean>()

    val guardStart = rawDataInput.withIndex().firstNotNullOf{ (i,it) ->
        val j =it.indexOf("^")
        if(j == -1) null else i to j
    }

    var guardPosition = guardStart
    var direction = Direction.UP

    while (rawDataInput.getField(guardPosition) != null){
        val look = when(direction){
            Direction.UP -> guardPosition addX -1
            Direction.DOWN -> guardPosition addX 1
            Direction.RIGHT -> guardPosition addY 1
            Direction.LEFT -> guardPosition addY -1
        }
        if(rawDataInput.getField(look) == '#'){
            direction = direction.rightTurn
            continue
        }
        guardPosition = look
        outputMap[look] = true
    }

    println(outputMap.size)

    outputMap.remove(guardStart)
    val loopObstacles = outputMap.count { (obstacle,_) ->
        val runMap = mutableMapOf<Pair<Int,Int>,List<Direction>>()
        guardPosition = guardStart
        direction = Direction.UP
        while (rawDataInput.getField(guardPosition) != null && runMap[guardPosition]?.contains(direction) != true){
            runMap.merge(guardPosition,listOf(direction)){a,b -> a+b}
            val look = when(direction){
                Direction.UP -> guardPosition addX -1
                Direction.DOWN -> guardPosition addX 1
                Direction.RIGHT -> guardPosition addY 1
                Direction.LEFT -> guardPosition addY -1
            }
            if(look == obstacle ||rawDataInput.getField(look) == '#'){
                direction = direction.rightTurn
                continue
            }
            guardPosition = look
        }
        runMap[guardPosition]?.contains(direction) ?: false
    }

    println(loopObstacles)
}

private fun List<String>.getField(pos : Pair<Int,Int>) = getOrNull(pos.first)?.getOrNull(pos.second)

infix fun Pair<Int,Int>.addX(x : Int) = (first + x) to second
infix fun Pair<Int,Int>.addY(y : Int) = first to (second + y)

private enum class Direction {
    UP,
    DOWN,
    RIGHT,
    LEFT;

    val rightTurn by lazy {
        when (this) {
            UP -> RIGHT
            RIGHT -> DOWN
            DOWN -> LEFT
            LEFT -> UP
        }
    }

}
