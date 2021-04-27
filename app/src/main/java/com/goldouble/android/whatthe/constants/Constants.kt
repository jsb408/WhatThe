package com.goldouble.android.whatthe.constants

import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.goldouble.android.whatthe.R
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.util.*

enum class Table(val id: String) {
    REQUEST("REQUEST"),
    CHATTING("CHATTING")
}

data class RequestData(
    @get:Exclude var id: String = "",
    val date: Date = Date(),
    val record: String = ""
)

data class ChattingData(
    @get:Exclude var id: String = "",
    val room: String = "",
    val date: Date = Date(),
    val content: String? = null,
    val image: String? = null,
    val sender: String? = null
)

fun kSetActionBar(activity: AppCompatActivity) {
    activity.apply {
        supportActionBar?.apply {
            setDisplayShowCustomEnabled(true)
            setDisplayShowTitleEnabled(false)
        }
        //커스텀액션바 표시
        supportActionBar?.customView = LayoutInflater.from(this).inflate(R.layout.toolbar_custom, null)
        supportActionBar?.elevation = 0f
    }
}

fun kRecordDir(dataDir: File): String {
    val dir = File(dataDir.absolutePath + "/records")
    if (!dir.exists()) dir.mkdir()
    return dir.absolutePath
}

fun kScreenshotDir(dataDir: File): String {
    val dir = File(dataDir.absolutePath + "/screenshots")
    if (!dir.exists()) dir.mkdir()
    return dir.absolutePath
}

val kAuth = Firebase.auth
val kFirestore = Firebase.firestore
val kStorage = Firebase.storage.reference

var kAnonymousUser: FirebaseUser? = null