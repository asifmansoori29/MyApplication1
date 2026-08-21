package com.example.myapplication.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.ActivityEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Edit Profile"

        loadCurrentUserData()

        binding.btnSaveProfile.setOnClickListener {
            val newName = binding.etEditName.text.toString()
            if (newName.isNotEmpty()) {
                updateProfile(newName)
            } else {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadCurrentUserData() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            lifecycleScope.launch {
                try {
                    val document = db.collection("users").document(uid).get().await()
                    if (document != null && document.exists()) {
                        binding.etEditName.setText(document.getString("name"))
                    }
                } catch (e: Exception) {
                    // Handle load error
                }
            }
        }
    }

    private fun updateProfile(newName: String) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            binding.editProgressBar.visibility = View.VISIBLE
            binding.btnSaveProfile.isEnabled = false

            lifecycleScope.launch {
                try {
                    val userUpdates = mapOf("name" to newName)
                    
                    // 10 second timeout to avoid infinite loading
                    val success = withTimeoutOrNull(10000) {
                        db.collection("users").document(uid)
                            .set(userUpdates, SetOptions.merge())
                            .await()
                        true
                    }

                    binding.editProgressBar.visibility = View.GONE
                    if (success == true) {
                        Toast.makeText(this@EditProfileActivity, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        binding.btnSaveProfile.isEnabled = true
                        Toast.makeText(this@EditProfileActivity, "Request Timeout. Check Internet.", Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    binding.editProgressBar.visibility = View.GONE
                    binding.btnSaveProfile.isEnabled = true
                    Toast.makeText(this@EditProfileActivity, "Update Failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
