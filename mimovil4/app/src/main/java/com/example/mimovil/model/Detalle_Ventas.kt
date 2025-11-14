package com.example.mimovil.model

import com.google.gson.annotations.SerializedName

class Detalle_Ventas(
    @SerializedName("Cantidad") var cantidad : Int = 0,
    @SerializedName("Fecha_Salida") var fecha_salida : String ="",
    @SerializedName ("ID_Producto") var id_producto  : String ="",
    @SerializedName ("ID_Venta") var id_venta  : String ="",
)



