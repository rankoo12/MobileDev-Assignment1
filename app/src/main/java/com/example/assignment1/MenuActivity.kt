package com.example.assignment1

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

enum class ControlMode {
    BUTTON_FAST,
    BUTTON_SLOW,
    SENSOR
}

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val buttonFast = findViewById<Button>(R.id.buttonFast)
        val buttonSlow = findViewById<Button>(R.id.buttonSlow)
        val buttonSensor = findViewById<Button>(R.id.buttonSensor)
        val buttonHighscores = findViewById<Button>(R.id.buttonHighscores)

        buttonFast.setOnClickListener {
            launchGame(ControlMode.BUTTON_FAST)
        }

        buttonSlow.setOnClickListener {
            launchGame(ControlMode.BUTTON_SLOW)
        }

        buttonSensor.setOnClickListener {
            launchGame(ControlMode.SENSOR)
        }

        // New highscores button handler
        buttonHighscores.setOnClickListener {
            val intent = Intent(this, HighscoreActivity::class.java)
            startActivity(intent)
        }
    }

    private fun launchGame(mode: ControlMode) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("CONTROL_MODE", mode.name)
        startActivity(intent)
    }
}
