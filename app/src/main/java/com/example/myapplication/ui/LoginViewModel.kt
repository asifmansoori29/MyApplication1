package com.example.myapplication.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.api.LoginRequest
import com.example.myapplication.api.LoginResponse
import com.example.myapplication.api.RetrofitClient
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResponse?>()
    val loginResult: LiveData<LoginResponse?> = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun login(email: String, password: String) {
        _isLoading.value = true
        
        // Demo Check: ReqRes API kabhi kabhi unstable hoti hai, 
        // isliye humne ye demo credentials ka direct check rakha hai.
        if (email == "eve.holt@reqres.in" && password == "cityslicka") {
            _loginResult.postValue(LoginResponse("QpwL5tke4Pnpja7X4", null))
            _isLoading.value = false
            return
        }

        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    _loginResult.postValue(response.body())
                } else {
                    _loginResult.postValue(LoginResponse(null, "Invalid Credentials"))
                }
            } catch (e: Exception) {
                _loginResult.postValue(LoginResponse(null, "Network Error: ${e.message}"))
            } finally {
                _isLoading.postValue(false)
            }
        }
    }
}
