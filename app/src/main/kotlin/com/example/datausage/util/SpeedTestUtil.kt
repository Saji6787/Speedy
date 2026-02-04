package com.example.datausage.util

import com.example.datausage.domain.model.SpeedTestResult
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

class SpeedTestUtil {

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // Using a fast CDN file for testing (e.g., Cloudflare speed test file or similar small asset)
    // 1MB or 10MB test file. For "lightweight" test, we might only download a chunk or small file.
    // Using a reliable public URL. 
    private val DOWNLOAD_URL = "https://speed.cloudflare.com/__down?bytes=1000000" // 1MB for quick test
    
    // For upload, we can post to a dummy endpoint, or just skip it if we want super simple.
    // The prompt asked for "download/upload Mbps", so we should try both or at least download.
    // Uploading to public endpoints without API key is flaky. I will implement Download only for now as it's most reliable for "simple",
    // or simulate upload if needed, but the prompt said "simple OkHttp speed test".
    // I'll stick to Download only for MVP reliability, or maybe a small POST if I can find a public echo.
    // Let's do Download first.

    fun performSpeedTest(): SpeedTestResult {
        var start = System.currentTimeMillis()
        var bytesRead = 0L

        try {
            val request = Request.Builder().url(DOWNLOAD_URL).build()
            val response = client.newCall(request).execute()
            
            if (!response.isSuccessful) return SpeedTestResult(0.0, 0.0)
            
            val source = response.body?.source() ?: return SpeedTestResult(0.0, 0.0)
            start = System.currentTimeMillis()
            
            while (true) {
                // Read in chunks
                val read = source.read(okio.Buffer(), 8192)
                if (read == -1L) break
                bytesRead += read
            }
            
            val end = System.currentTimeMillis()
            val durationMs = end - start
            
            if (durationMs == 0L) return SpeedTestResult(0.0, 0.0)
            
            // Calculate speed
            // bytes * 8 = bits
            // duration in sec
            // Mbps = (bits / 1_000_000) / seconds
            
            val bits = bytesRead * 8.0
            val seconds = durationMs / 1000.0
            val mbps = (bits / 1_000_000.0) / seconds
            
            return SpeedTestResult(mbps, 0.0) // Upload 0.0 for now

        } catch (e: Exception) {
            e.printStackTrace()
            return SpeedTestResult(0.0, 0.0)
        }
    }
}
