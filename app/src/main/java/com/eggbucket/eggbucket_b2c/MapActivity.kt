package com.eggbucket.eggbucket_b2c

import android.location.Geocoder
import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.util.*


data class FinalAddress(
    val fullAddress: String,
    val coordinates: GeoPoint
)

class MapFragment : Fragment() {

    private lateinit var mapView: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var marker: Marker
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private var finalAddress: String? = null
    private var finalCoordinates: GeoPoint? = null
    var fullFinalAddress: FinalAddress? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_map, container, false)
        val saveAddressBtn=view.findViewById<Button>(R.id.save_address)
        // Inflate the layout for this fragment
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the map
        mapView = view.findViewById(R.id.osm_map)
        Configuration.getInstance().userAgentValue = requireContext().packageName

        // Set the tile source
        mapView.setTileSource(TileSourceFactory.MAPNIK)

        // Enable Zoom Controls
        mapView.setBuiltInZoomControls(true)
        mapView.setMultiTouchControls(true)

        // Request user's location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        val saveButton = view.findViewById<Button>(R.id.save_address)
        saveButton.setOnClickListener{
            println("Final address: $finalAddress")
            println("Final co-ordinates: $finalCoordinates")
            fullFinalAddress = FinalAddress(fullAddress = finalAddress!!, coordinates = finalCoordinates!!)
            println("Final fullFinalAddress: $fullFinalAddress")
            findNavController().navigate(R.id.action_mapFragment_to_addAddressFragment)
        }
        checkLocationPermission()
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            val current = getCurrentLocation()
            println("Current Co-ordinates: $current")
        }
    }

    private fun getCurrentLocation()  {
        var currentLocation = GeoPoint(0.0, 0.0)
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val currentLat = it.latitude
                val currentLon = it.longitude
                currentLocation = GeoPoint(currentLat, currentLon)
                finalCoordinates = currentLocation
                // Move the map to the user's current location
                mapView.controller.setZoom(15.0)
                mapView.controller.setCenter(currentLocation)
                // Add a draggable marker at the user's location
                addMarker(currentLocation)
            }
        }
    }

    private fun addMarker(location: GeoPoint) {
        marker = Marker(mapView)
        marker.position = location
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.isDraggable = true
        marker.title = "Current Address:"
        getAddressFromLocation(location)

        // Set marker drag listener
        marker.setOnMarkerDragListener(object : Marker.OnMarkerDragListener {
            override fun onMarkerDrag(marker: Marker?) {
                // This method can be used for real-time dragging feedback if needed
            }

            override fun onMarkerDragEnd(marker: Marker?) {
                marker?.let {
                    val newLocation = it.position
                    finalCoordinates = newLocation
                    // Get address from the new location
                    getAddressFromLocation(newLocation)
                    mapView.controller.setCenter(newLocation)
                }
            }

            override fun onMarkerDragStart(marker: Marker?) {
                // Called when dragging starts
            }
        })

        mapView.overlays.add(marker)
    }

    private fun getAddressFromLocation(geoPoint: GeoPoint) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val latitude = geoPoint.latitude
        val longitude = geoPoint.longitude

        // Use a coroutine to handle reverse geocoding in the background
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
                if (addresses != null && addresses.isNotEmpty()) {
                    val address = addresses[0].getAddressLine(0)
                    // Update UI on the main thread
                    CoroutineScope(Dispatchers.Main).launch {
                        marker.snippet = address
                        finalAddress = address
                        marker.showInfoWindow()
                        // Toast.makeText(requireContext(), "Address: $address", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("MapFragment", "Error getting address: ${e.message}")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation()
        }
    }
}
