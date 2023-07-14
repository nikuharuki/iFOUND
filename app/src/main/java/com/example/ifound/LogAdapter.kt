package com.example.ifound

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ifound.databinding.EachLogBinding

class LogAdapter(private val context : Context, private val logsList : ArrayList<LogModel>) :
    RecyclerView.Adapter<LogAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogAdapter.ViewHolder {
        val binding = EachLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, context)
    }

    override fun onBindViewHolder(holder: LogAdapter.ViewHolder, position: Int) {
        logsList[position].let{
            holder.bind(it, position)
        }
    }

    override fun getItemCount(): Int {
        return logsList.size
    }

    class ViewHolder(private val binding : EachLogBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root) {
        fun bind(log : LogModel, position: Int) {
            binding.tvUserLog.text = log.message
        }
    }
}