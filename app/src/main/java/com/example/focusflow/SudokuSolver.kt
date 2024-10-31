package com.example.focusflow

data class SudokuBoard(
    internal val board: Array<Array<Int?>>,
    internal val choices: Array<Array<MutableSet<Int>>> = Array(SUDOKU_SIZE) { Array(SUDOKU_SIZE) { (1..SUDOKU_SIZE).toMutableSet() } },
    internal val hasPropagated: Array<Array<Boolean>> = Array(SUDOKU_SIZE) { Array(SUDOKU_SIZE) { false } },
    internal var unsolved: Int = SUDOKU_SIZE * SUDOKU_SIZE
) {
    companion object {
        const val SUDOKU_SIZE: Int = 9
    }

    private constructor(existingBoard: Array<Array<Int?>>) : this(board = existingBoard) {
        for (row in 0..<SUDOKU_SIZE) {
            for (col in 0..<SUDOKU_SIZE) {
                if (board[row][col] != 0) {
                    choices[row][col].clear();
                    --unsolved;
                }
            }
        }
    }

    fun isSolved(): Boolean {
        return unsolved == 0;
    }
}

class SudokuSolver(
    private val board: SudokuBoard
) {
    private constructor(existingBoard: Array<Array<Int?>>) : this(SudokuBoard(existingBoard))

    fun solve(): Array<Array<Int?>> {
        if (board.isSolved()) {
            return board.board
        }

    }

    fun eliminate(x: Int, y: Int, value: Int) {
        if (board.choices[x][y].contains(value)) {
            board.choices[x][y].remove(value)
        }
    }

    fun propagate(x: Int, y: Int) {
        if (board.board[x][y] == null) {
            return
        }
        if (board.hasPropagated[x][y]) {
            return
        }
    }
}
