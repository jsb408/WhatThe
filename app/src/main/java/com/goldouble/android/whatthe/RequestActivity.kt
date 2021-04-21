package com.goldouble.android.whatthe

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.goldouble.android.whatthe.adapter.RequestRecyclerViewAdapter
import com.goldouble.android.whatthe.constants.RequestData
import com.goldouble.android.whatthe.constants.Table
import com.goldouble.android.whatthe.constants.kFirestore
import com.goldouble.android.whatthe.constants.kSetActionBar
import com.goldouble.android.whatthe.databinding.ActivityRequestBinding

class RequestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRequestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        kSetActionBar(this)

        val mediaPlayer = MediaPlayer()
        val query = kFirestore.collection(Table.REQUEST.id).orderBy("date")
        val options = FirestoreRecyclerOptions.Builder<RequestData>().setQuery(query, RequestData::class.java).build()
        val requestAdapter = RequestRecyclerViewAdapter(options, mediaPlayer)

        binding.recyclerViewRequest.adapter = requestAdapter
        binding.recyclerViewRequest.layoutManager = LinearLayoutManager(this)

        requestAdapter.startListening()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_request, menu)
        return super.onCreateOptionsMenu(menu)
    }
}