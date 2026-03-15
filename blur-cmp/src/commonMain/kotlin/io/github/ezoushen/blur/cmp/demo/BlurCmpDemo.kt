package io.github.ezoushen.blur.cmp.demo

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ezoushen.blur.cmp.BlurBlendMode
import io.github.ezoushen.blur.cmp.BlurGradientType
import io.github.ezoushen.blur.cmp.BlurOverlayConfig
import io.github.ezoushen.blur.cmp.BlurOverlayHost
import io.github.ezoushen.blur.cmp.rememberBlurOverlayState
import io.github.ezoushen.blur.cmp.withTint

// ---------------------------------------------------------------------------
// Animated background (self-contained, no sample module dependency)
// ---------------------------------------------------------------------------

@Composable
private fun AnimatedBackground(modifier: Modifier = Modifier) {
    val density = LocalDensity.current

    val infiniteTransition = rememberInfiniteTransition(label = "background")

    val offset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset1"
    )

    val offset2 by infiniteTransition.animateFloat(
        initialValue = 100f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset2"
    )

    val backgroundColors = listOf(
        Color(0xFF1a1a2e),
        Color(0xFF16213e),
        Color(0xFF0f3460)
    )
    val circleColor1 = Color(0xFFe94560)
    val circleColor2 = Color(0xFF0f3460)
    val circleColor3 = Color(0xFF533483)
    val circleColor4 = Color(0xFF00b4d8)

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val dpToPx = with(density) { 1.dp.toPx() }

        drawRect(
            brush = Brush.verticalGradient(
                colors = backgroundColors,
                startY = 0f,
                endY = h
            )
        )

        val circle1Size = 200 * dpToPx
        val circle1X = offset1 * dpToPx + circle1Size / 2
        val circle1Y = (50 + offset2 * 0.5f) * dpToPx + circle1Size / 2
        drawCircle(
            color = circleColor1,
            radius = circle1Size / 2,
            center = Offset(circle1X, circle1Y)
        )

        val circle2Size = 250 * dpToPx
        val circle2X = (200 - offset1) * dpToPx + circle2Size / 2
        val circle2Y = (300 + offset1 * 0.3f) * dpToPx + circle2Size / 2
        drawCircle(
            color = circleColor2,
            radius = circle2Size / 2,
            center = Offset(circle2X, circle2Y)
        )

        val circle3Size = 180 * dpToPx
        val circle3X = (offset2 + 50) * dpToPx + circle3Size / 2
        val circle3Y = (500 - offset1 * 0.2f) * dpToPx + circle3Size / 2
        drawCircle(
            color = circleColor3,
            radius = circle3Size / 2,
            center = Offset(circle3X, circle3Y)
        )

        val circle4Size = 160 * dpToPx
        val circle4X = (280 - offset2 * 0.5f) * dpToPx + circle4Size / 2
        val circle4Y = (150 + offset2) * dpToPx + circle4Size / 2
        drawCircle(
            color = circleColor4,
            radius = circle4Size / 2,
            center = Offset(circle4X, circle4Y)
        )
    }
}

// ---------------------------------------------------------------------------
// Reusable UI components
// ---------------------------------------------------------------------------

@Composable
private fun DemoChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
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
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        androidx.compose.foundation.text.BasicText(
            text = text,
            style = androidx.compose.ui.text.TextStyle(
                color = Color.White.copy(alpha = if (selected) 1f else 0.7f),
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
            )
        )
    }
}

@Composable
private fun LabelText(text: String, modifier: Modifier = Modifier) {
    androidx.compose.foundation.text.BasicText(
        text = text,
        modifier = modifier,
        style = androidx.compose.ui.text.TextStyle(
            color = Color.White,
            fontSize = 14.sp,
        )
    )
}

@Composable
private fun TitleText(text: String, modifier: Modifier = Modifier) {
    androidx.compose.foundation.text.BasicText(
        text = text,
        modifier = modifier,
        style = androidx.compose.ui.text.TextStyle(
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )
    )
}

