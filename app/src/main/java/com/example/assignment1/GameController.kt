package com.example.assignment1

import android.Manifest
import android.view.View
import android.view.Gravity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.GridLayout
import android.widget.ImageView
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.view.isVisible

class GameController(
    private val context: Context,
    private val gameGrid: GridLayout,
    private val controlMode: ControlMode?,
    private val onCollision: () -> Unit,
    private val onDistanceUpdate: (Int) -> Unit
) {
    private val handler = Handler(Looper.getMainLooper())
    private val obstacles = mutableListOf<Obstacle>()
    private val coins = mutableListOf<Coin>()
    private var obstacleId = 1000
    private var coinId = 2000
    private var tickCount = 0
    private var lastObstacleCol = -1
    private var isRunning = false
    private var distance = 0

    private val gameSpeed: Long = when (controlMode) {
        ControlMode.BUTTON_FAST -> 500L
        ControlMode.BUTTON_SLOW -> 1000L
        else -> 1000L
    }

    data class Obstacle(var row: Int, var col: Int, val imageViewId: Int)
    data class Coin(var row: Int, var col: Int, val imageViewId: Int)

    fun startGameLoop(car: ImageView) {
        isRunning = true
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (!isRunning) return
                tick(car)
                handler.postDelayed(this, gameSpeed)
            }
        }, gameSpeed)
    }

    fun tick(car: ImageView) {
        moveObjects()
        detectCollision(car)

        tickCount++
        if (tickCount % 2 == 0) {
            spawnObstacle()
        }
        if ((0..99).random() < 30) {
            spawnCoin()
        }
    }

    private fun chooseColForObstacle(): Int {
        var col = (0 until gameGrid.columnCount).random()
        while (col == lastObstacleCol) {
            col = (0 until gameGrid.columnCount).random()
        }
        lastObstacleCol = col
        return col
    }

    fun spawnObstacle() {
        val col = chooseColForObstacle()
        val (cellWidth, cellHeight) = getCellSize(gameGrid)

        val imageView = ImageView(context)
        imageView.setImageResource(R.drawable.obstacle)

        val params = GridLayout.LayoutParams().apply {
            rowSpec = GridLayout.spec(0)
            columnSpec = GridLayout.spec(col)
            width = cellWidth
            height = cellHeight
            setGravity(Gravity.CENTER)
        }

        imageView.layoutParams = params
        imageView.id = obstacleId++
        gameGrid.addView(imageView)
        obstacles.add(Obstacle(0, col, imageView.id))
    }

    fun spawnCoin() {
        val availableCols = (0 until gameGrid.columnCount).toMutableList()

        for (obstacle in obstacles) {
            if (obstacle.row == 0) {
                availableCols.remove(obstacle.col)
            }
        }

        if (availableCols.isEmpty()) return

        val col = availableCols.random()
        val (cellWidth, cellHeight) = getCellSize(gameGrid)

        val imageView = ImageView(context)
        imageView.setImageResource(R.drawable.coin)

        val params = GridLayout.LayoutParams().apply {
            rowSpec = GridLayout.spec(0)
            columnSpec = GridLayout.spec(col)
            width = cellWidth
            height = cellHeight
            setGravity(Gravity.CENTER)
        }

        imageView.layoutParams = params
        imageView.id = coinId++
        gameGrid.addView(imageView)
        coins.add(Coin(0, col, imageView.id))
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    private fun detectCollision(car: ImageView) {
        for (obstacle in obstacles) {
            val obstacleView = gameGrid.findViewById<ImageView>(obstacle.imageViewId)
            if (obstacleView.isVisible) {
                val carParams = car.layoutParams as GridLayout.LayoutParams
                val obstacleParams = obstacleView.layoutParams as GridLayout.LayoutParams
                if (carParams.columnSpec == obstacleParams.columnSpec &&
                    carParams.rowSpec == obstacleParams.rowSpec
                ) {
                    FeedbackUtils.showToast(context, "Collision Detected!")
                    FeedbackUtils.vibrate(context)
                    onCollision()
                }
            }
        }
    }

    private fun moveObjects() {
        val obstacleIterator = obstacles.iterator()
        while (obstacleIterator.hasNext()) {
            val obstacle = obstacleIterator.next()
            val view = gameGrid.findViewById<ImageView>(obstacle.imageViewId)

            if (obstacle.row >= gameGrid.rowCount - 1) {
                gameGrid.removeView(view)
                obstacleIterator.remove()
                continue
            }

            obstacle.row += 1
            val (cellWidth, cellHeight) = getCellSize(gameGrid)
            val params = GridLayout.LayoutParams().apply {
                rowSpec = GridLayout.spec(obstacle.row)
                columnSpec = GridLayout.spec(obstacle.col)
                width = cellWidth
                height = cellHeight
                setGravity(Gravity.CENTER)
            }

            gameGrid.removeView(view)
            gameGrid.addView(view, params)
        }

        val coinIterator = coins.iterator()
        while (coinIterator.hasNext()) {
            val coin = coinIterator.next()
            val view = gameGrid.findViewById<ImageView>(coin.imageViewId)

            if (coin.row >= gameGrid.rowCount - 1) {
                gameGrid.removeView(view)
                coinIterator.remove()
                continue
            }

            coin.row += 1
            val (cellWidth, cellHeight) = getCellSize(gameGrid)
            val params = GridLayout.LayoutParams().apply {
                rowSpec = GridLayout.spec(coin.row)
                columnSpec = GridLayout.spec(coin.col)
                width = cellWidth
                height = cellHeight
                setGravity(Gravity.CENTER)
            }

            gameGrid.removeView(view)
            gameGrid.addView(view, params)
        }

        distance++
        onDistanceUpdate(distance)
    }

    fun stopGameLoop() {
        isRunning = false
        handler.removeCallbacksAndMessages(null)
    }
}
