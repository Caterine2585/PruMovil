package com.example.mimovil.model

import com.google.gson.annotations.SerializedName

class DetalleCompras(
    @SerializedName("Fecha_Entrada") var fechaentrada: String = "",
    @SerializedName("Cantidad") var cantidad: String = "",
    @SerializedName("ID_Proveedor") var idproveedor: String = "",
    @SerializedName("ID_Entrada") var identrada: String = ""
)