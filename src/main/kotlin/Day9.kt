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

    val mutEmpties = empties.toMutableList()


    val result = files.reversed().associateWith { a ->
        mutEmpties.firstOrNull { it.value >= a.value && it.index <= a.index }?.also {
            if (a.value == it.value) mutEmpties.remove(it) else {
                mutEmpties[mutEmpties.indexOf(it)] = IndexedValue(it.index, (it.value - a.value))
            }
        }
    }.toSortedMap{a,b -> a.index.compareTo(b.index)}

    result.toList().sortedBy {a,b->

    }

    val mutFile = buildList<Int> {

    }


}