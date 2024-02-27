package com.inforad.mapapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.inforad.mapapp.model.UserModel

class UserViewModel: ViewModel() {
    val userModel = MutableLiveData<UserModel>()

}