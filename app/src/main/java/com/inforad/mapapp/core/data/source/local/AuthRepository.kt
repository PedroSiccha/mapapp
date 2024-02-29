package com.inforad.mapapp.core.data.source.local

import com.inforad.mapapp.domain.AuthResult

interface AuthRepository {
    suspend fun authenticate(username: String, password: String): AuthResult
}