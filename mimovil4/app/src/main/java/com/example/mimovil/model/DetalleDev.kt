package com.example.mimovil.model

import com.google.gson.annotations.SerializedName

class DetalleDev( @SerializedName("ID_DetalleDev") var IDDetalleDev: String = "",
                  @SerializedName ("ID_Devolucion") var IDDevolucion: String = "",
                  @SerializedName ("ID_Venta") var IDVenta: String = "",
                  @SerializedName ("Cantidad_Devuelta") var CantidadDevuelta: String = "",)