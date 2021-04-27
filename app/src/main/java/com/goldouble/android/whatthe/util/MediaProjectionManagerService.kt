package com.goldouble.android.whatthe.util

import android.app.*
import android.app.Activity.RESULT_CANCELED
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.IBinder
import android.util.Log
import android.view.Surface
import com.goldouble.android.whatthe.MediaProjectionPermissionActivity
import com.goldouble.android.whatthe.R
import com.goldouble.android.whatthe.constants.*

open class MediaProjectionManagerService : Service() {
    private val FOREGROUND_SERVICE_ID = 1000
    private val CHANNEL_ID = "MediaProjectionService"

    companion object {
        fun newStartMediaProjection(
                context: Context,
                surface: Surface,
                projectionName: String = DEFAULT_VALUE_PROJECTION_NAME,
                width: Int = DEFAULT_VALUE_SIZE_WIDTH,
                height: Int = DEFAULT_VALUE_SIZE_HEIGHT
        ): Intent = Intent(context, MediaProjectionManagerService::class.java).apply {
            putExtra(EXTRA_SURFACE, surface)
            putExtra(EXTRA_PROJECTION_NAME, projectionName)
            putExtra(EXTRA_SIZE_WIDTH, width)
            putExtra(EXTRA_SIZE_HEIGHT, height)
            action = ACTION_START
        }
    }

    private val mediaProjectionManager: MediaProjectionManager by lazy {
        getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    }

    private lateinit var mediaProjection: MediaProjection
    private lateinit var virtualDisplay: VirtualDisplay

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
    }

    private fun startForegroundService() {
        createNotificationChannel()
        val notificationIntent = Intent(this, MediaProjectionPermissionActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val notification = Notification.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setSmallIcon(R.drawable.ic_baseline_camera_alt_50)
                .setContentIntent(pendingIntent)
                .build()
        startForeground(FOREGROUND_SERVICE_ID, notification)
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(CHANNEL_ID, "Foreground Service Channel", NotificationManager.IMPORTANCE_DEFAULT)
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
            createNotificationChannel(serviceChannel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        action(intent)
        return START_REDELIVER_INTENT
    }

    private fun action(intent: Intent?) {
        when (intent?.action) {
            ACTION_INIT -> getPermission(intent)
            ACTION_PERMISSION_INIT -> permissionInitMediaProjection(intent)
            ACTION_REJECT -> rejectMediaProjection()
            ACTION_START -> startMediaProjection(intent)
            ACTION_STOP -> stopMediaProjection()
            ACTION_SELF_STOP -> stopSelf()
        }
    }

    fun createMediaProjection() {
        Log.e("SERVICE", "CREATE")
        getPermission()
    }

    private fun getPermission(intent: Intent? = null) {
        MediaProjectionBroadcastReceiver.register(this, ::action)
        startActivity(Intent(this, MediaProjectionPermissionActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    private fun unregisterReceiver() {
        MediaProjectionBroadcastReceiver.unregister(this)
    }

    private fun permissionInitMediaProjection(intent: Intent) {
        unregisterReceiver()
        val resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, RESULT_CANCELED)
        val data = intent.getParcelableExtra<Intent>(EXTRA_REQUEST_DATA)
        mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data!!)
        mediaProjection.registerCallback(object : MediaProjection.Callback() { }, null)
        Log.e("MEDIA", "INITIALIZED")
    }

    private fun rejectMediaProjection() {
        unregisterReceiver()
    }

    private fun startMediaProjection(intent: Intent) {
        startMediaProjection(
                surface = intent.extras!![EXTRA_SURFACE] as Surface,
                projectionName = intent.getStringExtra(EXTRA_PROJECTION_NAME) ?: DEFAULT_VALUE_PROJECTION_NAME,
                width = intent.getIntExtra(EXTRA_SIZE_WIDTH, DEFAULT_VALUE_SIZE_WIDTH),
                height = intent.getIntExtra(EXTRA_SIZE_HEIGHT, DEFAULT_VALUE_SIZE_HEIGHT)
        )
    }

    private fun startMediaProjection(surface: Surface, projectionName: String = DEFAULT_VALUE_PROJECTION_NAME, width: Int = DEFAULT_VALUE_SIZE_WIDTH, height: Int = DEFAULT_VALUE_SIZE_HEIGHT) {
        Log.e("MEDIA", surface.toString())
        if (::mediaProjection.isInitialized) {
            virtualDisplay = mediaProjection.createVirtualDisplay(
                    projectionName,
                    width,
                    height,
                    application.resources.displayMetrics.densityDpi,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    surface,
                    null,
                    null
            )
            Log.d("VIRTUAL DISPLAY", "CREATED")
        } else {
            Log.e("VIRTUAL DISPLAY", "NOT CREATED")
        }
    }

    private fun stopMediaProjection() {
        try {
            if (::mediaProjection.isInitialized) {
                mediaProjection.stop()
            }
            if (::virtualDisplay.isInitialized) {
                virtualDisplay.release()
            }
        } catch (e: Exception) {
        }
    }
}