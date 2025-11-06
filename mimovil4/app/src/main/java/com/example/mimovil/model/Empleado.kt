package com.example.mimovil.model

import com.google.gson.annotations.SerializedName

data class Empleado(
    @SerializedName("Documento_Empleado") var documento: String = "",
    @SerializedName("Tipo_Documento")    var tipoDocumento: String = "",
    @SerializedName("Nombre_Usuario")    var nombre: String = "",
    @SerializedName("Apellido_Usuario")  var apellido: String = "",
    @SerializedName("Edad")              var edad: String = "",               // si tu API lo quiere int, conviértelo antes
    @SerializedName("Correo_Electronico")var correo: String = "",
    @SerializedName("Telefono")          var telefono: String = "",
    @SerializedName("Genero")            var genero: String = "",
    @SerializedName("ID_Estado")         var idEstado: String = "EST001",
    @SerializedName("ID_Rol")            var idRol: String = "ROL002",
    @SerializedName("Fotos")             var fotos: String = ""               // opcional
)
