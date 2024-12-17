import java.io.File
import java.lang.IllegalArgumentException

fun day17() {
    val file = File("src/main/resources/Day17Input.txt")

    val input = file.readLines()

    val regexRegister = "Register [ABC]: (?<r>\\d+)".toRegex()

    val digitRegex = "(?<d>\\d+),?".toRegex()

    val registerA = regexRegister.find(input[0])?.getGroupInt("r") ?: 0
    val registerB = regexRegister.find(input[1])?.getGroupInt("r") ?: 0
    val registerC = regexRegister.find(input[2])?.getGroupInt("r") ?: 0

    val program = digitRegex.findAll(input[4]).map { it.getGroupInt("d") as bit3 }.toList()

    val pc = MiniPC(registerA.toLong(),registerB.toLong(),registerC.toLong(),program)

    pc.run()

    val selfPrintingA = completeA(0L,program)

    println(selfPrintingA)

    println(selfPrintingA.toString(2))

    val selfPc = MiniPC(selfPrintingA,0,0,program)

    selfPc.run()
}

private fun completeA(a : Long,program: List<bit3>) : Long{
    if(program.isEmpty()) return a
    val top = program.last()
    return findPossibleAAdditions(a,top).minOfOrNull {
        completeA((a shl 3) + it,program.dropLast(1))
    } ?: Long.MAX_VALUE
}

private fun findPossibleAAdditions(a : Long,op : bit3) : List<bit3>{
    val search = op.toLong() xor 5L
    val rawList = (0..7).toList()
    val filtered =  rawList.filter {
        val A = (a shl 3) + it
        search == ((A / (1L shl ((A % 8).toInt() xor 1))) xor (A % 8)) % 8
    }
    return filtered
}


private class MiniPC(
    var registerA: Long = 0,
    var registerB: Long = 0,
    var registerC: Long = 0,
    val program : List<bit3>
) {

    var instructionPointer = 0

    val output = mutableListOf<Int>()

    fun printOutput() = println(output.joinToString(",") { it.toString() })

    fun run(){
        while (instructionPointer >= 0 && instructionPointer < program.size){
            val opCode = program[instructionPointer]
            val operand = program[instructionPointer+1]
            getOperation(opCode)(operand)
            instructionPointer+=2
        }
        printOutput()
    }

    private fun getCombo(operand : bit3): Long = when(operand){
        0,1,2,3 -> operand.toLong()
        4 -> registerA
        5 -> registerB
        6 -> registerC
        else -> throw IllegalArgumentException("$operand is not a valid combo operand")
    }

    fun getOperation(opCode: bit3) = when(opCode){
        0 -> this::adv
        1 -> this::bxl
        2 -> this::bst
        3 -> this::jnz
        4 -> this::bxc
        5 -> this::out
        6 -> this::bdv
        7 -> this::cdv
        else -> throw IllegalArgumentException("$opCode is not a valid op code")
    }

    fun adv(input : bit3){
        val numerator = registerA
        val denominator = 1 shl getCombo(input).toInt()
        registerA = numerator/denominator
    }
    fun bxl(input : bit3){
        registerB = input.toLong() xor registerB
    }
    fun bst(input : bit3){
        registerB = getCombo(input) % 8
    }
    fun jnz(input : bit3){
        if(registerA == 0L) return
        instructionPointer = input - 2
    }
    @Suppress("UNUSED_PARAMETER")
    fun bxc(input : bit3){
        registerB = registerB xor registerC
    }
    fun out(input : bit3){
        output.add((getCombo(input) % 8).toInt())
    }
    fun bdv(input : bit3){
        val numerator = registerA
        val denominator = 1 shl getCombo(input).toInt()
        registerB = numerator/denominator
    }
    fun cdv(input : bit3){
        val numerator = registerA
        val denominator = 1 shl getCombo(input).toInt()
        registerC = numerator/denominator
    }
}

private typealias bit3 = Int