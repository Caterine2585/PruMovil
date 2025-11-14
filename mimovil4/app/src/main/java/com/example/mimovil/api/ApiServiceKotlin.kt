package com.example.mimovil.api

import com.example.mimovil.model.Cliente
import com.example.mimovil.model.Compras
import com.example.mimovil.model.DetalleCompras
import com.example.mimovil.model.Detalle_Ventas
import com.example.mimovil.model.Empleado
import com.example.mimovil.model.Producto
import com.example.mimovil.model.Ventas
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
    fun getCompras(): Call<List<String>>

    @Headers("Content-Type: application/json")
    @POST("ComprasR")
    fun crearCompra(@Body compra: Compras): Call<ResponseBody>

    @PUT("Compras/{ID_Entrada}")
    fun actualizarCompra(
        @Path("ID_Entrada") ID_Entrada: String,
        @Body compra: Compras
    ): Call<ResponseBody>

    @DELETE("ComprasE/{ID_Entrada}")
    fun eliminarCompra(@Path("ID_Entrada") ID_Entrada: String): Call<ResponseBody>

    // ============================
    // DETALLE COMPRAS
    // ============================
    @GET("DetalleC")
    fun getDetalleCompras(): Call<List<String>>

    @Headers("Content-Type: application/json")
    @POST("AgregarDetalleC")
    fun crearDetalleCompra(@Body detalle: DetalleCompras): Call<ResponseBody>

    @PUT("ActualizarDetalleC/{ID_Entrada}/{ID_Proveedor}")
    fun actualizarDetalleCompra(
        @Path("ID_Entrada") idEntrada: String,
        @Path("ID_Proveedor") idProveedor: String,
        @Body detalle: DetalleCompras
    ): Call<ResponseBody>

    @DELETE("EliminarDetC/{ID_Entrada}/{ID_Proveedor}")
    fun eliminarDetalleCompra(
        @Path("ID_Entrada") idEntrada: String,
        @Path("ID_Proveedor") idProveedor: String
    ): Call<ResponseBody>


    // ============================
    // Ventas
    // ============================

    @GET("/Ventas")
    fun getVentas(): Call<List<String>>

    @Headers("Content-Type: application/json")
    @POST("VentaRegistro")
    fun crearVenta(@Body venta: Ventas): Call<ResponseBody>

    @PUT("VentaActualizar/{ID_Venta}")
    fun actualizarVenta(
        @Path("ID_Venta") documento: String,
        @Body venta: Ventas
    ): Call<ResponseBody>

    @DELETE("VentaEliminar/{ID_Venta}")
    fun eliminarVenta(@Path("ID_Venta") documento: String): Call<ResponseBody>

    // ============================
    // Detalle Ventas
    // ============================


    @GET("/DetalleVentas")
    fun getDetalleVentas(): Call<List<String>>

    @Headers("Content-Type: application/json")
    @POST("DetalleVentasRegistro")
    fun crearDetalleVenta(@Body detalle: Detalle_Ventas): Call<ResponseBody>

    @PUT("DetalleVentasActualizar/{ID_Producto}/{ID_Venta}")
    fun actualizarDetalleVenta(
        @Path("ID_Producto") idProducto: String,
        @Path("ID_Venta") idVenta: String,
        @Body detalle: Detalle_Ventas
    ): Call<ResponseBody>

    @DELETE("DetalleVentasEliminar/{ID_Producto}/{ID_Venta}")
    fun eliminarDetalleVenta(
        @Path("ID_Producto") idProducto: String,
        @Path("ID_Venta") idVenta: String
    ): Call<ResponseBody>

}
