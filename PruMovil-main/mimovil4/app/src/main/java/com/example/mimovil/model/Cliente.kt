package com.example.mimovil.model

import com.google.gson.annotations.SerializedName

data class Cliente(
    @SerializedName("documento_Cliente") var documento: String = "",
    @SerializedName("nombre_Cliente") var nombre: String = "",
    @SerializedName("apellido_Cliente") var apellido: String = "",
    var telefono: String = "",
    @SerializedName("fecha_Nacimiento") var fecha: String = "",
    var genero: String = "",
    @SerializedName("ID_Estado") var estado: String = ""
)