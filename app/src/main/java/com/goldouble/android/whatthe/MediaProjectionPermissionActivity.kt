package com.goldouble.android.whatthe

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import com.goldouble.android.whatthe.util.MediaProjectionBroadcastReceiver

class MediaProjectionPermissionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_projection_permission)

        val mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        registerForActivityResult(MediaProjectionResultContract()) {
            sendBroadcast(it)
            finish()
        }.launch(mediaProjectionManager.createScreenCaptureIntent())
    }
}

internal class MediaProjectionResultContract : ActivityResultContract<Intent, Intent>() {
    override fun createIntent(context: Context, input: Intent?): Intent = input!!

    override fun parseResult(resultCode: Int, intent: Intent?): Intent {
        return if (resultCode == RESULT_OK && intent != null) {
            MediaProjectionBroadcastReceiver.newInstance(resultCode, intent)
        } else {
            MediaProjectionBroadcastReceiver.newReject()
        }
    }
}