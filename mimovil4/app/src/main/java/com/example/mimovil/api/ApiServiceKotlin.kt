package com.example.mimovil.api

import com.example.mimovil.model.Cliente
import com.example.mimovil.model.Compras
import com.example.mimovil.model.Empleado
import com.example.mimovil.model.Producto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiServiceKotlin {

    // ============================
    // CLIENTES
    // ============================
    @GET("/Detalles")
    fun getClientes(): Call<List<String>>

    @Headers("Content-Type: application/json")
    @POST("RegistraC")
    fun crearCliente(@Body persona: Cliente): Call<ResponseBody>

    @PUT("ActualizarC/{documento}")
    fun actualizarCliente(
        @Path("documento") documento: String,
        @Body cliente: Cliente
    ): Call<ResponseBody>

    @DELETE("EliminarC/{documento}")
    fun eliminarCliente(@Path("documento") documento: String): Call<ResponseBody>

    // ============================
    // EMPLEADOS
    // ============================
    @GET("/Empleados")
    fun getEmpleados(): Call<List<String>>

    @Headers("Content-Type: application/json")
    @POST("EmpleadoRegistro")
    fun crearEmpleado(@Body empleado: Empleado): Call<ResponseBody>

    @PUT("/EmpleadoActualizar/{Documento_Empleado}")
    fun actualizarEmpleado(
        @Path("Documento_Empleado") documento: String,
        @Body empleado: Empleado
    ): Call<ResponseBody>

    @DELETE("/EmpleadoEliminar/{Documento_Empleado}")
    fun eliminarEmpleado(
        @Path("Documento_Empleado") documento: String
    ): Call<ResponseBody>

    // ============================
    // PRODUCTOS
    // ============================
    @GET("Productos")
    fun getProducto(): Call<List<String>>

    @Headers("Content-Type: application/json")
    @POST("crearProducto")
    fun crearProducto(@Body producto: Producto): Call<ResponseBody>

    // ============================
    // COMPRAS
    // ============================
    @GET("Compras")
    fun getCompras(): Call<List<Compras>>

    @Headers("Content-Type: application/json")
    @POST("ComprasR")
    fun crearCompra(@Body compra: Compras): Call<ResponseBody>
}
