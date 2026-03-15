package io.github.ezoushen.blur.cmp.demo

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

/**
 * Creates a UIViewController hosting the cross-platform blur demo.
 * Call from Swift/ObjC: `let vc = IosDemoKt.createBlurDemoViewController()`
 */
fun createBlurDemoViewController(): UIViewController {
    return ComposeUIViewController {
        BlurCmpDemoScreen()
    }
}