@Composable
private fun HeadlineText(text: String, modifier: Modifier = Modifier) {
    androidx.compose.foundation.text.BasicText(
        text = text,
        modifier = modifier,
        style = androidx.compose.ui.text.TextStyle(
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
    )
}

/**
 * A simple slider built from Foundation APIs only (no Material dependency).
 * Renders a horizontal track with a draggable thumb.
 */
@Composable
private fun DemoSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    modifier: Modifier = Modifier,
) {
    val fraction = ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start))
        .coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(32.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val trackY = size.height / 2
            val trackHeight = 4.dp.toPx()
            val thumbRadius = 10.dp.toPx()
            val trackStart = thumbRadius
            val trackEnd = size.width - thumbRadius
            val thumbX = trackStart + (trackEnd - trackStart) * fraction

            // Inactive track
            drawLine(
                color = Color.White.copy(alpha = 0.3f),
                start = Offset(trackStart, trackY),
                end = Offset(trackEnd, trackY),
                strokeWidth = trackHeight,
            )
            // Active track
            drawLine(
                color = Color.White,
                start = Offset(trackStart, trackY),
                end = Offset(thumbX, trackY),
                strokeWidth = trackHeight,
            )
            // Thumb
            drawCircle(
                color = Color.White,
                radius = thumbRadius,
                center = Offset(thumbX, trackY),
            )
        }

        // Invisible touch target using pointerInput
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(valueRange) {
                    val thumbRadius = 10.dp.toPx()
                    val trackStart = thumbRadius
                    val trackEnd = size.width - thumbRadius

                    detectHorizontalDragGestures(
                        onDragStart = { startOffset ->
                            val frac = ((startOffset.x - trackStart) / (trackEnd - trackStart))
                                .coerceIn(0f, 1f)
                            onValueChange(
                                valueRange.start + frac * (valueRange.endInclusive - valueRange.start)
                            )
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            val currentFrac = ((value - valueRange.start) /
                                (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)
                            val currentX = trackStart + (trackEnd - trackStart) * currentFrac
                            val newX = (currentX + dragAmount).coerceIn(trackStart, trackEnd)
                            val newFrac = ((newX - trackStart) / (trackEnd - trackStart))
                                .coerceIn(0f, 1f)
                            onValueChange(
                                valueRange.start + newFrac * (valueRange.endInclusive - valueRange.start)
                            )
                        }
                    )
                }
        )
    }
}

// ---------------------------------------------------------------------------
// Demo tabs
// ---------------------------------------------------------------------------

private enum class DemoTab(val label: String) {
    Uniform("Uniform Blur"),
    Variable("Variable Blur"),
    ColorDodge("Color Dodge"),
}

// ---------------------------------------------------------------------------
// Main demo screen
// ---------------------------------------------------------------------------

