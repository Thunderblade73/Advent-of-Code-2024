import java.io.File

fun day5() {
    val file = File("src/main/resources/Day5Input.txt")

    val ruleRegex = "(\\d+)\\|(\\d+)".toRegex()

    val pageRegex = "(\\d+),?".toRegex()

    val rules = mutableMapOf<Int, List<Int>>()

    val pageLine = mutableListOf<List<Int>>()

    file.forEachLine {
        val ruleMatch = ruleRegex.find(it)
        if (ruleMatch != null) {
            val first = ruleMatch.groupValues[1].toInt()
            val second = ruleMatch.groupValues[2].toInt()
            rules.merge(first, listOf(second)) { a, b -> a + b }
        } else {
            if (it != "") {
                pageLine.add(pageRegex.findAll(it).map { it.groupValues[1].toInt() }.toList())
            }
        }
    }

    val correctPages = pageLine.filter { list ->
        rules.all { (key, value) ->
            val index = list.indexOf(key)
            if (index == -1) {
                true
            } else {
                value.all {
                    val sIndex = list.indexOf(it)
                    if (sIndex == -1) {
                        true
                    } else {
                        index < sIndex
                    }
                }
            }
        }
    }

    val correctPageSum = correctPages.sumOf { it.get(it.size / 2) }

    println(correctPageSum)

    val rawWrongPages = pageLine.toSet() - correctPages.toSet()

    val correctedPages = rawWrongPages.map {
        val page = it.toMutableList()
        val subRuleSet = rules.filter { (key, _) -> page.contains(key) }.mapValues { (_, value) ->
            value.filter { page.contains(it) }
        }
        do {
            val toSwap = subRuleSet.firstNotNullOfOrNull { (key, value) ->
                val after = value.firstOrNull {
                    page.indexOf(key) >= page.indexOf(it)
                }
                if (after == null) null else key to after
            }
            if(toSwap != null){
                page[page.indexOf(toSwap.first)] = toSwap.second
                page[page.indexOf(toSwap.second)] = toSwap.first
            }
        } while (toSwap != null)
        page
    }

    val correctedPageSum = correctedPages.sumOf { it.get(it.size / 2) }

    println(correctedPageSum)

}