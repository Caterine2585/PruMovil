package com.example.mimovil.api

import com.example.mimovil.model.Cliente
import com.example.mimovil.model.Compras
import com.example.mimovil.model.Empleado
import com.example.mimovil.model.Producto
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiServiceKotlin {

    @GET("/Detalles")
    fun getClientes(): Call<List<String>>

    // POST Clientes (mismo estilo que usas)
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

    // POST Empleados
    @Headers("Content-Type: application/json")
    @POST("EmpleadoRegistro") // cámbialo si tu endpoint real es otro
    fun crearEmpleado(@Body empleado: Empleado): Call<ResponseBody>

    //Productos
    @GET("Productos")
    fun getProducto(): Call<List<String>>
    @Headers("Content-Type: application/json")
    @POST("crearProducto")
    fun crearProducto(@Body producto: Producto): Call<ResponseBody>
    @PUT("ActualizaProd/{ID_Producto}")
    fun ActualizarProducto(
        @Path("ID_Producto") ID_Producto: String,
        @Body producto: Producto
    ): Call<ResponseBody>

    // Compras
    @GET("Compras")
    fun getCompras(): Call<List<Compras>>

    @Headers("Content-Type: application/json")
    @POST("ComprasR")
    fun crearCompra(@Body compra: Compras): Call<ResponseBody>
 
 @GET("/Empleados")
    fun getEmpleados(): Call<List<String>>
}
