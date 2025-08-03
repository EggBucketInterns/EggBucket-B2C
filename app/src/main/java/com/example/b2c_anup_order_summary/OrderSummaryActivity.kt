package com.example.b2c_anup_order_summary



import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class OrderSummaryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_summary)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewOrders)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val orders = listOf(
            OrderItem(6, "16th June", R.drawable.eggs_6),
            OrderItem(12, "15th June", R.drawable.eggs_12),
            OrderItem(30, "14th June", R.drawable.eggs_30),
            OrderItem(6, "13th June", R.drawable.eggs_6),
            OrderItem(30, "12th June", R.drawable.eggs_30)
        )

        recyclerView.adapter = OrderAdapter(orders)
    }
}



