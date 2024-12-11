import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import kotlin.coroutines.*
import kotlin.time.Duration.Companion.seconds

suspend fun day11(){
    val file = File("src/main/resources/Day11Input.txt")

    val inState = file.readText().split(" ").map { it.toLong() }

    var firstRun = inState
    for( x in 0..24){
        firstRun = firstRun.permute()
    }

    println(firstRun.size)
    println()

    val workStack = ConcurrentLinkedDeque<Triple<Int,Int,List<Long>>>()
    val resultStack = mutableListOf<Triple<Int,Int,List<Long>>>()

    workStack.addAll(firstRun.mapIndexed{i,it -> Triple(i,24,listOf(it))})

    fun doWork(it : Triple<Int,Int,List<Long>>){
        val a = Triple(it.first,it.second+1,it.third.permute())
        println("${it.second} : ${it.first}")
        if(it.second == 73){
            resultStack.add(a)
        }else{
            workStack.add(a)
        }
    }

    runBlocking {
        for (i in 0..63){
            launch {
                while (workStack.isNotEmpty()){
                    val a = workStack.poll() ?: break
                    doWork(a)
                }
            }
        }
    }

    while (workStack.isNotEmpty()){
        delay(10.seconds)
    }


    println(resultStack.sumOf { it.third.size })
}



fun List<Long>.permute() : List<Long>{

    val outList = mutableListOf<Pair<Int,Long>>()

    val (zero,zeroRemain) = withIndex().map { Pair(it.index*2,it.value) }.partition { it.second == 0L }

    // Zero Rule
    outList.addAll(zero.map { Pair(it.first * 2,1) })

    val (even,odd) = zeroRemain.partition { it.second.toString().length % 2 == 0 }

    // Even Split
    outList.addAll(even.map { it.split() }.flatten())

    // Multiply 2024
    outList.addAll(odd.map { Pair(it.first,it.second * 2024L) })

    outList.sortWith { a,b ->
        a.first.compareTo(b.first)
    }

    return outList.map { it.second }
}

fun Pair<Int,Long>.split() : List<Pair<Int,Long>>{
    val asString = second.toString()

    val one = asString.substring(0,asString.length/2).toLong()

    val two = asString.substring(asString.length/2).toLong()

    return listOf(Pair(first,one),Pair(first + 1,two))
}
