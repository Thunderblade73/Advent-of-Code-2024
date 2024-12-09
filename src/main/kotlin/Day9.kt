import java.io.File

fun day9() {
    val file = File("src/main/resources/Day9Input.txt")

    val inArray = file.readText().map { it.digitToInt() }

    var odd = false
    var indexStart = 0
    var lastIndex = inArray.size / 2
    val remainder = mutableListOf<Int>()

    val xIterator = inArray.listIterator()

    val endList = buildList<Int> {
        while (indexStart <= lastIndex) {
            val x = xIterator.next()
            odd = !odd
            if (odd) {
                for (i in 0..<x) {
                    add(indexStart)
                }
                indexStart++
                continue
            }
            for (i in 0..<x) {
                if (remainder.isEmpty()) {
                    for (y in 0..<inArray[lastIndex * 2]) {
                        remainder.add(lastIndex)
                    }
                    lastIndex--
                }
                add(remainder.first())
                remainder.removeFirst()
            }
        }
        addAll(remainder)
    }

    val sum = endList.withIndex().sumOf { (i, it) -> (i * it).toLong() }

    println(sum)

    val (files, empties) = inArray.withIndex().partition { (i, _) -> i % 2 == 0 }

    val mutEmpties = empties.toMutableList().toSortedSet{a,b -> a.index.compareTo(b.index)}

    val result = files.reversed().associateWith { a ->
        mutEmpties.firstOrNull { it.value >= a.value && it.index <= a.index }?.also {
            if (a.value == it.value) mutEmpties.remove(it) else {
                mutEmpties.remove(it)
                mutEmpties.add(IndexedValue(it.index, (it.value - a.value)))
            }
            mutEmpties.add(a)
        }
    }.toSortedMap { a, b -> a.index.compareTo(b.index) }

    val emptiesMap = mutEmpties.filter { it.value != 0 }.map{ (index, times) ->
        Quad(index,0,times,0)
    }

    val mapped = (result.map { (a, b) ->
        if (b == null) {
            Quad(a.index, a.index, a.value, 0)
        } else {
            Quad(b.index, a.index, a.value, b.value)
        }
    } + emptiesMap).sortedBy { -it.fourth }.sortedBy { it.first }

    val grouped = mapped.groupBy { it.first }.map {
            val last = it.value.last()
            //val trail = if(last.fourth == 0) emptyList<Int>() else (0..<(last.fourth - last.third)).map { 0 }
            it.value.map { (_, index, times, _) -> (0..<times).map { index/2 } }.flatten()//+ trail
        }

    val out2 = grouped.flatten()

    val sum2 = out2.withIndex().sumOf { (i, it) -> (i * it).toLong() }

    println(sum2)
}