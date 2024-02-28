package com.inforad.mapapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inforad.mapapp.core.data.source.local.AuthRepository
import com.inforad.mapapp.domain.AuthResult
import com.inforad.mapapp.model.UserModel
import kotlinx.coroutines.launch

class UserViewModel(private val authRepository: AuthRepository): ViewModel() {
    val authResultLiveData: MutableLiveData<AuthResult> = MutableLiveData()

    fun authenticate(username: String, password: String) {
        viewModelScope.launch {
            val result = authRepository.authenticate(username, password)
            authResultLiveData.postValue(result)
        }
    }
}