package com.example.mimovil.api

import com.example.mimovil.model.Cliente
import com.example.mimovil.model.Compras
import com.example.mimovil.model.DetalleCompras
import com.example.mimovil.model.DetalleDev
import com.example.mimovil.model.Detalle_Ventas
import com.example.mimovil.model.Devoluciones
import com.example.mimovil.model.LoginRequest
import com.example.mimovil.model.LoginResponse
import com.example.mimovil.model.Producto
import com.example.mimovil.model.Proveedor
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
    fun crearEmpleado(@Body empleado: com.example.mimovil.model.EmpleadoRegistroRequest): Call<ResponseBody>

    @PUT("EmpleadoActualizar/{Documento_Empleado}")
    fun actualizarEmpleado(
        @Path("Documento_Empleado") documento: String,
        @Body empleado: com.example.mimovil.model.EmpleadoActualizarRequest
    ): Call<ResponseBody>

    @DELETE("/EmpleadoEliminar/{Documento_Empleado}")
    fun eliminarEmpleado(
        @Path("Documento_Empleado") documento: String
    ): Call<ResponseBody>


    // ============================
    // PRODUCTOS (PROTEGIDOS CON JWT)
    // ============================
    @GET("Productos")
    fun getProducto(
        @Header("Authorization") authHeader: String
    ): Call<List<String>>

    @Headers("Content-Type: application/json")
    @POST("RegistroP")
    fun crearProducto(
        @Header("Authorization") authHeader: String,
        @Body producto: Producto
    ): Call<ResponseBody>

    // 🔹 ACTUALIZAR PRODUCTO (PUT) JSON
    @Headers("Content-Type: application/json")
    @PUT("ActualizaProd/{id}")
    fun actualizarProducto(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String,
        @Body producto: Producto
    ): Call<ResponseBody>

    // 🔹 ELIMINAR PRODUCTO (DELETE)
    @DELETE("EliminarPro/{id}")
    fun eliminarProducto(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String
    ): Call<ResponseBody>

    // ============================
    // ✅ PRODUCTOS MULTIPART (IMAGEN)
    // ============================

    @Multipart
    @POST("RegistroPMultipart")
    fun crearProductoMultipart(
        @Header("Authorization") authHeader: String,
        @Part("data") data: RequestBody,
        @Part file: MultipartBody.Part?
    ): Call<ResponseBody>

    @Multipart
    @PUT("ActualizaProdMultipart/{id}")
    fun actualizarProductoMultipart(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String,
        @Part("data") data: RequestBody,
        @Part file: MultipartBody.Part?
    ): Call<ResponseBody>


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
    // VENTAS
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
    // DETALLE VENTAS
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


    // ============================
    // DEVOLUCIONES
    // ============================
    @GET("Devoluciones")
    fun getDevolucion(): Call<List<String>>

    @Headers("Content-Type: application/json")
    @POST("AgregarDevolucion")
    fun crearDevolucion(@Body dev: Devoluciones): Call<ResponseBody>

    @PUT("ActualizarD/{ID_Devolucion}")
    fun actualizarDevolucion(
        @Path("ID_Devolucion") ID_Devolucion: String,
        @Body dev: Devoluciones
    ): Call<ResponseBody>

    @DELETE("EliminarDev/{ID_Devolucion}")
    fun eliminarDevolucion(@Path("ID_Devolucion") ID_Devolucion: String): Call<ResponseBody>


    @GET("DetalleD")
    fun getDetalleDev(): Call<List<String>>

    @Headers("Content-Type: application/json")
    @POST("AgregarDetalleD")
    fun crearDevo(@Body detalledev: DetalleDev): Call<ResponseBody>

    @PUT("ActualizarDetalleD/{ID_Devolucion}/{ID_Venta}")
    fun actualizarDetalleDev(
        @Path("ID_Devolucion") ID_Devolucion: String,
        @Path("ID_Venta") ID_Venta: String,
        @Body detalledev: DetalleDev
    ): Call<ResponseBody>

    @DELETE("EliminarDetD/{ID_Devolucion}/{ID_Venta}")
    fun eliminarDetalleDev(
        @Path("ID_Devolucion") ID_Devolucion: String,
        @Path("ID_Venta") ID_Venta: String
    ): Call<ResponseBody>


    // ============================
    // PROVEEDORES
    // ============================
    @GET("/Proveedor")
    fun getProveedores(): Call<List<String>>

    @Headers("Content-Type: application/json")
    @POST("/ProveeReg")
    fun crearProveedor(@Body proveedor: Proveedor): Call<ResponseBody>

    @PUT("/ActualizaProv/{ID_Proveedor}")
    fun actualizarProveedor(
        @Path("ID_Proveedor") idProveedor: String,
        @Body proveedor: Proveedor
    ): Call<ResponseBody>

    @DELETE("/EliminarProve/{ID_Proveedor}")
    fun eliminarProveedor(@Path("ID_Proveedor") idProveedor: String): Call<ResponseBody>


    // ============================
    // LOGIN (JWT)
    // ============================
    @Headers("Content-Type: application/json")
    @POST("/auth/login")
    fun loginEmpleado(@Body loginRequest: LoginRequest): Call<LoginResponse>
}
