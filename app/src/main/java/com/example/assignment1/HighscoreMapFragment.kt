package com.example.assignment1

import kotlin.random.Random
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class HighscoreMapFragment : Fragment(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_highscore_map, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            ?: SupportMapFragment.newInstance().also {
                childFragmentManager.beginTransaction().replace(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)

        return view
    }



    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        // Optional: set default location and zoom
        val defaultLocation = LatLng(0.0, 0.0)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 2f))
    }

    // Function to update marker based on selected highscore
    fun updateLocation(lat: Double, lng: Double) {
        googleMap?.clear()
        val position = LatLng(lat, lng)
        googleMap?.addMarker(MarkerOptions().position(position))
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 10f))
    }
}
