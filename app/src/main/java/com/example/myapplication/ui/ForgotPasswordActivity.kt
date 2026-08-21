package com.example.myapplication.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Enable Back Arrow in Action Bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Forgot Password"

        binding.btnResetPassword.setOnClickListener {
            val email = binding.etResetEmail.text.toString()
            if (email.isNotEmpty()) {
                viewModel.resetPassword(email)
            } else {
                Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvBackToLogin.setOnClickListener {
            finish() // Go back to Login
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.lottieAnimation.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnResetPassword.isEnabled = !isLoading
            
            if (isLoading) {
                binding.ivLogo.visibility = View.GONE
            } else {
                binding.ivLogo.visibility = View.VISIBLE
            }
        }

        viewModel.resetResult.observe(this) { message ->
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                if (message.contains("sent")) {
                    finish() // Close activity on success
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish() // Handle top back arrow click
        return true
    }
}
