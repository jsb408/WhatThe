package com.goldouble.android.whatthe.ui

import android.content.Context
import android.util.Log
import android.view.SurfaceHolder
import androidx.core.content.ContextCompat.startForegroundService
import com.goldouble.android.whatthe.util.MediaProjectionManagerService

class SurfaceViewHolder(val context: Context) : SurfaceHolder.Callback2 {
    override fun surfaceRedrawNeeded(holder: SurfaceHolder) {
        Log.e("SURFACE", "REDRAW")
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {

    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        Log.e("SURFACE", "CREATE")
        startForegroundService(context, MediaProjectionManagerService.newStartMediaProjection(context, holder.surface))
    }
}