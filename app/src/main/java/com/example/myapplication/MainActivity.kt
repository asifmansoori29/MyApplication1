package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.ui.LoginActivity
import com.example.myapplication.ui.EditProfileActivity
import com.example.myapplication.utils.SessionManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        sessionManager = SessionManager(this)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDashboard()
    }

    override fun onResume() {
        super.onResume()
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val name = document.getString("name") ?: "User"
                        binding.tvWelcome.text = "Welcome, $name"
                    }
                }
                .addOnFailureListener {
                    binding.tvWelcome.text = "Welcome, User"
                }
        }
    }

    private fun setupDashboard() {
        // Set Current Date
        val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        binding.tvDate.text = sdf.format(Date())

        // Handle Logout
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        // Setup Card Clicks
        binding.cardMarket.setOnClickListener {
            showServiceToast("PSX Market Summary")
        }
        binding.cardLocator.setOnClickListener {
            showServiceToast("CDC Branch Locator")
        }
        binding.cardCurrency.setOnClickListener {
            showServiceToast("Currency Exchange Rates")
        }
        binding.cardNews.setOnClickListener {
            showServiceToast("Financial News")
        }
        binding.cardSupport.setOnClickListener {
            showServiceToast("Contact Support")
        }
        binding.cardPortfolio.setOnClickListener {
            showServiceToast("My Portfolio")
        }
        binding.cardGold.setOnClickListener {
            showServiceToast("Live Gold Rates")
        }
        binding.cardGemini.setOnClickListener {
            showServiceToast("Gemini Financial AI")
        }
    }

    private fun showServiceToast(serviceName: String) {
        Toast.makeText(this, "$serviceName service will be available soon!", Toast.LENGTH_SHORT).show()
    }
}
