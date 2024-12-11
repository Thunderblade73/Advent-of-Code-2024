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



fun List<Long>.permute() = buildList<Long>{
    val (zero,zeroRemain) = partition { it == 0L }

    // Zero Rule
    addAll(zero.map { 1L })

    val (even,odd) = zeroRemain.partition { it.toString().length % 2 == 0 }

    // Even Split
    addAll(even.map { it.split() }.flatten())

    // Multiply 2024
    addAll(odd.map { it * 2024L })
}

fun Long.split() : List<Long>{
    val asString = toString()

    val one = asString.substring(0,asString.length/2).toLong()

    val two = asString.substring(asString.length/2).toLong()

    return listOf(one,two)
}
