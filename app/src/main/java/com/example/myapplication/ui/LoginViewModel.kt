package com.example.myapplication.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.models.LoginResponse
import com.example.myapplication.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _loginResult = MutableLiveData<LoginResponse?>()
    val loginResult: LiveData<LoginResponse?> = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _resetResult = MutableLiveData<String?>()
    val resetResult: LiveData<String?> = _resetResult

    private val _signupResult = MutableLiveData<String?>()
    val signupResult: LiveData<String?> = _signupResult

    private val _userData = MutableLiveData<User?>()
    val userData: LiveData<User?> = _userData

    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                if (result.user != null) {
                    fetchUserData(result.user!!.uid)
                    _loginResult.postValue(LoginResponse("firebase_token", null))
                } else {
                    _loginResult.postValue(LoginResponse(null, "Login Failed"))
                }
            } catch (e: Exception) {
                _loginResult.postValue(LoginResponse(null, e.message ?: "Error Occurred"))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun signup(email: String, password: String, name: String = "") {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = result.user
                if (firebaseUser != null) {
                    // Save additional user info to Firestore
                    val user = User(uid = firebaseUser.uid, email = email, name = name)
                    db.collection("users").document(firebaseUser.uid).set(user).await()
                    
                    _signupResult.postValue("Account Created Successfully")
                } else {
                    _signupResult.postValue("Signup Failed")
                }
            } catch (e: Exception) {
                _signupResult.postValue(e.message ?: "Error Occurred")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    private suspend fun fetchUserData(uid: String) {
        try {
            val document = db.collection("users").document(uid).get().await()
            val user = document.toObject(User::class.java)
            _userData.postValue(user)
        } catch (e: Exception) {
            // Handle error fetching user data
        }
    }

    fun resetPassword(email: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                auth.sendPasswordResetEmail(email).await()
                _resetResult.postValue("Reset link sent to your email!")
            } catch (e: Exception) {
                _resetResult.postValue(e.message ?: "Error Occurred")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}
