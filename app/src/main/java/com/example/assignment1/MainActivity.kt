package com.example.assignment1

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var car: ImageView
    private lateinit var heart1: ImageView
    private lateinit var heart2: ImageView
    private lateinit var heart3: ImageView

    private var carLane = 0 // DEFAULT : 0, changed in placeCarInGrid
    private var lane = 1
    private var lives = 3

    private lateinit var gameController: GameController
    private lateinit var gameGrid: GridLayout
    private var controlMode: ControlMode? = null

    // Sensor-related variables
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private val sensorThreshold = 3.0f
    private var lastMoveTime = 0L
    private val debounceTime = 500L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get control mode from MenuActivity
        val modeString = intent.getStringExtra("CONTROL_MODE")
        controlMode = modeString?.let { ControlMode.valueOf(it) }

        heart1 = findViewById(R.id.heart1)
        heart2 = findViewById(R.id.heart2)
        heart3 = findViewById(R.id.heart3)

        val btnLeft: FloatingActionButton = findViewById(R.id.btnLeft)
        val btnRight: FloatingActionButton = findViewById(R.id.btnRight)

        if (controlMode == ControlMode.BUTTON_FAST || controlMode == ControlMode.BUTTON_SLOW) {
            btnLeft.visibility = View.VISIBLE
            btnRight.visibility = View.VISIBLE

            btnLeft.setOnClickListener {
                if (carLane > 0) {
                    carLane--
                    moveCarToLane(car, carLane)
                }
            }

            btnRight.setOnClickListener {
                if (carLane < gameGrid.columnCount - 1) {
                    carLane++
                    moveCarToLane(car, carLane)
                }
            }
        } else {
            btnLeft.visibility = View.GONE
            btnRight.visibility = View.GONE

            // Sensor setup
            sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        }

        Log.d("DEBUG", "initGameGrid called")
        initGameGrid()
    }

    override fun onResume() {
        super.onResume()
        if (controlMode == ControlMode.SENSOR) {
            sensorManager.registerListener(sensorListener, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onPause() {
        super.onPause()
        if (controlMode == ControlMode.SENSOR) {
            sensorManager.unregisterListener(sensorListener)
        }
    }

    private val sensorListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            val x = event?.values?.get(0) ?: return
            val now = System.currentTimeMillis()
            if (now - lastMoveTime < debounceTime) return

            if (x > sensorThreshold && carLane > 0) {
                carLane--
                moveCarToLane(car, carLane)
                lastMoveTime = now
            } else if (x < -sensorThreshold && carLane < gameGrid.columnCount - 1) {
                carLane++
                moveCarToLane(car, carLane)
                lastMoveTime = now
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    private fun initGameGrid() {
        gameGrid = findViewById(R.id.gameGrid)
        createGridMatrix(this, gameGrid)

        gameGrid.post {
            carLane = gameGrid.columnCount / 2
            gameController = GameController(this, gameGrid, controlMode) { onCarCollision() }
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
            setGravity(Gravity.CENTER)
        }

        car.layoutParams = params
        gameGrid.addView(car)
    }

    private fun onCarCollision() {
        lives--
        Log.d("DEBUG", "Lives left: $lives")

        when (lives) {
            2 -> heart3.visibility = View.GONE
            1 -> heart2.visibility = View.GONE
            0 -> {
                heart1.visibility = View.GONE
                gameController.stopGameLoop()
                val intent = Intent(this, MenuActivity::class.java)
                finish()
                startActivity(intent)
            }
        }
    }

    private fun showGameOverDialog() {
        runOnUiThread {
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            builder.setTitle("Game Over")
            builder.setMessage("You lost all lives! Play again?")
            builder.setCancelable(false)
            builder.setPositiveButton("Restart") { _, _ ->
                restartGame()
            }
            builder.setNegativeButton("Exit") { _, _ ->
                finish()
            }
            builder.show()
        }
    }

    private fun restartGame() {
        val intent = Intent(this, MenuActivity::class.java)
        finish()
        startActivity(intent)
    }
}
