package com.example.project_g08.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.project_g08.R
import com.example.project_g08.models.Results
import com.example.project_g08.databinding.NewsViewBinding
import com.example.project_g08.listeners.OnNewsClickListener

class NewsAdapter(private val context: Context,
                  private val NewsList:ArrayList<Results>,
                  private val clickListener: OnNewsClickListener)
    : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>(){

    inner class NewsViewHolder(var binding: NewsViewBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(currentNews: Results, clickListener: OnNewsClickListener){
            binding.tvTitle.setText(currentNews.title)

            if(currentNews.image_url!=null){
                Glide.with(binding.tvTitle.context).load(currentNews.image_url).into(binding.ivPic)
            }else{
                binding.ivPic.setImageResource(R.drawable.image)
            }

            //Set a click listener to open a selected news on the browser
            itemView.setOnClickListener(){
                clickListener.onItemClickListener(currentNews.link)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {

        return NewsViewHolder(NewsViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(NewsList[position], clickListener)
    }

    override fun getItemCount(): Int {
        return NewsList.size
    }
}