package com.example.mimovil.model

import com.google.gson.annotations.SerializedName

class Devoluciones( @SerializedName("ID_Devolucion") var IDDevolucion: String = "",
                    @SerializedName ("Fecha_Devolucion") var FechaDevolucion: String = "",
                    @SerializedName ("Motivo") var Motivo: String = "",)