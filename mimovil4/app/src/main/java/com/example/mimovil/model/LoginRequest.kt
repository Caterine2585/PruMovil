package com.example.mimovil.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("documento_Empleado")
    val documentoEmpleado: String,

    @SerializedName("contrasena")
    val contrasena: String
)
