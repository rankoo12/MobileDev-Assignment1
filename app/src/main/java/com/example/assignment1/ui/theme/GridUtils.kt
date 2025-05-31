package com.example.assignment1

import android.content.Context
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.view.Gravity


fun getCellSize(grid: GridLayout): Pair<Int, Int> {
    val cellHeight = grid.height / grid.rowCount
    val cellWidth = grid.width / grid.columnCount
    return Pair(cellWidth, cellHeight)
}

fun createGridMatrix(context: Context, gameGrid: GridLayout, color: Int = 0xFFDDDDDD.toInt()) {
    gameGrid.post {
        val (cellWidth, cellHeight) = getCellSize(gameGrid)

        for (row in 0 until gameGrid.rowCount) {
            for (col in 0 until gameGrid.columnCount) {
                val cell = View(context)
                val params = GridLayout.LayoutParams().apply {
                    rowSpec = GridLayout.spec(row)
                    columnSpec = GridLayout.spec(col)
                    width = cellWidth
                    height = cellHeight
                    //setMargins(2, 2, 2, 2)
                }
                cell.layoutParams = params
                cell.setBackgroundColor(color)
                gameGrid.addView(cell)
            }
        }
    }
}

fun placeCarInGrid(context: Context, gameGrid: GridLayout, lane: Int): ImageView {
    val (cellWidth, cellHeight) = getCellSize(gameGrid)
    val car = ImageView(context)
    car.setImageResource(R.drawable.car)

    val params = GridLayout.LayoutParams().apply {
        rowSpec = GridLayout.spec(gameGrid.rowCount - 1)
        columnSpec = GridLayout.spec(lane)
        width = cellWidth / 2
        height = cellHeight / 2
        //setMargins(100, 100, 100, 100)
        setGravity(Gravity.CENTER)
    }
    car.layoutParams = params
    gameGrid.addView(car)
    return car
}

fun moveCarToLane(car: ImageView, lane: Int) {
    val params = car.layoutParams as GridLayout.LayoutParams
    params.columnSpec = GridLayout.spec(lane)
    params.setGravity(Gravity.CENTER) // ✅ make sure it's still centered
    car.layoutParams = params
}

