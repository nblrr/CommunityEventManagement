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
                NetworkResult.Error("Permintaan waktu habis. Silakan coba lagi.", 408)
            }
            is IOException -> {
                NetworkResult.Error("Tidak ada koneksi internet. Periksa jaringan Anda.", 0)
            }
            is HttpException -> {
                val errorBody = e.response()?.errorBody()?.string()
                val serverMessage = try {
                    val json = Gson().fromJson(errorBody, JsonObject::class.java)
                    json.get("message")?.asString
                } catch (ex: Exception) {
                    null
                }

                val message = serverMessage ?: when (e.code()) {
                    401 -> "Sesi telah berakhir. Silakan login kembali."
                    403 -> "Anda tidak memiliki izin untuk melakukan tindakan ini."
                    404 -> "Data yang diminta tidak ditemukan."
                    422 -> "Input tidak valid. Periksa kembali detail Anda."
                    429 -> "Terlalu banyak permintaan. Silakan tunggu sebentar."
                    500 -> "Kesalahan server. Silakan coba lagi nanti."
                    else -> "Terjadi kesalahan tak terduga. Silakan coba lagi."
                }
                NetworkResult.Error(message, e.code())
            }
            else -> {
                NetworkResult.Error("Terjadi kesalahan yang tidak diketahui. Silakan coba lagi.", null)
            }
        }
    }
}
