package com.bluelay.damda

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkInfo
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class NetworkManager(var context: Context?) {
    private var clientId: String? = null
    private var clientSecret: String? = null

    fun setClientId(clientId: String?) { this.clientId = clientId }
    fun setClientSecret(clientSecret: String?) { this.clientSecret = clientSecret }

    fun downloadContents(address: String?): String? {
        var conn: HttpURLConnection? = null
        var stream: InputStream?
        var result: String? = null
        if (!isOnline()) return null
        try {
            val url = URL(address)
            conn = url.openConnection() as HttpURLConnection
            stream = getNetworkConnection(conn)
            result = readStreamToString(stream)
            stream?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            conn?.disconnect()
        }
        return result
    }

    fun downloadImage(address: String?): Bitmap? {
        var conn: HttpURLConnection? = null
        var stream: InputStream? = null
        var result: Bitmap? = null
        try {
            val url = URL(address)
            conn = url.openConnection() as HttpURLConnection
            stream = getNetworkConnection(conn)
            result = readStreamToBitmap(stream)
            stream?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            conn?.disconnect()
        }
        return result
    }

    private fun readStreamToBitmap(stream: InputStream?): Bitmap? {
        return BitmapFactory.decodeStream(stream)
    }

    private fun readStreamToString(stream: InputStream?): String? {
        val result = StringBuilder()
        try {
            val inputStreamReader = InputStreamReader(stream)
            val bufferedReader = BufferedReader(inputStreamReader)
            var readLine = bufferedReader.readLine()
            while (readLine != null) {
                result.append("$readLine\n")
                readLine = bufferedReader.readLine()
            }
            bufferedReader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return result.toString()
    }

    private fun isOnline(): Boolean {
        val connMgr = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo
        return networkInfo?.isConnected == true
    }

    private fun getNetworkConnection(conn: HttpURLConnection?): InputStream? {
        conn!!.readTimeout = 3000
        conn.connectTimeout = 3000
        conn.requestMethod = "GET"
        conn.doInput = true
        if (clientId != null && clientSecret != null) {
            conn.setRequestProperty("X-Naver-Client-Id", clientId)
            conn.setRequestProperty("X-Naver-Client-Secret", clientSecret)
        }
        if (conn.responseCode != HttpsURLConnection.HTTP_OK) {
            throw IOException("HTTP error code: " + conn.responseCode)
        }
        return conn.inputStream
    }
}