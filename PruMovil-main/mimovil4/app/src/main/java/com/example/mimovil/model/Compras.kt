package com.example.mimovil.model

import com.google.gson.annotations.SerializedName

data class Compras (
    @SerializedName("ID_Entrada") var identrada: String = "",
    @SerializedName("Precio_Compra") var preciocompra: String = "",
    @SerializedName( "ID_Producto") var idproducto: String = "",
    @SerializedName("Documento_Empleado") var documento: String = ""
)