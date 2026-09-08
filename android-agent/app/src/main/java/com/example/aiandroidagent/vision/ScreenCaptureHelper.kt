package com.example.aiandroidagent.vision

/**
 * Shared in-memory bridge for the optional screen-analysis pipeline.
 * The foreground ScreenCaptureService was intentionally removed because
 * the current agent does not need a foreground service just to build/run.
 */
object ScreenCaptureHelper {
    @Volatile
    var lastCapturedImage: ByteArray? = null
}
