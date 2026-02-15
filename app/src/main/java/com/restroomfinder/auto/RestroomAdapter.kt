package com.restroomfinder.auto

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.restroomfinder.auto.data.model.Restroom
import com.restroomfinder.auto.databinding.ItemRestroomBinding

class RestroomAdapter(private val onRestroomClicked: (Restroom) -> Unit) :
    ListAdapter<Restroom, RestroomAdapter.RestroomViewHolder>(RestroomDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestroomViewHolder {
        val binding = ItemRestroomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RestroomViewHolder(binding, onRestroomClicked)
    }

    override fun onBindViewHolder(holder: RestroomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class RestroomViewHolder(
        private val binding: ItemRestroomBinding,
        private val onRestroomClicked: (Restroom) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(restroom: Restroom) {
            binding.restroomName.text = restroom.name
            binding.restroomAddress.text = restroom.address
            binding.restroomDistance.text = restroom.getFormattedDistance()
            binding.restroomFeatures.text = restroom.getAccessibilityInfo()
            binding.root.setOnClickListener { onRestroomClicked(restroom) }
        }
    }

    class RestroomDiffCallback : DiffUtil.ItemCallback<Restroom>() {
        override fun areItemsTheSame(oldItem: Restroom, newItem: Restroom): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Restroom, newItem: Restroom): Boolean {
            return oldItem == newItem
        }
    }
}
