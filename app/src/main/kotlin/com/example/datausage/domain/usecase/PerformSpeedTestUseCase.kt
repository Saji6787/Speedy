package com.example.datausage.domain.usecase

import com.example.datausage.domain.model.SpeedTestResult
import com.example.datausage.util.SpeedTestUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PerformSpeedTestUseCase {
    private val speedTestUtil = SpeedTestUtil()

    suspend operator fun invoke(): SpeedTestResult = withContext(Dispatchers.IO) {
        speedTestUtil.performSpeedTest()
    }
}
