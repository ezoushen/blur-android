package com.example.blur.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.blur.BlurGradient
import com.example.blur.compose.BlurSurface
import com.example.blur.compose.VariableBlurSurface
import com.example.blur.sample.ui.theme.BlurSampleTheme
import io.github.ezoushen.blur.cmp.BlurBlendMode
import io.github.ezoushen.blur.cmp.BlurOverlayConfig
import io.github.ezoushen.blur.cmp.BlurOverlayHost
import io.github.ezoushen.blur.cmp.demo.BlurCmpDemoScreen
import io.github.ezoushen.blur.cmp.rememberBlurOverlayState
import io.github.ezoushen.blur.cmp.withTint

/**
 * Main activity demonstrating the Compose blur library.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BlurSampleTheme {
                BlurSampleScreen()
            }
        }
    }
}

@Composable
fun BlurSampleScreen() {
    // Top-level mode: 0 = blur-core demo, 1 = blur-cmp overlay demo, 2 = CMP Demo (shared)
    var demoMode by remember { mutableIntStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedBackground(modifier = Modifier.fillMaxSize())

        when (demoMode) {
            0 -> BlurCoreDemoContent(
                demoMode = demoMode,
                onDemoModeChange = { demoMode = it },
            )
            1 -> BlurCmpDemoContent(
                demoMode = demoMode,
                onDemoModeChange = { demoMode = it },
            )
            2 -> BlurCmpDemoScreen()
        }

        // Floating mode selector when in CMP Demo mode (shared screen has its own tabs)
        if (demoMode == 2) {
            DemoModeSelector(
                demoMode = demoMode,
                onDemoModeChange = { demoMode = it },
            )
        }
    }
}

// ---------------------------------------------------------------------------
// blur-core demo (uniform + variable blur surfaces)
// ---------------------------------------------------------------------------

@Composable
private fun BlurCoreDemoContent(
    demoMode: Int,
    onDemoModeChange: (Int) -> Unit,
) {
    // Blur mode: 0 = Uniform, 1 = Variable
    var blurMode by remember { mutableIntStateOf(0) }

    // Uniform blur properties
    var uniformRadius by remember { mutableFloatStateOf(16f) }

    // Variable blur properties
    var gradientType by remember { mutableIntStateOf(0) } // 0=Linear, 1=Radial
    var startRadius by remember { mutableFloatStateOf(0f) }
    var endRadius by remember { mutableFloatStateOf(30f) }

    // Shared properties
    var downsampleFactor by remember { mutableFloatStateOf(4f) }
    var isLive by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Demo mode selector at the top
        DemoModeSelector(
            demoMode = demoMode,
            onDemoModeChange = onDemoModeChange,
        )

        Spacer(modifier = Modifier.weight(1f))

        // Blur surface based on mode
        if (blurMode == 0) {
            BlurSurface(
                modifier = Modifier
                    .width(300.dp)
                    .height(200.dp)
                    .shadow(elevation = 18.dp, shape = RoundedCornerShape(16.dp))
                    .align(alignment = Alignment.CenterHorizontally),
                radius = uniformRadius,
                downsampleFactor = downsampleFactor,
                isLive = isLive
            ) {
                Text(
                    text = "Uniform Blur",
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(alignment = Alignment.Center)
                )
            }
        } else {
            val gradient = remember(gradientType, startRadius, endRadius) {
                when (gradientType) {
                    0 -> BlurGradient.verticalGradient(startRadius, endRadius)
                    else -> BlurGradient.radialGradient(
                        centerRadius = startRadius,
                        edgeRadius = endRadius,
                        radius = 0.5f,
                    )
                }
            }

            VariableBlurSurface(
                modifier = Modifier
                    .width(300.dp)
                    .height(300.dp)
                    .shadow(elevation = 18.dp, shape = RoundedCornerShape(16.dp))
                    .align(alignment = Alignment.CenterHorizontally),
                gradient = gradient,
                overlayColor = Color.White.copy(alpha = 0.7f),
                downsampleFactor = downsampleFactor,
                isLive = isLive
            ) {
                val gradientName = when (gradientType) {
                    0 -> "Vertical"
                    else -> "Radial"
                }
                Text(
                    text = "$gradientName Gradient",
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(alignment = Alignment.Center)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Control panel at bottom
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.25f))
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .padding(bottom = 12.dp)
        ) {
            Text(
                text = "Blur Controls",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ModeChip(text = "Uniform", selected = blurMode == 0, onClick = { blurMode = 0 })
                ModeChip(text = "Variable", selected = blurMode == 1, onClick = { blurMode = 1 })
            }

            if (blurMode == 0) {
                Text(
                    text = "Radius: ${String.format("%.1f", uniformRadius)}",
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Slider(
                    value = uniformRadius,
                    onValueChange = { uniformRadius = it },
                    valueRange = 0f..100f,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    colors = sliderColors()
                )
            } else {
                Text(
                    text = "Gradient Type",
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ModeChip(text = "Linear", selected = gradientType == 0, onClick = { gradientType = 0 })
                    ModeChip(text = "Radial", selected = gradientType == 1, onClick = { gradientType = 1 })
                }

                val startLabel = if (gradientType == 1) "Center" else "Start"
                Text(
                    text = "$startLabel Radius: ${String.format("%.1f", startRadius)}",
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 12.dp)
                )
                Slider(
                    value = startRadius,
                    onValueChange = { startRadius = it },
                    valueRange = 0f..50f,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    colors = sliderColors()
                )

                val endLabel = if (gradientType == 1) "Edge" else "End"
                Text(
                    text = "$endLabel Radius: ${String.format("%.1f", endRadius)}",
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Slider(
                    value = endRadius,
                    onValueChange = { endRadius = it },
                    valueRange = 0f..50f,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    colors = sliderColors()
                )
            }

            Text(
                text = "Downsample: ${downsampleFactor.toInt()}x",
                color = Color.White,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 12.dp)
            )
            Slider(
                value = downsampleFactor,
                onValueChange = { downsampleFactor = it },
                valueRange = 1f..16f,
                steps = 14,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                colors = sliderColors()
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Live Updates", color = Color.White, fontSize = 14.sp)
                Switch(
                    checked = isLive,
                    onCheckedChange = { isLive = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color.White.copy(alpha = 0.5f),
                        uncheckedThumbColor = Color.White.copy(alpha = 0.7f),
                        uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                    )
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// blur-cmp demo (BlurOverlayHost fullscreen overlay)
// ---------------------------------------------------------------------------

@Composable
private fun BlurCmpDemoContent(
    demoMode: Int,
    onDemoModeChange: (Int) -> Unit,
) {
    var radius by remember { mutableFloatStateOf(20f) }
    var isEnabled by remember { mutableStateOf(true) }

    val blurState = rememberBlurOverlayState(
        initialConfig = BlurOverlayConfig(
            radius = radius,
            tintBlendMode = BlurBlendMode.Normal,
        ).withTint(Color.White.copy(alpha = 0.3f))
    )

    blurState.isEnabled = isEnabled
    blurState.setRadius(radius)

    BlurOverlayHost(state = blurState) {
        Column(modifier = Modifier.fillMaxSize()) {
            DemoModeSelector(
                demoMode = demoMode,
                onDemoModeChange = onDemoModeChange,
            )

            Spacer(modifier = Modifier.weight(1f))

            // Label in the center to show CMP overlay is active
            Text(
                text = if (isEnabled) "CMP Overlay Active" else "CMP Overlay Disabled",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Controls at bottom
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.25f))
                    .padding(horizontal = 24.dp, vertical = 20.dp)
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = "CMP Overlay Controls",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Radius: ${String.format("%.1f", radius)}",
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Slider(
                    value = radius,
                    onValueChange = { radius = it },
                    valueRange = 0f..60f,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    colors = sliderColors()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Overlay Enabled", color = Color.White, fontSize = 14.sp)
                    Switch(
                        checked = isEnabled,
                        onCheckedChange = { isEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color.White.copy(alpha = 0.5f),
                            uncheckedThumbColor = Color.White.copy(alpha = 0.7f),
                            uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                        )
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Shared demo mode selector
// ---------------------------------------------------------------------------

@Composable
private fun DemoModeSelector(
    demoMode: Int,
    onDemoModeChange: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.3f))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Mode:",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        ModeChip(
            text = "blur-core",
            selected = demoMode == 0,
            onClick = { onDemoModeChange(0) }
        )
        ModeChip(
            text = "CMP",
            selected = demoMode == 1,
            onClick = { onDemoModeChange(1) }
        )
        ModeChip(
            text = "CMP Demo",
            selected = demoMode == 2,
            onClick = { onDemoModeChange(2) }
        )
    }
}

// ---------------------------------------------------------------------------
// Shared composables
// ---------------------------------------------------------------------------

@Composable
private fun ModeChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (selected) Color.White.copy(alpha = 0.3f)
                else Color.Transparent
            )
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = if (selected) 0.8f else 0.4f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = Color.White.copy(alpha = if (selected) 1f else 0.7f),
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
private fun sliderColors() = SliderDefaults.colors(
    thumbColor = Color.White,
    activeTrackColor = Color.White,
    inactiveTrackColor = Color.White.copy(alpha = 0.3f)
)