/**
 * Cross-platform demo screen for blur-cmp.
 * Uses only commonMain APIs -- safe to call from both Android and iOS.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BlurCmpDemoScreen() {
    var selectedTab by remember { mutableStateOf(DemoTab.Uniform) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Layer 1: animated background
        AnimatedBackground(modifier = Modifier.fillMaxSize())

        // Layer 2: blur overlay + controls
        when (selectedTab) {
            DemoTab.Uniform -> UniformBlurDemo(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
            )
            DemoTab.Variable -> VariableBlurDemo(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
            )
            DemoTab.ColorDodge -> ColorDodgeDemo(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Tab selector row
// ---------------------------------------------------------------------------

@Composable
private fun TabRow(
    selectedTab: DemoTab,
    onTabSelected: (DemoTab) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.3f))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DemoTab.entries.forEach { tab ->
            DemoChip(
                text = tab.label,
                selected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Control panel wrapper
// ---------------------------------------------------------------------------

@Composable
private fun ControlPanel(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(Color.Black.copy(alpha = 0.45f))
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .padding(bottom = 12.dp)
    ) {
        TitleText(text = title)
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

// ---------------------------------------------------------------------------
// 1. Uniform Blur demo
// ---------------------------------------------------------------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun UniformBlurDemo(
    selectedTab: DemoTab,
    onTabSelected: (DemoTab) -> Unit,
) {
    var radius by remember { mutableFloatStateOf(20f) }
    var tintAlpha by remember { mutableFloatStateOf(0.15f) }
    var blendModeIndex by remember { mutableIntStateOf(0) }

    val blendModes = remember {
        listOf(
            BlurBlendMode.Normal,
            BlurBlendMode.ColorDodge,
            BlurBlendMode.Multiply,
            BlurBlendMode.Screen,
            BlurBlendMode.Overlay,
        )
    }
    val blendModeNames = remember { listOf("Normal", "ColorDodge", "Multiply", "Screen", "Overlay") }

    val blurState = rememberBlurOverlayState(
        initialConfig = BlurOverlayConfig(
            radius = radius,
            tintBlendMode = blendModes[blendModeIndex],
        ).withTint(Color.White.copy(alpha = tintAlpha))
    )

    blurState.setRadius(radius)
    blurState.config = blurState.config
        .copy(tintBlendMode = blendModes[blendModeIndex])
        .withTint(Color.White.copy(alpha = tintAlpha))

    BlurOverlayHost(state = blurState) {
        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(selectedTab = selectedTab, onTabSelected = onTabSelected)

            Spacer(modifier = Modifier.weight(1f))

            HeadlineText(
                text = "Uniform Blur",
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            LabelText(
                text = "Fullscreen blur overlay with tint",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 4.dp),
            )

            Spacer(modifier = Modifier.weight(1f))

            ControlPanel(title = "Uniform Blur Controls") {
                LabelText(text = "Radius: ${radius.toInt()}")
                DemoSlider(
                    value = radius,
                    onValueChange = { radius = it },
                    valueRange = 0f..60f,
                    modifier = Modifier.padding(top = 4.dp),
                )

                Spacer(modifier = Modifier.height(12.dp))

                LabelText(text = "Tint Alpha: ${(tintAlpha * 100).toInt()}%")
                DemoSlider(
                    value = tintAlpha,
                    onValueChange = { tintAlpha = it },
                    valueRange = 0f..1f,
                    modifier = Modifier.padding(top = 4.dp),
                )

                Spacer(modifier = Modifier.height(12.dp))

                LabelText(text = "Blend Mode")
                FlowRow(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    blendModeNames.forEachIndexed { index, name ->
                        DemoChip(
                            text = name,
                            selected = index == blendModeIndex,
                            onClick = { blendModeIndex = index },
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 2. Variable Blur demo
// ---------------------------------------------------------------------------

@Composable
private fun VariableBlurDemo(
    selectedTab: DemoTab,
    onTabSelected: (DemoTab) -> Unit,
) {
    var radius by remember { mutableFloatStateOf(30f) }
    var startIntensity by remember { mutableFloatStateOf(1f) }
    var endIntensity by remember { mutableFloatStateOf(0f) }
    var useSpotlight by remember { mutableStateOf(false) }
    var spotlightRadius by remember { mutableFloatStateOf(0.4f) }

    val gradient = remember(useSpotlight, startIntensity, endIntensity, spotlightRadius) {
        if (useSpotlight) {
            BlurGradientType.spotlight(radius = spotlightRadius)
        } else {
            BlurGradientType.verticalTopToBottom(
                startIntensity = startIntensity,
                endIntensity = endIntensity,
            )
        }
    }

    val blurState = rememberBlurOverlayState(
        initialConfig = BlurOverlayConfig(
            radius = radius,
            gradient = gradient,
        )
    )

    blurState.setRadius(radius)
    blurState.setGradient(gradient)

    BlurOverlayHost(state = blurState) {
        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(selectedTab = selectedTab, onTabSelected = onTabSelected)

            Spacer(modifier = Modifier.weight(1f))

            HeadlineText(
                text = if (useSpotlight) "Radial Spotlight" else "Vertical Gradient",
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            LabelText(
                text = if (useSpotlight) "Clear center, blurred edges"
                else "Top blurred, bottom clear",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 4.dp),
            )

            Spacer(modifier = Modifier.weight(1f))

            ControlPanel(title = "Variable Blur Controls") {
                LabelText(text = "Radius: ${radius.toInt()}")
                DemoSlider(
                    value = radius,
                    onValueChange = { radius = it },
                    valueRange = 0f..60f,
                    modifier = Modifier.padding(top = 4.dp),
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    DemoChip(
                        text = "Vertical",
                        selected = !useSpotlight,
                        onClick = { useSpotlight = false },
                    )
                    DemoChip(
                        text = "Spotlight",
                        selected = useSpotlight,
                        onClick = { useSpotlight = true },
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (useSpotlight) {
                    LabelText(text = "Spotlight Radius: ${(spotlightRadius * 100).toInt()}%")
                    DemoSlider(
                        value = spotlightRadius,
                        onValueChange = { spotlightRadius = it },
                        valueRange = 0.1f..1f,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                } else {
                    LabelText(text = "Start Intensity: ${(startIntensity * 100).toInt()}%")
                    DemoSlider(
                        value = startIntensity,
                        onValueChange = { startIntensity = it },
                        valueRange = 0f..1f,
                        modifier = Modifier.padding(top = 4.dp),
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LabelText(text = "End Intensity: ${(endIntensity * 100).toInt()}%")
                    DemoSlider(
                        value = endIntensity,
                        onValueChange = { endIntensity = it },
                        valueRange = 0f..1f,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// 3. Color Dodge Showcase
// ---------------------------------------------------------------------------

@Composable
private fun ColorDodgeDemo(
    selectedTab: DemoTab,
    onTabSelected: (DemoTab) -> Unit,
) {
    var radius by remember { mutableFloatStateOf(25f) }
    var tintAlpha by remember { mutableFloatStateOf(0.15f) }
    var showNormal by remember { mutableStateOf(false) }

    // Color Dodge state
    val dodgeState = rememberBlurOverlayState(
        initialConfig = BlurOverlayConfig(
            radius = radius,
            tintBlendMode = BlurBlendMode.ColorDodge,
        ).withTint(Color.White.copy(alpha = tintAlpha))
    )

    dodgeState.setRadius(radius)
    dodgeState.config = dodgeState.config
        .copy(tintBlendMode = if (showNormal) BlurBlendMode.Normal else BlurBlendMode.ColorDodge)
        .withTint(Color.White.copy(alpha = tintAlpha))

    BlurOverlayHost(state = dodgeState) {
        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(selectedTab = selectedTab, onTabSelected = onTabSelected)

            Spacer(modifier = Modifier.weight(1f))

            HeadlineText(
                text = "Color Dodge Showcase",
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            LabelText(
                text = if (showNormal) "Normal blend mode (comparison)"
                else "ColorDodge: brightens underlying colors",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 4.dp),
            )

            Spacer(modifier = Modifier.weight(1f))

            ControlPanel(title = "Color Dodge Controls") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    DemoChip(
                        text = "ColorDodge",
                        selected = !showNormal,
                        onClick = { showNormal = false },
                    )
                    DemoChip(
                        text = "Normal",
                        selected = showNormal,
                        onClick = { showNormal = true },
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                LabelText(text = "Radius: ${radius.toInt()}")
                DemoSlider(
                    value = radius,
                    onValueChange = { radius = it },
                    valueRange = 0f..60f,
                    modifier = Modifier.padding(top = 4.dp),
                )

                Spacer(modifier = Modifier.height(12.dp))

                LabelText(text = "Tint Alpha: ${(tintAlpha * 100).toInt()}%")
                DemoSlider(
                    value = tintAlpha,
                    onValueChange = { tintAlpha = it },
                    valueRange = 0f..1f,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}
