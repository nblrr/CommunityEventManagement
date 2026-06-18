package com.example.communityeventmanagementsystem.core.network

import com.example.communityeventmanagementsystem.core.common.NetworkResult
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException

object ErrorHandler {
    fun <T> handleException(e: Exception): NetworkResult<T> {
        return when (e) {
            is SocketTimeoutException -> {
                NetworkResult.Error("Gagal terhubung ke server", 408)
            }
            is IOException -> {
                NetworkResult.Error("Gagal terhubung ke server", 0)
            }
            is HttpException -> {
                val errorBody = e.response()?.errorBody()?.string()
                val serverMessage = try {
                    val json = Gson().fromJson(errorBody, JsonObject::class.java)
                    json.get("message")?.asString
                } catch (ex: Exception) {
                    null
                }

                var message = serverMessage ?: when (e.code()) {
                    401 -> "Sesi telah berakhir. Silakan login kembali."
                    403 -> "Anda tidak memiliki izin untuk melakukan tindakan ini."
                    404 -> "Data yang diminta tidak ditemukan."
                    422 -> "Data yang dimasukkan belum valid"
                    429 -> "Terlalu banyak permintaan. Silakan tunggu sebentar."
                    500 -> "Kesalahan server. Silakan coba lagi nanti."
                    else -> "Terjadi kesalahan tak terduga. Silakan coba lagi."
                }

                if (message.contains("Exception", ignoreCase = true) ||
                    message.contains("SQLSTATE", ignoreCase = true) ||
                    message.contains("PDOException", ignoreCase = true) ||
                    message.contains("database", ignoreCase = true) ||
                    message.contains("query", ignoreCase = true)) {
                    message = "Terjadi kesalahan internal pada server. Silakan coba lagi nanti."
                }

                if (e.code() == 401 && (serverMessage?.contains("password", ignoreCase = true) == true || 
                            serverMessage?.contains("email", ignoreCase = true) == true || 
                            serverMessage?.contains("salah", ignoreCase = true) == true)) {
                    message = "Email atau password salah"
                }

                if (message.contains("Event penuh", ignoreCase = true) || message.contains("Kuota", ignoreCase = true)) {
                    message = "Kuota peserta telah penuh"
                }

                NetworkResult.Error(message, e.code())
            }
            else -> {
                NetworkResult.Error("Terjadi kesalahan yang tidak diketahui. Silakan coba lagi.", null)
            }
        }
    }
}
