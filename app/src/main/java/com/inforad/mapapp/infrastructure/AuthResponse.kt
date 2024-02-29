package com.inforad.mapapp.infrastructure

import com.inforad.mapapp.domain.User

data class AuthResponse(
    val status: Int,
    val message: String,
    val user: User
)
