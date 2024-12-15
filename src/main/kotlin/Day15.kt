import java.io.File

fun day15() {
    val file = File("src/main/resources/Day15Input.txt")

    var newLine = false
    val (fieldString, commandString) = file.readLines().partition {
        if (it.isEmpty()) {
            newLine = true
        }
        !newLine
    }

    val commands = commandString.map { it.map { Tasks.getTask(it) } }.flatten()

    val board = fieldString.map { it.map { BoardItem.getItem(it) }.toMutableList() }.toMutableList() as Board

    commands.forEach {
        it.move(board)
    }

    val sumBoxes = board.withIndex().sumOf { (y, rows) ->
        rows.withIndex().sumOf { (x, item) ->
            if (item == BoardItem.BOX) {
                y * 100 + x
            } else {
                0
            }
        }
    }

    println(sumBoxes)

    val bigFieldString = fieldString.map {
        it.map {
            when (it) {
                '#' -> "##"
                'O' -> "[]"
                '.' -> ".."
                '@' -> "@."
                else -> ""
            }
        }.joinToString("")
    }

    val bigBoard = bigFieldString.map { it.map { BoardItem.getItem(it) }.toMutableList() }.toMutableList() as Board

    commands.forEach {
        it.moveBig(bigBoard)
    }

    val sumBigBoxes = bigBoard.withIndex().sumOf { (y, rows) ->
        rows.withIndex().sumOf { (x, item) ->
            if (item == BoardItem.BOX_START) {
                y * 100 + x
            } else {
                0
            }
        }
    }

    println(sumBigBoxes)
}

