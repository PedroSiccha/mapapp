package com.inforad.mapapp.core.data.source.local

import com.inforad.mapapp.domain.AuthResult
import com.inforad.mapapp.infrastructure.AuthRequest
import com.inforad.mapapp.infrastructure.AuthService

class AuthRepositoryImpl(private val authService: AuthService): AuthRepository {
    override suspend fun authenticate(username: String, password: String): AuthResult {
        return try {
            val response = authService.authenticate(AuthRequest(username, password))
            if (response.isSuccessful) {
                val user = response.body()?.user ?: throw Exception("User not found")
                AuthResult.Success(user)
            } else {
                AuthResult.Error(response.message())
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Unknown error")
        }
    }
}