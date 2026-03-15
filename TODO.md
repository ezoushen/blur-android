# Blur Library Implementation TODO

## Phase 1: Core Infrastructure ✅
- [x] 1.1 Create BitmapPool utility for memory optimization
- [x] 1.2 Create DeviceCapability utility for performance detection
- [x] 1.3 Define BlurAlgorithm interface (strategy pattern)

## Phase 2: Blur Algorithm Implementations ✅
- [x] 2.1 Implement OpenGLBlur with Dual Kawase algorithm (API 23+)
  - [x] Pyramid-based downsample/upsample passes
  - [x] Continuous transitions via iteration blending
  - [x] Logarithmic radius-to-iteration mapping
- [x] 2.2 Implement RenderEffectBlur (API 31+)
  - [x] Uses RenderScript internally for bitmap blur
  - [x] Proper resource management
- [x] 2.3 Create BlurAlgorithmFactory to select best algorithm per device
  - [x] OpenGL Dual Kawase as default for all API 23+

## Phase 3: Content Capture System ✅
- [x] 3.1 Define ContentCapture interface
- [x] 3.2 Implement DecorViewCapture
  - [x] ViewTreeObserver.OnPreDrawListener integration
  - [x] Self-draw prevention (StopException pattern)
  - [x] Position offset calculation
- [x] 3.3 Implement downsampling logic

## Phase 4: Blur Pipeline/Controller ✅
- [x] 4.1 Create BlurController class
  - [x] Integrate capture + algorithm + config
  - [x] Dirty tracking to avoid unnecessary blur
  - [x] Cache management
  - [x] Downsample-independent blur scaling (4x baseline)
- [x] 4.2 Implement PreDrawBlurController for View system

## Phase 5: View Implementation ✅
- [x] 5.1 Complete BlurView implementation
  - [x] Integrate BlurController
  - [x] Lifecycle management (attach/detach)
  - [x] onSizeChanged handling
- [x] 5.2 Define XML attributes (attrs.xml)
- [x] 5.3 Parse XML attributes in BlurView
- [x] 5.4 Implement drawBlurredBitmap with overlay

## Phase 6: Compose Implementation ✅
- [x] 6.1 Implement BlurSurface composable
  - [x] AndroidView wrapper for BlurView
  - [x] True blur-behind effect on all APIs
- [x] 6.2 Create AnimatedBackground composable
  - [x] Canvas-based drawing with infinite animations
  - [x] Replaced native View version
- [x] 6.3 Pure Compose sample app

## Phase 7: Polish & Optimization ✅
- [x] 7.1 Continuous blur transitions (no visible jumps)
  - [x] Blend between N and N+1 iteration levels
  - [x] Fractional part of log₂(radius) as blend factor
- [x] 7.2 Downsample-independent blur
  - [x] Scale radius by (4 / downsampleFactor)
  - [x] Consistent appearance across all scales
- [x] 7.3 Upgrade minSdk to 23 for OpenGL ES 2.0

## Phase 8: Unlimited Radius Migration ✅
- [x] 8.1 Remove radius cap from BlurConfig
  - [x] Update validation to only require non-negative
  - [x] Added Light/Medium/Heavy presets
- [x] 8.2 Update OpenGLBlur for higher iterations
  - [x] MAX_ITERATIONS = 8 (supports ~512px radius)
- [x] 8.3 Update sample app slider range
  - [x] Allow radius up to 100 in demo

## Phase 9: Variable Blur Implementation ✅
- [x] 9.1 Create BlurGradient sealed class
  - [x] Linear gradient (start/end position + radii)
  - [x] Radial gradient (center + edge radii)
  - [x] Sweep gradient (angular blur variation)
  - [x] Support for custom radius stops (Pair<Float, Float>)
  - [x] Convenience methods: verticalGradient, horizontalGradient, angledGradient
- [x] 9.2 Implement VariableOpenGLBlur algorithm
  - [x] Generate blur pyramid (store all levels)
  - [x] Create composite shader for gradient-based sampling
  - [x] Implement gradient calculation in GLSL (linear/radial/sweep)
  - [x] Smooth level interpolation
