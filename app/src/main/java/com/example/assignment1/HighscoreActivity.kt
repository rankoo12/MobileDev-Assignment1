package com.example.assignment1

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.model.LatLng
import kotlin.random.Random

class HighscoreActivity : AppCompatActivity(), HighscoreListFragment.OnHighscoreSelectedListener {

    private lateinit var mapFragment: HighscoreMapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_highscore)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_list, HighscoreListFragment())
                .replace(R.id.fragment_container_map, HighscoreMapFragment())
                .commit()
        }

        // Ensure fragments are attached before accessing them
        supportFragmentManager.executePendingTransactions()
        mapFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_map) as HighscoreMapFragment
    }



    override fun onHighscoreSelected(highscore: Highscore) {
        Log.d("HighscoreActivity", "Selected score lat: ${highscore.latitude}, lng: ${highscore.longitude}")
        mapFragment.updateLocation(highscore.latitude, highscore.longitude)
    }
}
