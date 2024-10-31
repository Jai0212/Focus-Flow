package com.example.focusflow

import kotlin.collections.toTypedArray

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

    fun solve(): Array<Array<Int>> {
        if (!board.isSolved()) {
            while (board.unsolved > 0) {
                propagateAll()
                var threshold = 1;
                val prevUnsolved = board.unsolved;
                while (prevUnsolved == board.unsolved) {
                    fillAll(threshold);
                    ++threshold;
                }
            }
        }
        return board.board.map { row ->
            row.map { it ?: 0 }.toTypedArray() // Replace nulls with 0
        }.toTypedArray()
    }

    fun fillAll(threshold: Int) {
        for (row in 0..<SudokuBoard.SUDOKU_SIZE) {
            for (col in 0..<SudokuBoard.SUDOKU_SIZE) {
                fill(row, col, threshold)
            }
        }
    }

    fun fill(row: Int, col: Int, threshold: Int) {
        if (1 <= board.choices[row][col].size && board.choices[row][col].size <= threshold) {
            board.board[row][col] = board.choices[row][col].first()
            --board.unsolved
            board.choices[row][col].clear()
            if (threshold > 1) {
                return
            }
        }
    }

    fun eliminate(row: Int, col: Int, value: Int) {
        if (board.choices[row][col].contains(value)) {
            board.choices[row][col].remove(value)
        }
    }

    fun propagateAll() {
        for (row in 0..<SudokuBoard.SUDOKU_SIZE) {
            for (col in 0..<SudokuBoard.SUDOKU_SIZE) {
                propagate(row, col)
            }
        }
    }


    fun propagate(row: Int, col: Int) {
        if (board.board[row][col] == null) {
            return
        }
        if (board.hasPropagated[row][col]) {
            return
        }
        val value = board.board[row][col]!!
        for (i in 0..<SudokuBoard.SUDOKU_SIZE) {
            if (i != row) {
                eliminate(i, col, value)
            }
            if (i != col) {
                eliminate(row, i, value)
            }
        }
        val startRow = (row / 3) * 3
        val startCol = (col / 3) * 3
        for (row in startRow..startRow + 2) {
            for (col in startCol..startCol + 2) {
                if (!(row == row && col == col)) {
                    eliminate(row, col, value)
                }
            }
        }
        board.hasPropagated[row][col] = true
    }
}
