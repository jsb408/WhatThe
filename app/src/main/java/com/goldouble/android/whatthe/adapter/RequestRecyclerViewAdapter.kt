package com.goldouble.android.whatthe.adapter

import android.app.AlertDialog
import android.content.Intent
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.goldouble.android.whatthe.*
import com.goldouble.android.whatthe.constants.RequestData
import com.goldouble.android.whatthe.constants.Table
import com.goldouble.android.whatthe.constants.kFirestore
import com.goldouble.android.whatthe.constants.kStorage
import com.goldouble.android.whatthe.databinding.ItemRequestBinding

class RequestRecyclerViewAdapter(options: FirestoreRecyclerOptions<RequestData>, val mediaPlayer: MediaPlayer)
    : FirestoreRecyclerAdapter<RequestData, RequestRecyclerViewAdapter.ItemViewHolder>(options) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int, model: RequestData) {
        model.id = snapshots.getSnapshot(position).id
        holder.bindData(model)
    }

    inner class ItemViewHolder(private val binding: ItemRequestBinding): RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: RequestData) {
            binding.apply {
                imageRequestPlay.setOnClickListener {
                    imageRequestPlay.setImageResource(R.drawable.ic_baseline_cloud_download_50)
                    kStorage.child("records/${data.record}.aac").downloadUrl.addOnSuccessListener {
                        imageRequestPlay.setImageResource(R.drawable.ic_baseline_pause_50)
                        mediaPlayer.apply {
                            reset()
                            setDataSource(it.toString())
                            prepare()
                            setOnCompletionListener {
                                imageRequestPlay.setImageResource(R.drawable.ic_baseline_play_arrow_50)
                            }
                        }.start()
                    }
                }

                cardViewRequest.setOnClickListener {
                    AlertDialog.Builder(binding.root.context, R.style.AlertDialogStyle)
                        .setMessage("연결하시겠습니까?")
                        .setPositiveButton("확인") { _, _ ->
                            binding.root.context.startActivity(Intent(binding.root.context, ChattingActivity::class.java)
                                .putExtra("roomNo", data.id))
                            kFirestore.collection(Table.REQUEST.id).document(data.id).update("response", true)
                        }
                        .setNegativeButton("취소") { _, _ -> }
                        .show()
                }

                //TODO : Request 시간 text 수정정
            }
       }
    }
}