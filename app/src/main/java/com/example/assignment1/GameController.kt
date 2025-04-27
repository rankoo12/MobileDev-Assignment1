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
    private val gameGrid: GridLayout
) {
    private val handler = Handler(Looper.getMainLooper())
    private val obstacles = mutableListOf<Obstacle>()
    private var obstacleId = 1000
    private var tickCount = 0
    private var lastObstacleCol = -1


    fun startGameLoop(car: ImageView) {
        handler.postDelayed(object : Runnable {
            override fun run() {
                tick(car)
                handler.postDelayed(this, 1000)
            }
        }, 1000)
    }

    fun tick(car: ImageView) {
        moveObstacles()
        detectCollision(car)

        tickCount++
        if (tickCount % 2 == 0) { // Every 2 seconds (every 2 ticks)
            spawnObstacle()
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
            setGravity(android.view.Gravity.CENTER)
        }

        imageView.layoutParams = params
        imageView.id = obstacleId++
        gameGrid.addView(imageView)
        obstacles.add(Obstacle(0, col, imageView.id))
    }


    @RequiresPermission(Manifest.permission.VIBRATE)
    private fun detectCollision(car: ImageView) {
        for (obstacle in obstacles) {
            val obstacleView = gameGrid.findViewById<ImageView>(obstacle.imageViewId)
            if (obstacleView.isVisible) {
                val carParams = car.layoutParams as GridLayout.LayoutParams
                val obstacleParams = obstacleView.layoutParams as GridLayout.LayoutParams
                if (carParams.columnSpec == obstacleParams.columnSpec && carParams.rowSpec == obstacleParams.rowSpec) {
                    FeedbackUtils.showToast(context, "Collision Detected!")
                    FeedbackUtils.vibrate(context)

                }
            }
        }

    }

    private fun moveObstacles() {
        val iterator = obstacles.iterator()

        while (iterator.hasNext()) {
            val obstacle = iterator.next()
            val view = gameGrid.findViewById<ImageView>(obstacle.imageViewId)

            // Already at last playable row? Remove it.
            if (obstacle.row >= gameGrid.rowCount - 1) {
                gameGrid.removeView(view)
                iterator.remove()
                continue
            }

            obstacle.row += 1
            val (cellWidth, cellHeight) = getCellSize(gameGrid)
            val params = GridLayout.LayoutParams().apply {
                rowSpec = GridLayout.spec(obstacle.row)
                columnSpec = GridLayout.spec(obstacle.col)
                width = cellWidth
                height = cellHeight
                setGravity(android.view.Gravity.CENTER)
            }

            gameGrid.removeView(view)
            gameGrid.addView(view, params)

        }
    }


}
