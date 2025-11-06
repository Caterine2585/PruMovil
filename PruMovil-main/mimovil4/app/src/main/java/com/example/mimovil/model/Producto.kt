package com.example.mimovil.model

import com.google.gson.annotations.SerializedName

data class Producto(
    @SerializedName("ID_Producto") val ID_Producto: String = "",
    @SerializedName ("Nombre_Producto")val Nombre_Producto: String = "",
    @SerializedName ("Descripcion")val Descripcion: String = "",
    @SerializedName ("Precio_Venta")val Precio_Venta: String = "",
    @SerializedName ("Stock_Minimo")val Stock_Minimo: String = "",
    @SerializedName ("ID_Categoria")val ID_Categoria: String = "",
    @SerializedName ("ID_Estado")val ID_Estado: String = "",
    @SerializedName ("ID_Gama")val ID_Gama: String = "",
    @SerializedName ("Fotos")val Fotos: String = ""
)