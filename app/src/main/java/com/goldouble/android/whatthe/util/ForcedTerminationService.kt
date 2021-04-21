package com.goldouble.android.whatthe.util

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class ForcedTerminationService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)

        Log.e("APPLICATION", "onTaskRemoved - 강제종료 : $rootIntent")
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("APPLICATION", "SERVICE IS REMOVED")
    }
}