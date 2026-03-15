package io.github.ezoushen.blur.cmp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import platform.CoreGraphics.CGRectZero
import platform.UIKit.UIApplication
import platform.UIKit.UIBlurEffect
import platform.UIKit.UIBlurEffectStyle
import platform.UIKit.UIColor
import platform.UIKit.UIView
import platform.UIKit.UIViewAutoresizingFlexibleHeight
import platform.UIKit.UIViewAutoresizingFlexibleWidth
import platform.UIKit.UIVisualEffectView
import platform.UIKit.UIWindow

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun BlurOverlayHost(
    state: BlurOverlayState,
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    val currentState by rememberUpdatedState(state)

    DisposableEffect(Unit) {
        val window = UIApplication.sharedApplication.keyWindow ?: run {
            return@DisposableEffect onDispose { }
        }

        val blurView = createIosBlurView(currentState.config)
        blurView.tag = BLUR_VIEW_TAG
        blurView.setFrame(window.bounds)
        blurView.autoresizingMask =
            UIViewAutoresizingFlexibleWidth or UIViewAutoresizingFlexibleHeight

        window.insertSubview(blurView, atIndex = 0)

        onDispose {
            blurView.removeFromSuperview()
        }
    }

    // React to config changes
    LaunchedEffect(Unit) {
        snapshotFlow { currentState.config }
            .distinctUntilChanged()
            .collectLatest { config ->
                val window = UIApplication.sharedApplication.keyWindow ?: return@collectLatest
                val blurView = findIosBlurView(window) ?: return@collectLatest
                updateIosBlurView(window, blurView, config)
            }
    }

    // React to enabled state
    LaunchedEffect(Unit) {
        snapshotFlow { currentState.isEnabled }
            .distinctUntilChanged()
            .collectLatest { enabled ->
                val window = UIApplication.sharedApplication.keyWindow ?: return@collectLatest
                val blurView = findIosBlurView(window) ?: return@collectLatest
                blurView.setHidden(!enabled)
            }
    }

    content()
}

private const val BLUR_VIEW_TAG: Long = 0x426C7572L

@OptIn(ExperimentalForeignApi::class)
private fun createIosBlurView(config: BlurOverlayConfig): UIView {
    // Map blur radius to the nearest system material style.
    val blurStyle: UIBlurEffectStyle = when {
        config.radius <= 5f -> UIBlurEffectStyle.UIBlurEffectStyleSystemThinMaterial
        config.radius <= 15f -> UIBlurEffectStyle.UIBlurEffectStyleSystemMaterial
        config.radius <= 30f -> UIBlurEffectStyle.UIBlurEffectStyleSystemThickMaterial
        else -> UIBlurEffectStyle.UIBlurEffectStyleSystemUltraThinMaterial
    }

    val blurEffect = UIBlurEffect.effectWithStyle(blurStyle)
    val effectView = UIVisualEffectView(effect = blurEffect)

    // Add tint overlay inside contentView if a tint colour is requested.
    if (config.tintColorValue != 0L) {
        val argb = config.tintColorValue.toInt()
        val alpha = ((argb ushr 24) and 0xFF) / 255.0
        val red = ((argb ushr 16) and 0xFF) / 255.0
        val green = ((argb ushr 8) and 0xFF) / 255.0
        val blue = (argb and 0xFF) / 255.0

        val tintView = UIView(frame = CGRectZero.readValue())
        tintView.backgroundColor = UIColor(
            red = red,
            green = green,
            blue = blue,
            alpha = alpha,
        )
        tintView.autoresizingMask =
            UIViewAutoresizingFlexibleWidth or UIViewAutoresizingFlexibleHeight

        effectView.contentView.addSubview(tintView)
    }

    return effectView
}

private fun findIosBlurView(window: UIWindow): UIView? {
    val subviews = window.subviews
    for (i in 0 until subviews.count().toInt()) {
        @Suppress("UNCHECKED_CAST")
        val subview = subviews[i] as? UIView ?: continue
        if (subview.tag == BLUR_VIEW_TAG) return subview
    }
    return null
}

@OptIn(ExperimentalForeignApi::class)
private fun updateIosBlurView(window: UIWindow, existing: UIView, config: BlurOverlayConfig) {
    // UIVisualEffectView doesn't expose its effect style for mutation after init,
    // so we remove the old view and insert a freshly configured one.
    existing.removeFromSuperview()
    val newView = createIosBlurView(config)
    newView.tag = BLUR_VIEW_TAG
    newView.setFrame(window.bounds)
    newView.autoresizingMask =
        UIViewAutoresizingFlexibleWidth or UIViewAutoresizingFlexibleHeight
    window.insertSubview(newView, atIndex = 0)
}
