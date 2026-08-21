package com.example.myapplication.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.models.LoginResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _loginResult = MutableLiveData<LoginResponse?>()
    val loginResult: LiveData<LoginResponse?> = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _resetResult = MutableLiveData<String?>()
    val resetResult: LiveData<String?> = _resetResult

    private val _signupResult = MutableLiveData<String?>()
    val signupResult: LiveData<String?> = _signupResult

    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                if (result.user != null) {
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

    fun signup(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                if (result.user != null) {
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
