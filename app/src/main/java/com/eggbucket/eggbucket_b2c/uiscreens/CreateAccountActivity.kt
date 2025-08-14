package com.eggbucket.eggbucket_b2c.uiscreens
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eggbucket.eggbucket_b2c.R
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class CreateAccountActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var btnCreateAccount: Button
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_account)

        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        btnCreateAccount = findViewById(R.id.btnSendOtp)
        db = FirebaseFirestore.getInstance()

        btnCreateAccount.setOnClickListener {
            val name = etName.text.toString().trim()
            val phone = etPhone.text.toString().trim()

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = hashMapOf(
                "name" to name,
                "phoneNumber" to phone,
                "createdAt" to Timestamp.now()
            )

            db.collection("users").document(phone)
                .set(user)
                .addOnSuccessListener {
                    Toast.makeText(this, "Account Created", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}