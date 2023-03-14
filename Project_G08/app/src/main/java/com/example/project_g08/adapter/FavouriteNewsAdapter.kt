package com.example.project_g08.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project_g08.R
import com.example.project_g08.models.FavouriteNews
import com.example.project_g08.databinding.NewsViewBinding
import com.example.project_g08.listeners.OnNewsClickListener

class FavouriteNewsAdapter(private val context: Context,
                           private val dataSet: ArrayList<FavouriteNews>,
                           private val clickListener: OnNewsClickListener)
    : RecyclerView.Adapter<FavouriteNewsAdapter.FavouriteNewsViewHolder>(){

    inner class FavouriteNewsViewHolder(var binding: NewsViewBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(currentItem: FavouriteNews, clickListener: OnNewsClickListener){
            binding.tvTitle.text = currentItem.title

            if(currentItem.pic!=""){
                Glide.with(binding.tvTitle.context).load(currentItem.pic).into(binding.ivPic)
            }else{
                binding.ivPic.setImageResource(R.drawable.image)
            }

            //Set a click listener to open a selected news on the browser
            itemView.setOnClickListener(){
                clickListener.onItemClickListener(currentItem.link)
            }

            //Set a long click listener to remove a selected news from the favourite
            itemView.setOnLongClickListener(){
                clickListener.onItemLongClickListener((currentItem))
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteNewsViewHolder {
        return FavouriteNewsViewHolder(
            NewsViewBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder:FavouriteNewsViewHolder, position: Int) {
        holder.bind(dataSet[position], clickListener)
    }

    override fun getItemCount(): Int = dataSet.size
}