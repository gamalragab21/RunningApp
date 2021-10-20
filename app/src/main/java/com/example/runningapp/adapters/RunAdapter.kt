package com.example.runningapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.AsyncListUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.runningapp.R
import com.example.runningapp.db.Run
import com.example.runningapp.other.TrackingUtility
import kotlinx.android.synthetic.main.item_run.view.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class RunAdapter @Inject constructor(
    private val glide : RequestManager
)
    : RecyclerView.Adapter<RunAdapter.RunViewHolder>() {

    inner class RunViewHolder(itemView:View):RecyclerView.ViewHolder(itemView)

    val diffCallback=object : DiffUtil.ItemCallback<Run>() {
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
           return oldItem.hashCode()==newItem.hashCode()
        }

    }
    private val differ=AsyncListDiffer(this,diffCallback)
    fun submitList(list: List<Run>)=differ.submitList(list)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        return RunViewHolder(
           LayoutInflater.from(parent.context)
               .inflate(R.layout.item_run,
               parent,
               false)
        )
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
       val run= differ.currentList[position]

        holder.itemView.apply {

            glide.load(run.img).into(ivRunImage)
            val calender=Calendar.getInstance().apply {
                timeInMillis=run.timestamp
            }

            val dateFormat=SimpleDateFormat("ddd.MM", Locale.getDefault())
            tvDate.text=dateFormat.format(calender.time)

            val avgSpeed="${run.avgSpeedInKMH}km/h"
            tvAvgSpeed.text=avgSpeed

            val distance="${run.distanceInMeters /1000f}km"
            tvDistance.text=distance

            tvTime.text=TrackingUtility.getFormattedStopWatchTime(run.timeInMillis)

            val caloriesBurned="${run.caloriesBurned}kcal"
            tvCalories.text=caloriesBurned
            

        }

    }

    override fun getItemCount(): Int = differ.currentList.size


}