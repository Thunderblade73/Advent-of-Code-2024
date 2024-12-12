import java.io.File

fun day12(){
    val file = File("src/main/resources/Day12TestInput.txt")

    val input = file.readLines()

    val fields = mutableListOf<MutableList<Field>>()
    
    val clusters = mutableListOf<MutableList<Field>>()

    fun Field.addNewField() {
        val cluster = clusters[clusterIndex]
        cluster.add(this)
        val fieldRow = fields.getOrNull(pos.second) ?: mutableListOf<Field>().also { fields.add(it) }
        fieldRow.add(this)
    }

    fun Field.addToCluster(
        pos: Pair<Int, Int>,
        c : Char
    ) {
        Field(pos, clusterIndex,c).addNewField()
    }

    fun Pair<Int, Int>.checkField(c : Char) {
        fields.getOrNull(second - 1)?.getOrNull(first)?.let { if(it.value == c) it else null }?.addToCluster(this,c)?.also { return }
        input.getOrNull(second - 1)?.getOrNull(first)?.let { if(it == c) it else null }?.addToCluster(this,c)?.also { return }
        fields.getOrNull(second)?.getOrNull(first - 1)?.let { if(it.value == c) it else null }?.addToCluster(this,c)?.also { return }
        input.getOrNull(second)?.getOrNull(first - 1)?.let { if(it == c) it else null }?.addToCluster(this,c)?.also { return }
        val field = Field(this,clusters.size,c)
        clusters.add(mutableListOf())
        field.addNewField()
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


private class Field(val pos : Pair<Int,Int>,val clusterIndex : Int,val value : Char)

private infix fun Pair<Int,Int>.addX(x : Int) = (first + x) to second
private infix fun Pair<Int,Int>.addY(y : Int) = first to (second + y)