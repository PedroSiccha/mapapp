package com.inforad.mapapp.model

class UserProvider {
    val user = listOf<UserModel>(
        UserModel(
            email = "pedro@gmail.com",
            password = "123456"
        ),
        UserModel(
            email = "juan@gmail.com",
            password = "123456"
        )
    )
}