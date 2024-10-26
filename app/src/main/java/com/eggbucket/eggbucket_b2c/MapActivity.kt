package com.eggbucket.eggbucket_b2c

import android.location.Geocoder
import android.Manifest
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.location.Address
import android.location.Location
import android.location.LocationManager
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
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
    val fullAddress: FullAddress,
    val coordinates: GeoPoint
)

class MapFragment : Fragment() {

    private lateinit var mapView: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    // private lateinit var locationManager: LocationManager
    private lateinit var marker: Marker
    private val LOCATION_PERMISSION_REQUEST_CODE = 2
    private var finalAddress: FullAddress? = null
    private var finalCoordinates: GeoPoint? = null
    var fullFinalAddress: FinalAddress? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_map, container, false)

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

        // Using the correct Context for the service
        // locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        val saveButton = view.findViewById<Button>(R.id.save_address)
        saveButton.setOnClickListener{

            fullFinalAddress = FinalAddress(fullAddress = finalAddress!!, coordinates = finalCoordinates!!)
            println("Final fullFinalAddress: $fullFinalAddress")
            val sharedPreferences=requireContext().getSharedPreferences("my_preference", Context.MODE_PRIVATE)
            val editor=sharedPreferences.edit()
            val addressJson= Gson().toJson(fullFinalAddress)
            editor.putString("address",addressJson)
            editor.apply()
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
        // var currentLocation = GeoPoint(0.0, 0.0)
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
                val currentLocation = GeoPoint(currentLat, currentLon)
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
                    val addressLine2 = addresses[0].thoroughfare ?: ""
                    val area = addresses[0].subLocality ?: ""
                    val city = addresses[0].locality ?: ""
                    val state = addresses[0].adminArea ?: ""
                    val postalCode = addresses[0].postalCode ?: ""
                    val country = addresses[0].countryName ?: ""
                    val fullAddress = FullAddress(
                        addressLine2 = addressLine2,
                        area = area,
                        city = city,
                        state = state,
                        zipCode = postalCode,
                        country = country
                    )
                    // Update UI on the main thread
                    CoroutineScope(Dispatchers.Main).launch {
                        marker.snippet = address
                        finalAddress = fullAddress
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
