package com.example.httpchat.ui.messages

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.httpchat.databinding.ItemMessageFromBinding
import com.example.httpchat.models.responses.User
import com.example.httpchat.models.responses.UserAndMessageThumbnail
import java.text.SimpleDateFormat
import java.util.*

class MessagesRecyclerAdapter : RecyclerView.Adapter<MessagesRecyclerAdapter.ViewHolder>() {
    private val viewBinderHelper: ViewBinderHelper = ViewBinderHelper()

    private var conversations: MutableList<UserAndMessageThumbnail> =
        mutableListOf()

    var itemClickedListener: ((user: User) -> Unit)? = null

    var itemDeleteListener: ((userMappingId: Long) -> Unit)? = null

    var lastItemLoadedListener: (() -> Unit)? = null

    init {
        viewBinderHelper.setOpenOnlyOne(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemMessageFromBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return conversations.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        viewBinderHelper.bind(
            holder.binding.swipeLayout,
            conversations[position].message?.text + position.toString()
        )
        holder.bindData(position)
    }

    fun setData(conversations: MutableList<UserAndMessageThumbnail>) {
        this.conversations = conversations.toMutableList()
        notifyItemRangeInserted(
            this.conversations.count() - conversations.count(),
            conversations.count()
        )
    }

    inner class ViewHolder(val binding: ItemMessageFromBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.conversation.setOnClickListener {
                itemClickedListener?.invoke(conversations[adapterPosition].user)
            }

            binding.deleteImage.setOnClickListener {
                conversations[adapterPosition].message?.userMappingId?.let { it1 ->
                    itemDeleteListener?.invoke(
                        it1
                    )
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bindData(position: Int) {
            if (!conversations[position].user.picture.isNullOrEmpty()) {
                val imageAsBytes: ByteArray =
                    Base64.decode(conversations[position].user.picture, Base64.DEFAULT)
                val image = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.count())
                Glide.with(binding.root.context)
                    .load(image)
                    .into(binding.messageFromImage)
            }

            binding.messageFromText.text = conversations[position].user.name
            binding.messageText.text = conversations[position].message?.text
            conversations[position].message?.let {
                val diffTime = System.currentTimeMillis() - it.dateMillis
                val seconds = diffTime / 1000
                val minutes = seconds / 60
                val hours = minutes / 60
                when {
                    seconds < 60 -> {
                        binding.messageSentTimeText.text = "$seconds seconds ago"
                    }
                    minutes < 60 -> {
                        binding.messageSentTimeText.text = "$minutes minutes ago"
                    }
                    hours < 24 -> {
                        binding.messageSentTimeText.text = "$hours hours ago"
                    }
                    else -> {
                        val date = Date(it.dateMillis)
                        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        binding.messageSentTimeText.text = simpleDateFormat.format(date)
                    }
                }
            }


            binding.deleteImage.setOnClickListener {
                conversations.removeAt(position)
                notifyDataSetChanged()
            }
            if (position == conversations.count() - 1) {
                lastItemLoadedListener?.invoke()
            }
        }
    }
}