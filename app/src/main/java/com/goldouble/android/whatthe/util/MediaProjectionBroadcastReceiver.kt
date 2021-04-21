package com.goldouble.android.whatthe.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.goldouble.android.whatthe.constants.*

class MediaProjectionBroadcastReceiver private constructor(
        private val onReceive: (intent: Intent?) -> Unit
) : BroadcastReceiver() {
    companion object {
        private const val KEY_ACTION = "action"
        private const val INTENT_FILTER_MEDIA_PROJECTION = "media_projection_permission_event"

        private var receiver: MediaProjectionBroadcastReceiver? = null

        fun register(context: Context, onReceive: (intent: Intent?) -> Unit) {
            if (receiver == null) {
                val receiver = MediaProjectionBroadcastReceiver(onReceive)
                context.registerReceiver(receiver, IntentFilter(INTENT_FILTER_MEDIA_PROJECTION))
            }
        }

        fun unregister(context: Context) {
            receiver?.let(context::unregisterReceiver)
            receiver = null
        }

        fun newInstance(resultCode: Int, requestData: Intent): Intent =
                Intent(INTENT_FILTER_MEDIA_PROJECTION)
                        .putExtra(EXTRA_RESULT_CODE, resultCode)
                        .putExtra(EXTRA_REQUEST_DATA, requestData)
                        .putExtra(KEY_ACTION, ACTION_PERMISSION_INIT)

        fun newReject(): Intent =
                Intent(INTENT_FILTER_MEDIA_PROJECTION)
                        .putExtra(KEY_ACTION, ACTION_REJECT)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.action = intent?.getStringExtra(KEY_ACTION)
        onReceive(intent)
    }
}