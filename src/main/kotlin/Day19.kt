import java.io.File

fun day19(){
    val file = File("src/main/resources/Day19Input.txt")

    val input = file.readLines()

    val available = input[0].split(",").map { it.trim() }

    val request = input.drop(2)

    towels.addAll(available)
    available.sortedBy { it.length }.forEach {
        patternCache[it] = findPattern(it) + 1
    }
    //patternCache.putAll(available.associateWith { 1L })

    println(request.count {
        println(it)
        findPattern(it) > 0
    })

    println(request.sumOf { findPattern(it) })
}

private val towels = mutableListOf<String>()
private val patternCache = mutableMapOf<String,Long>()

private fun findPattern(pattern : String) : Long{
    val cache = patternCache[pattern]
    if(cache != null) return cache
    val result = towels.sumOf {
        if(pattern.startsWith(it)){
            findPattern(pattern.substring(it.length))
        }else 0L
    }
    patternCache[pattern] = result
    return result
}