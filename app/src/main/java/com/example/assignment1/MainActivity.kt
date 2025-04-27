package com.example.assignment1
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.GridLayout
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.view.View
import android.view.Gravity
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.assignment1.getCellSize
import com.example.assignment1.createGridMatrix
import com.example.assignment1.placeCarInGrid
import com.example.assignment1.moveCarToLane



class MainActivity : AppCompatActivity() {

    private lateinit var car: ImageView
    private var carLane = 1 // 0 = left, 1 = center, 2 = right

    private var lane = 1 // 0 = left, 1 = center, 2 = right
    private lateinit var gameController: GameController

    private lateinit var gameGrid: GridLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnLeft: FloatingActionButton = findViewById(R.id.btnLeft)
        val btnRight: FloatingActionButton = findViewById(R.id.btnRight)

        btnLeft.setOnClickListener {
            if (carLane > 0) {
                carLane--
                moveCarToLane(car, carLane)
            }
        }

        btnRight.setOnClickListener {
            if (carLane < 2) {
                carLane++
                moveCarToLane(car, carLane)
            }
        }

        Log.d("DEBUG", "initGameGrid called")
        initGameGrid()


    }


    private fun initGameGrid() {
        gameGrid = findViewById(R.id.gameGrid)
        createGridMatrix(this, gameGrid)

        // Only after layout is ready (height is real):
        gameGrid.post {
            gameController = GameController(this, gameGrid)

            car = placeCarInGrid(this, gameGrid, carLane)
            gameController.startGameLoop(car)
        }
    }

    protected fun getCellSize(): Pair<Int, Int> {
        val cellHeight = gameGrid.height / gameGrid.rowCount
        val cellWidth = gameGrid.width / gameGrid.columnCount
        return Pair(cellWidth, cellHeight)
    }

    private fun placeCarInGrid() {
        val (cellWidth, cellHeight) = getCellSize(gameGrid)

        car = ImageView(this)
        car.setImageResource(R.drawable.old_car)

        val params = GridLayout.LayoutParams().apply {
            rowSpec = GridLayout.spec(6)
            columnSpec = GridLayout.spec(carLane)
            width = cellWidth
            height = cellHeight
            setGravity(android.view.Gravity.CENTER)
        }

        car.layoutParams = params
        gameGrid.addView(car)
    }

}

