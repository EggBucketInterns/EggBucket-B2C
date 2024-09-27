package com.eggbucket.eggbucket_b2c

import android.content.Intent
import android.os.Bundle
<<<<<<< HEAD
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.eggbucket.eggbucket_b2c.uiscreens.LoginActivity
=======
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
>>>>>>> c5ac5022b02779661b7106a50cbe3885668bdd55

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
<<<<<<< HEAD
        val gotoNext=findViewById<TextView>(R.id.tex1)
        gotoNext.setOnClickListener {
            startActivity(Intent(this@MainActivity,LoginActivity::class.java))
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
=======

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.homeFragment, CompanyMenu.newInstance("", ""))
            }
>>>>>>> c5ac5022b02779661b7106a50cbe3885668bdd55
        }
    }
}