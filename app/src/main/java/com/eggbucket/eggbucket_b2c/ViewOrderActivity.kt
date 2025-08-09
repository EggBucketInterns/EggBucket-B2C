package com.eggbucket.eggbucket_b2c

import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ViewOrderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_view_order) // ← Replace with your actual XML layout name

        val buildingName = findViewById<TextView>(R.id.buildingName)
        val locationName = findViewById<TextView>(R.id.locationName)
        val itemsContainer = findViewById<LinearLayout>(R.id.itemsContainer)
        val totalPrice = findViewById<TextView>(R.id.totalPrice)
        val backButton = findViewById<ImageView>(R.id.backButton)

        backButton.setOnClickListener { finish() }

        val building = intent.getStringExtra("buildingName") ?: "Building name"
        val location = intent.getStringExtra("locationName") ?: "Location Name, Area Name"

        val items = intent.getSerializableExtra("orderItems") as? List<Pair<String, Int>>
            ?: listOf(
                "Item-1" to 100,
                "Item-2" to 150,
                "Item-3" to 200
            )

        buildingName.text = "   $building"
        locationName.text = "   $location"

        var total = 0
        for ((name, price) in items) {
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                weightSum = 2f
            }

            val nameView = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                text = name
                textSize = 16f
            }

            val priceView = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                text = "₹$price"
                textSize = 16f
                gravity = Gravity.END
            }

            row.addView(nameView)
            row.addView(priceView)
            itemsContainer.addView(row)

            total += price
        }

        totalPrice.text = "Total: ₹$total"
    }
}