private enum class Tasks(val symbol: Char) {
    SOUTH('v') {
        override fun move(board: Board) {
            val robot = board.robot()
            var yOffset = 1
            while (true) when (board[robot.second + yOffset][robot.first]) {
                BoardItem.WALL -> return
                BoardItem.EMPTY -> break
                else -> yOffset++
            }
            for (y in yOffset.downTo(2)) {
                board[robot.second + y][robot.first] = BoardItem.BOX
            }
            board[robot.second + 1][robot.first] = BoardItem.ROBOT
            board[robot.second][robot.first] = BoardItem.EMPTY
        }

        override fun moveBig(board: Board) {
            val robot = board.robot()
            var yOffset = 1
            val horizontalSize = mutableSetOf(robot.first)
            val slices = mutableListOf<Set<Int>>()
            while (!horizontalSize.all { board[robot.second + yOffset][it] == BoardItem.EMPTY }) {
                slices.add(horizontalSize.toSet())
                slices.last().forEach {
                    when (board[robot.second + yOffset][it]) {
                        BoardItem.WALL -> return
                        BoardItem.BOX_START -> horizontalSize.add(it + 1)
                        BoardItem.BOX_END -> horizontalSize.add(it - 1)
                        BoardItem.EMPTY -> horizontalSize.remove(it)
                        else -> {}
                    }
                }
                yOffset++
            }
            slices.add(horizontalSize)
            slices.reversed().forEach {
                it.forEach {
                    board[robot.second + yOffset][it] = board[robot.second + yOffset - 1][it]
                    board[robot.second + yOffset - 1][it] = BoardItem.EMPTY
                }
                yOffset--
            }
            board[robot.second][robot.first] = BoardItem.EMPTY
        }
    },
    NORTH('^') {
        override fun move(board: Board) {
            val robot = board.robot()
            var yOffset = -1
            while (true) when (board[robot.second + yOffset][robot.first]) {
                BoardItem.WALL -> return
                BoardItem.EMPTY -> break
                else -> yOffset--
            }
            for (y in yOffset..-2) {
                board[robot.second + y][robot.first] = BoardItem.BOX
            }
            board[robot.second - 1][robot.first] = BoardItem.ROBOT
            board[robot.second][robot.first] = BoardItem.EMPTY
        }

        override fun moveBig(board: Board) {
            val robot = board.robot()
            var yOffset = -1
            val horizontalSize = mutableSetOf(robot.first)
            val slices = mutableListOf<Set<Int>>()
            while (!horizontalSize.all { board[robot.second + yOffset][it] == BoardItem.EMPTY }) {
                slices.add(horizontalSize.toSet())
                slices.last().forEach {
                    when (board[robot.second + yOffset][it]) {
                        BoardItem.WALL -> return
                        BoardItem.BOX_START -> horizontalSize.add(it + 1)
                        BoardItem.BOX_END -> horizontalSize.add(it - 1)
                        BoardItem.EMPTY -> horizontalSize.remove(it)
                        else -> {}
                    }
                }
                yOffset--
            }
            slices.add(horizontalSize)
            slices.reversed().forEach {
                it.forEach {
                    board[robot.second + yOffset][it] = board[robot.second + yOffset + 1][it]
                    board[robot.second + yOffset + 1][it] = BoardItem.EMPTY
                }
                yOffset++
            }
            board[robot.second][robot.first] = BoardItem.EMPTY
        }
    },
    EAST('>') {
        override fun move(board: Board) {
            val robot = board.robot()
            var xOffset = 1
            while (true) when (board[robot.second][robot.first + xOffset]) {
                BoardItem.WALL -> return
                BoardItem.EMPTY -> break
                else -> xOffset++
            }
            for (x in xOffset.downTo(2)) {
                board[robot.second][robot.first + x] = BoardItem.BOX
            }
            board[robot.second][robot.first + 1] = BoardItem.ROBOT
            board[robot.second][robot.first] = BoardItem.EMPTY
        }

        override fun moveBig(board: Board) {
            val robot = board.robot()
            var xOffset = 1
            val verticalSize = mutableSetOf(robot.second)
            val slices = mutableListOf<Set<Int>>()
            while (!verticalSize.all { board[it][robot.first + xOffset] == BoardItem.EMPTY }) {
                slices.add(verticalSize.toSet())
                slices.last().forEach {
                    when (board[it][robot.first + xOffset]) {
                        BoardItem.WALL -> return
                        else -> {}
                    }
                }
                xOffset++
            }
            slices.add(verticalSize)
            slices.reversed().forEach {
                it.forEach {
                    board[it][robot.first + xOffset] = board[it][robot.first + xOffset - 1]
                    board[it][robot.first + xOffset - 1] = BoardItem.EMPTY
                }
                xOffset--
            }
            board[robot.second][robot.first] = BoardItem.EMPTY
        }
    },
    WEST('<') {
        override fun move(board: Board) {
            val robot = board.robot()
            var xOffset = -1
            while (true) when (board[robot.second][robot.first + xOffset]) {
                BoardItem.WALL -> return
                BoardItem.EMPTY -> break
                else -> xOffset--
            }
            for (x in xOffset..-2) {
                board[robot.second][robot.first + x] = BoardItem.BOX
            }
            board[robot.second][robot.first - 1] = BoardItem.ROBOT
            board[robot.second][robot.first] = BoardItem.EMPTY
        }

        override fun moveBig(board: Board) {
            val robot = board.robot()
            var xOffset = -1
            val verticalSize = mutableSetOf(robot.second)
            val slices = mutableListOf<Set<Int>>()
            while (!verticalSize.all { board[it][robot.first + xOffset] == BoardItem.EMPTY }) {
                slices.add(verticalSize.toSet())
                slices.last().forEach {
                    when (board[it][robot.first + xOffset]) {
                        BoardItem.WALL -> return
                        else -> {}
                    }
                }
                xOffset--
            }
            slices.add(verticalSize)
            slices.reversed().forEach {
                it.forEach {
                    board[it][robot.first + xOffset] = board[it][robot.first + xOffset + 1]
                    board[it][robot.first + xOffset + 1] = BoardItem.EMPTY
                }
                xOffset++
            }
            board[robot.second][robot.first] = BoardItem.EMPTY
        }
    }

    ;

    abstract fun move(board: Board)

    abstract fun moveBig(board: Board)

    companion object {
        fun getTask(symbol: Char) = entries.first { symbol == it.symbol }
    }
}

private enum class BoardItem(val symbol: Char) {
    WALL('#'), ROBOT('@'), BOX('O'), EMPTY('.'), BOX_START('['), BOX_END(']');

    companion object {
        fun getItem(symbol: Char) = entries.first { symbol == it.symbol }
    }
}

private typealias Board = MutableList<MutableList<BoardItem>>

private fun Board.robot() =
    indexOfFirst { it.indexOf(BoardItem.ROBOT) != -1 }.let { this[it].indexOf(BoardItem.ROBOT) to it } as VecI

private fun Board.print() {
    forEach {
        it.forEach {
            print(it.symbol)
        }
        print("\n")
    }
}
