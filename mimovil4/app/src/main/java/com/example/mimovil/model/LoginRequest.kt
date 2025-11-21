package com.example.mimovil.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("Documento_Empleado")
    val documento: String,

    @SerializedName("ID_Contrasena")
    val idContrasena: String
)
