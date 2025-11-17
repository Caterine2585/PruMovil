package com.example.mimovil.model

import com.google.gson.annotations.SerializedName

data class Proveedor (
    @SerializedName("ID_Proveedor") var id_proveedor: String = "",
    @SerializedName("Nombre_Proveedor") var nombre_proveedor: String = "",
    @SerializedName("Correo_Electronico") var correo_proveedor: String = "",
    @SerializedName("Telefono") var telefono: String = "",
    @SerializedName("ID_Estado") var id_estado: String = ""
)