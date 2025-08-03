package com.example.b2c_anup_calender



import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class ScheduleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        // Optional: Handle save button click
        val saveButton: ImageView = findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            // Handle save action if needed
        }
    }
}
