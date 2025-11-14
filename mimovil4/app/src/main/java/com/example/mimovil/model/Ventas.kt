package com.example.mimovil.model

import com.google.gson.annotations.SerializedName

data class Ventas(
    @SerializedName("ID_Venta") var id_venta : String ="",
    @SerializedName("Documento_Cliente") var documento_cli : String ="",
    @SerializedName ("Documento_Empleado") var documento_emp  : String ="",
)