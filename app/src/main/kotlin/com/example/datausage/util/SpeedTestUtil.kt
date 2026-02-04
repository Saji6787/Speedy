package com.example.datausage.util

import com.example.datausage.domain.model.SpeedTestResult
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class SpeedTestUtil {

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // 10.0.2.2 is the localhost of the host machine from the Android emulator
    private val BASE_URL = "http://10.0.2.2:8080"
    private val DOWNLOAD_URL = "$BASE_URL/download"
    private val UPLOAD_URL = "$BASE_URL/upload"

    fun performSpeedTest(): SpeedTestResult {
        val downloadMbps = testDownload()
        val uploadMbps = testUpload()
        return SpeedTestResult(downloadMbps, uploadMbps)
    }

    private fun testDownload(): Double {
        var start = System.currentTimeMillis()
        var bytesRead = 0L

        try {
            val request = Request.Builder().url(DOWNLOAD_URL).build()
            val response = client.newCall(request).execute()
            
            if (!response.isSuccessful) return 0.0
            
            val source = response.body?.source() ?: return 0.0
            start = System.currentTimeMillis()
            
            while (true) {
                // Read in chunks
                val read = source.read(okio.Buffer(), 8192)
                if (read == -1L) break
                bytesRead += read
            }
            
            val end = System.currentTimeMillis()
            val durationMs = end - start
            
            if (durationMs == 0L) return 0.0
            
            // Calculate speed
            val bits = bytesRead * 8.0
            val seconds = durationMs / 1000.0
            return (bits / 1_000_000.0) / seconds

        } catch (e: Exception) {
            e.printStackTrace()
            return 0.0
        }
    }

    private fun testUpload(): Double {
        try {
            // Generate 1MB of random data
            val data = ByteArray(1024 * 1024)
            Random.nextBytes(data)
            
            val requestBody = data.toRequestBody("application/octet-stream".toMediaType())
            val request = Request.Builder()
                .url(UPLOAD_URL)
                .post(requestBody)
                .build()

            val start = System.currentTimeMillis()
            val response = client.newCall(request).execute()
            val end = System.currentTimeMillis()
            
            if (!response.isSuccessful) return 0.0
            
            val durationMs = end - start
            if (durationMs == 0L) return 0.0

            val bits = data.size * 8.0
            val seconds = durationMs / 1000.0
            return (bits / 1_000_000.0) / seconds

        } catch (e: Exception) {
            e.printStackTrace()
            return 0.0
        }
    }
}
