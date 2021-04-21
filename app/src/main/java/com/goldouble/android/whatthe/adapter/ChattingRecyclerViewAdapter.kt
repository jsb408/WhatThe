package com.goldouble.android.whatthe.adapter

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.goldouble.android.whatthe.constants.ChattingData
import com.goldouble.android.whatthe.R
import com.goldouble.android.whatthe.databinding.ItemChattingBinding
import com.goldouble.android.whatthe.constants.kAuth

class ChattingRecyclerViewAdapter(options: FirestoreRecyclerOptions<ChattingData>)
    : FirestoreRecyclerAdapter<ChattingData, ChattingRecyclerViewAdapter.ItemViewHolder>(options) {
    enum class Chatting(val color: Int, val gravity: Int) {
        ME(R.color.chatting_me, Gravity.END),
        OTHER(R.color.chatting_other, Gravity.START)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemChattingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int, model: ChattingData) {
        model.id = snapshots.getSnapshot(position).id
        holder.bindData(if(kAuth.uid == model.sender) Chatting.ME else Chatting.OTHER, model)
    }

    inner class ItemViewHolder(private val binding: ItemChattingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(sender: Chatting, data: ChattingData) {
            binding.apply {
                layoutChattingBubble.gravity = sender.gravity
                if(data.image.isNullOrEmpty()) {
                    cardViewChattingText.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, sender.color))
                    textChatting.text = data.content
                } else {
                    cardViewChattingImage.setCardBackgroundColor(ContextCompat.getColor(binding.root.context, sender.color))
                    //TODO: 이미지 삽입
                }
            }
        }
    }
}