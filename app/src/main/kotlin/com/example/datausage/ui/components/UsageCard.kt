package com.example.datausage.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.example.datausage.domain.model.DailyUsage
import com.example.datausage.util.formatBytes

@Composable
fun UsageCard(
    dailyUsage: DailyUsage,
    thresholdMb: Long,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Today's Usage",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = dailyUsage.byteUsed.formatBytes(),
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            val progress by animateFloatAsState(
                targetValue = if (thresholdMb > 0) dailyUsage.byteUsed.toFloat() / (thresholdMb * 1024 * 1024) else 0f,
                label = "progress"
            )
            val cleanProgress = progress.coerceIn(0f, 1f)
            
            LinearProgressIndicator(
                progress = cleanProgress,
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = if (cleanProgress > 0.9f) Color.Red else MaterialTheme.colorScheme.primary,
                strokeCap = StrokeCap.Round
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "${(cleanProgress * 100).toInt()}% of daily limit (${thresholdMb} MB)",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
