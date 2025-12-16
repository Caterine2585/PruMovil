package com.example.mimovil.model

import com.google.gson.annotations.SerializedName

data class EmpleadoActualizarRequest(
    @SerializedName("Tipo_Documento")     var tipoDocumento: String? = null,
    @SerializedName("Nombre_Usuario")     var nombre: String? = null,
    @SerializedName("Apellido_Usuario")   var apellido: String? = null,
    @SerializedName("Edad")               var edad: String? = null,
    @SerializedName("Correo_Electronico") var correo: String? = null,
    @SerializedName("Telefono")           var telefono: String? = null,
    @SerializedName("Genero")             var genero: String? = null,
    @SerializedName("ID_Estado")          var idEstado: String? = null,
    @SerializedName("ID_Rol")             var idRol: String? = null,
    @SerializedName("Fotos")              var fotos: String? = null,


    @SerializedName("Contrasena")         var contrasena: String? = null
)
