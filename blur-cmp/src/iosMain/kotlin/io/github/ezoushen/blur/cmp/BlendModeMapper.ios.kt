package io.github.ezoushen.blur.cmp

internal object IosBlendModeMapper {
    // UIVisualEffectView doesn't support custom blend modes directly.
    // This is a placeholder for when we integrate the Swift framework with CABackdropLayer.
    // For now, blend modes are only applied to the tint overlay layer.

    fun isSupported(mode: BlurBlendMode): Boolean = when (mode) {
        BlurBlendMode.Normal -> true
        else -> false // Other modes require the Swift framework with CALayer compositingFilter
    }
}
