package com.hellmund.meetingtalkdetector.ui.overview

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hellmund.meetingtalkdetector.R
import com.hellmund.meetingtalkdetector.data.Recording
import kotlinx.android.synthetic.main.list_item_recording.view.*
import java.text.SimpleDateFormat
import java.util.*

class RecordingsAdapter(
    private val onClick: (Recording) -> Unit,
    private val onLongClick: (Recording, Int) -> Unit
) : RecyclerView.Adapter<RecordingsAdapter.ViewHolder>() {

    private val items = mutableListOf<Recording>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_recording, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], onClick, onLongClick)
    }

    override fun getItemCount() = items.size

    fun update(newItems: List<Recording>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun remove(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(recording: Recording,
                 onClick: (Recording) -> Unit, onLongClick: (Recording, Int) -> Unit) = with(itemView) {
            val dateText = sdf.format(Date(recording.timestamp))

            titleTextView.text = recording.title
            dateTextView.text = dateText

            setOnClickListener { onClick(recording) }
            setOnLongClickListener {
                onLongClick(recording, adapterPosition)
                true
            }
        }

    }

    companion object {

        @SuppressLint("SimpleDateFormat")
        private val sdf = SimpleDateFormat("YYYY/MM/dd HH:mm")

    }

}
