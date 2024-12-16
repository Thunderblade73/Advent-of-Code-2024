import java.io.File

private var input : List<String> = emptyList()

fun Pair<Int,Int>.getValue() : Char? = input.getOrNull(second)?.getOrNull(first)

fun day12(){
    val file = File("src/main/resources/Day12Input.txt")

    input = file.readLines()

    val fields = mutableMapOf<Pair<Int,Int>,Field>()
    
    val clusters = mutableListOf<MutableList<Field>>()

    fun Field.addNewField() {
        val cluster = clusters[clusterIndex]
        cluster.add(this)
        fields[this.pos] = this
    }

    fun addToCluster(
        pos: Pair<Int, Int>,
        c : Char,
        clusterIndex: Int
    ) : Field {
        val field = Field(pos, clusterIndex,c)
        field.addNewField()
        return field
    }

    fun Pair<Int,Int>.groupFields(c : Char,collect : MutableSet<Pair<Int,Int>>){
        collect.add(this@groupFields)
        this@groupFields.addX(-1).takeIf { !collect.contains(it) }?.getValue()?.takeIf { it == c }?.let { this@groupFields.addX(-1).groupFields(c, collect) }
        this@groupFields.addY(-1).takeIf { !collect.contains(it) }?.getValue()?.takeIf { it == c }?.let { this@groupFields.addY(-1).groupFields(c,collect) }
        this@groupFields.addX(1).takeIf { !collect.contains(it) }?.getValue()?.takeIf { it == c }?.let { this@groupFields.addX(1).groupFields(c,collect) }
        this@groupFields.addY(1).takeIf { !collect.contains(it) }?.getValue()?.takeIf { it == c }?.let { this@groupFields.addY(1).groupFields(c,collect) }
    }

    fun Pair<Int, Int>.checkField(c : Char) {
        fields[this]?.let { return }
        val collect = mutableSetOf<Pair<Int,Int>>()
        this.groupFields(c,collect)

        val clusterIndex = clusters.size

        clusters.add(mutableListOf())
        collect.forEach{
            addToCluster(it,c,clusterIndex)
        }
    }
    
    input.forEachIndexed { y, line -> 
        line.forEachIndexed { x, c ->
            (x to y).checkField(c)
        }
    }

    val sum1 = clusters.sumOf {
        it.getPerimeter() * it.getArea()
    }
    println(sum1)

    val sum2 = clusters.sumOf {
        it.getCheapPerimeter() * it.getArea()
    }
    println(sum2)

    // Sanity Checks
    val fieldsInClusters = clusters.sumOf { it.size }
    val noMixedClusters = clusters.all { a -> a.all { it.value == a.first().value } }
    val clusterToValue = clusters.associateWith { it.first().value }
    val valueGroups = clusters.groupBy { it.first().value }
}

private fun List<Field>.getArea(): Long = size.toLong()

private fun List<Field>.getPerimeter(): Long {
    var base = size.toLong()*4
    forEach { field ->
        val top = firstOrNull{it.pos == field.pos.addY(-1)}
        val bot = firstOrNull{it.pos == field.pos.addY(1)}
        val left = firstOrNull{it.pos == field.pos.addX(-1)}
        val right = firstOrNull{it.pos == field.pos.addX(1)}
        base -= listOfNotNull(top, bot, left, right).size
    }
    return base
}

private fun List<Field>.getCheapPerimeter(): Long {
    val infos = map { it.getSides() }.flatten()
    val groups = infos.groupBy { it.second }.mapValues { it.value.map { it.first }.toSet() }
    val north = groups[Side.NORTH]?.findNS() ?: 0L
    val south = groups[Side.SOUTH]?.findNS() ?: 0L
    val east = groups[Side.EAST]?.findEW() ?: 0L
    val west = groups[Side.WEST]?.findEW() ?: 0L
    return north + south + east + west
}

private fun Field.getSides() = listOfNotNull<SideInfo>(
    pos.addX(1).getValue().let { if(value != it) pos to Side.EAST else null },
    pos.addX(-1).getValue().let { if(value != it) pos to Side.WEST else null },
    pos.addY(1).getValue().let { if(value != it) pos to Side.SOUTH else null },
    pos.addY(-1).getValue().let { if(value != it) pos to Side.NORTH else null },
)


private typealias SideInfo = Pair<Pair<Int,Int>,Side>

enum class Side(val vec : VecI){
    NORTH(0 to -1),
    EAST(1 to 0),
    SOUTH(0 to 1),
    WEST(-1 to 0)
}

private fun Set<Pair<Int,Int>>.findEW() : Long{
    val reduceSet = this.toMutableSet()
    var sum = 0L
    while(reduceSet.isNotEmpty()){
        val inital = reduceSet.first()
        reduceSet.remove(inital)

        var plus = inital.addY(1)
        while (reduceSet.contains(plus)){
            reduceSet.remove(plus)
            plus = plus.addY(1)
        }

        var minus = inital.addY(-1)
        while (reduceSet.contains(minus)){
            reduceSet.remove(minus)
            minus = minus.addY(-1)
        }
        sum++
    }
    return sum
}

private fun Set<Pair<Int,Int>>.findNS(): Long{
    val reduceSet = this.toMutableSet()
    var sum = 0L
    while(reduceSet.isNotEmpty()){
        val inital = reduceSet.first()
        reduceSet.remove(inital)

        var plus = inital.addX(1)
        while (reduceSet.contains(plus)){
            reduceSet.remove(plus)
            plus = plus.addX(1)
        }

        var minus = inital.addX(-1)
        while (reduceSet.contains(minus)){
            reduceSet.remove(minus)
            minus = minus.addX(-1)
        }
        sum++
    }
    return sum
}

private class Field(val pos : Pair<Int,Int>,val clusterIndex : Int,val value : Char)

private infix fun Pair<Int,Int>.addX(x : Int) = (first + x) to second
private infix fun Pair<Int,Int>.addY(y : Int) = first to (second + y)