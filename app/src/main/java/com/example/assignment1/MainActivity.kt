package com.example.assignment1
import com.google.android.material.floatingactionbutton.FloatingActionButton

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var car: ImageView
    private var lane = 1 // 0 = left, 1 = center, 2 = right

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        car = findViewById(R.id.car)
        val btnLeft: FloatingActionButton = findViewById(R.id.btnLeft)
        val btnRight: FloatingActionButton = findViewById(R.id.btnRight)


        btnLeft.setOnClickListener {
            if (lane > 0) {
                lane--
                moveCarToLane(lane)
            }
        }

        btnRight.setOnClickListener {
            if (lane < 2) {
                lane++
                moveCarToLane(lane)
            }
        }
    }
    private fun moveCarToLane(lane: Int) {
        val translationX = when (lane) {
            0 -> -300f // left lane
            1 -> 0f    // center lane
            2 -> 300f  // right lane
            else -> 0f
        }
        car.translationX = translationX
    }

}
