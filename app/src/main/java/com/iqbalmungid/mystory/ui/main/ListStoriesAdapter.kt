package com.iqbalmungid.mystory.ui.main

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.iqbalmungid.mystory.R
import com.iqbalmungid.mystory.data.remote.response.Stories
import com.iqbalmungid.mystory.databinding.ItemRowStoryBinding

class ListStoriesAdapter : PagingDataAdapter<Stories, ListStoriesAdapter.ListViewHolder>(
    DIFF_CALLBACK
) {

    private var onItemClickCallback: OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val binding =
            ItemRowStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    inner class ListViewHolder(private val binding: ItemRowStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: Stories) {
            binding.apply {
                Glide.with(itemView)
                    .load(user.photoUrl)
                    .centerCrop()
                    .into(imgStory)
                txtName.text = user.name
                val desc = "<b>${user.name}</b> ${user.description}"
                val lat = user.lat
                val lon = user.lon
                txtDesc.text = Html.fromHtml(desc)
                txtTime.text = "Created At ${user.createdAt}"
                if (lat != 0.0 && lon != 0.0) {
                    txtLoc.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location, 0, 0, 0)
                    txtLoc.text = itemView.context.getString(R.string.location_find)
                } else {
                    txtLoc.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_location_off,
                        0,
                        0,
                        0
                    )
                    txtLoc.text = itemView.context.getString(R.string.error_location)
                }

                root.setOnClickListener {
                    onItemClickCallback?.onItemClicked(user)
                }
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: Stories)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Stories>() {
            override fun areItemsTheSame(oldItem: Stories, newItem: Stories): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Stories, newItem: Stories): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}