- [x] 9.3 Create VariableBlurView
  - [x] Add XML attributes (gradientType, startRadius, endRadius, etc.)
  - [x] Kotlin API: setBlurGradient(BlurGradient)
- [x] 9.4 Create VariableBlurSurface composable
  - [x] AndroidView wrapper for VariableBlurView
  - [x] Compose-friendly API matching Brush pattern
  - [x] Convenience composables: VerticalBlurSurface, RadialBlurSurface
- [x] 9.5 Create VariableBlurController
  - [x] Gradient-aware blur pipeline
  - [x] Downsample-independent scaling
- [x] 9.6 Update sample app
  - [x] Add VariableBlurSurface demo
  - [x] Toggle between uniform and variable blur
  - [x] Gradient type selector (linear/radial)
- [x] 9.7 Full multi-stop gradient interpolation
  - [x] Unrolled loop for GLSL ES 2.0 compatibility
  - [x] Support up to 8 stops per gradient
- [x] 9.8 Performance optimizations
  - [x] Lazy level generation (only compute needed levels)
  - [x] Level clamping in shader (uMinLevel/uMaxLevel)

## Phase 11: blur-cmp KMP Module ✅
- [x] 11.1 Update version catalog with KMP/CMP deps
- [x] 11.2 Register blur-cmp module in settings.gradle.kts
- [x] 11.3 Create blur-cmp build.gradle.kts (KMP + CMP + android.library)
- [x] 11.4 Copy iOS Swift sources for blur backend
- [x] 11.5 Define common API: BlurOverlayConfig, BlurOverlayState, BlurOverlayHost (expect)
- [x] 11.6 Define BlurBlendMode, BlurGradientType, BlurColorExtensions
- [x] 11.7 Android actual: BlurOverlayHost via DecorView injection
- [x] 11.8 Android actuals: BlendModeMapper, GradientMapper
- [x] 11.9 iOS actual: BlurOverlayHost via UIVisualEffectView CALayer
- [x] 11.10 iOS actuals: BlendModeMapper, GradientMapper
- [x] 11.11 Add blur-cmp dependency to sample module
- [x] 11.12 Add CMP mode toggle to sample MainActivity
- [x] 11.13 Add maven-publish plugin + GitHub Packages repository to blur-cmp
- [x] 11.14 Verify full build: blur-core release, blur-cmp Android+iOS, sample debug

## Phase 10: Future Enhancements (Optional)
- [ ] 10.1 Add unit tests for BlurConfig, BlurGradient, algorithms
- [ ] 10.2 Add instrumented tests for blur rendering
- [ ] 10.3 Performance benchmarking suite
- [ ] 10.4 Consider AGSL/RuntimeShader for API 33+ (custom shaders)
- [ ] 10.5 Linear color space blur for color accuracy

---

## Summary

| Phase | Status | Description |
|-------|--------|-------------|
| 1. Core Infrastructure | ✅ Complete | BitmapPool, DeviceCapability, interfaces |
| 2. Blur Algorithms | ✅ Complete | OpenGL Dual Kawase (default), RenderEffect |
| 3. Content Capture | ✅ Complete | DecorViewCapture with StopException |
| 4. Blur Pipeline | ✅ Complete | BlurController with downsample scaling |
| 5. View Implementation | ✅ Complete | BlurView with XML attributes |
| 6. Compose Implementation | ✅ Complete | BlurSurface, AnimatedBackground |
| 7. Polish & Optimization | ✅ Complete | Continuous transitions, consistency |
| 8. Unlimited Radius | ✅ Complete | Remove radius cap, support large blur |
| 9. Variable Blur | ✅ Complete | Linear/radial gradient blur + optimizations |
| 10. Future Enhancements | 🔲 Optional | Tests, benchmarks, AGSL |
| 11. blur-cmp KMP Module | ✅ Complete | CMP overlay for Android + iOS, Maven publishing |

---
*Last Updated: March 2026*
