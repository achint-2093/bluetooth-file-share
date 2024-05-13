package com.techuntried.bluetooth.ui.chat

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.techuntried.bluetooth.databinding.ChatMessageBinding
import com.techuntried.bluetoothshare.domain.model.BluetoothMessage


class MessagesListAdapter :
    ListAdapter<BluetoothMessage, MessagesListAdapter.MyViewHolder>(MessagesDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MyViewHolder private constructor(private val binding: ChatMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: BluetoothMessage
        ) {
            binding.item = item

            if (item.isFromLocalUser) {
                binding.chatItemLayout.gravity = Gravity.END
            } else {
                binding.chatItemLayout.gravity = Gravity.START
            }

        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = ChatMessageBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(view)

            }
        }
    }

}


class MessagesDiffUtil : DiffUtil.ItemCallback<BluetoothMessage>() {
    override fun areItemsTheSame(
        oldItem: BluetoothMessage,
        newItem: BluetoothMessage
    ): Boolean {
        return oldItem.message == newItem.message
    }

    override fun areContentsTheSame(
        oldItem: BluetoothMessage,
        newItem: BluetoothMessage
    ): Boolean {
        return oldItem == newItem
    }

}

