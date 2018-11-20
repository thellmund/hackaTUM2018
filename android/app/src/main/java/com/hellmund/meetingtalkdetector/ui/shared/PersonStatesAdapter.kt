package com.hellmund.meetingtalkdetector.ui.shared

import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hellmund.meetingtalkdetector.R
import com.hellmund.meetingtalkdetector.data.PersonState
import kotlinx.android.synthetic.main.list_item_person_state.view.*
import org.jetbrains.anko.windowManager
import kotlin.math.roundToInt

class PersonStatesAdapter : RecyclerView.Adapter<PersonStatesAdapter.ViewHolder>() {

    private var maxTalkingTime = 0L
    private val items = mutableListOf<PersonState>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_person_state, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], maxTalkingTime)
    }

    override fun getItemCount() = items.size

    fun update(newItems: List<PersonState>) {
        maxTalkingTime = newItems.map { it.talkingTime }.max() ?: 0L
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(state: PersonState, maxTalkingTime: Long) = with(itemView) {
            state.image?.let {
                personImageView.setImageBitmap(it)
            }

            val minutes = state.talkingTime / (60 * 1000)
            val seconds = (state.talkingTime / 1000) % 60
            val milliseconds = state.talkingTime % 1000
            val formattedTalkingTime = String.format("%d:%02d.%03d", minutes, seconds, milliseconds)
            personTalkingTimeTextView.text = formattedTalkingTime
            if (maxTalkingTime == 0L) {
                return@with
            }

            val availableWidth = getAvailableWidth(context)
            Log.d(ViewHolder::class.java.simpleName, "Available width: $availableWidth")

            val percentage = state.talkingTime.toFloat() / maxTalkingTime.toFloat()
            Log.d(ViewHolder::class.java.simpleName, "Percentage: $percentage")

            val newWidth = availableWidth * percentage
            val params = talkingTimeBar.layoutParams
            params.width = newWidth.roundToInt()
            talkingTimeBar.layoutParams = params
        }

        private fun getAvailableWidth(context: Context): Float {
            val margins = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, context.resources.displayMetrics)
            val imageWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40f, context.resources.displayMetrics)
            val displayMetrics = DisplayMetrics()
            context.windowManager.defaultDisplay.getMetrics(displayMetrics)

            val width = displayMetrics.widthPixels
            Log.d(ViewHolder::class.java.simpleName, "Display width: $width")

            return width - imageWidth - margins * 4
        }

    }

}
