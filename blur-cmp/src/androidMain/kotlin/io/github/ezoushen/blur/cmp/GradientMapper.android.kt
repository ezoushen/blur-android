package io.github.ezoushen.blur.cmp

import androidx.compose.ui.geometry.Offset
import com.example.blur.BlurConfig
import com.example.blur.BlurGradient

internal object AndroidGradientMapper {

    fun toBlurConfig(config: BlurOverlayConfig): BlurConfig {
        // Extract ARGB int from the packed Long, or null if no tint
        val overlayArgb: Int? = if (config.tintColorValue != 0L) {
            config.tintColorValue.toInt()
        } else {
            null
        }
        return BlurConfig(
            radius = config.radius,
            overlayColor = overlayArgb,
            downsampleFactor = config.downsampleFactor,
        )
    }

    fun toBlurGradient(gradient: BlurGradientType, radius: Float): BlurGradient = when (gradient) {
        is BlurGradientType.Linear -> {
            val start = Offset(gradient.startX, gradient.startY)
            val end = Offset(gradient.endX, gradient.endY)
            if (gradient.stops != null) {
                BlurGradient.linearGradient(
                    radiusStops = gradient.stops.map { it.position to (it.intensity * radius) }.toTypedArray(),
                    start = start,
                    end = end,
                )
            } else {
                BlurGradient.linearGradient(
                    startRadius = gradient.startIntensity * radius,
                    endRadius = gradient.endIntensity * radius,
                    start = start,
                    end = end,
                )
            }
        }
        is BlurGradientType.Radial -> {
            val center = Offset(gradient.centerX, gradient.centerY)
            if (gradient.stops != null) {
                BlurGradient.radialGradient(
                    radiusStops = gradient.stops.map { it.position to (it.intensity * radius) }.toTypedArray(),
                    center = center,
                    radius = gradient.radius,
                )
            } else {
                BlurGradient.radialGradient(
                    centerRadius = gradient.centerIntensity * radius,
                    edgeRadius = gradient.edgeIntensity * radius,
                    center = center,
                    radius = gradient.radius,
                )
            }
        }
    }
}
