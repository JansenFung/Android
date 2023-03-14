package com.example.project_g08.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.project_g08.models.UserArticle
import com.example.project_g08.databinding.UsersNewsItemBinding
import com.example.project_g08.listeners.OnUserNewsClickListener

class UserNewsAdapter(private val context: Context,
                      private val dataSet: ArrayList<UserArticle>,
                      private val clickListener: OnUserNewsClickListener)
    :RecyclerView.Adapter<UserNewsAdapter.UserNewsViewHolder>(){

    inner class UserNewsViewHolder(var binding: UsersNewsItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(currentItem: UserArticle, clickListener: OnUserNewsClickListener){
            if(currentItem != null){
                binding.tvTitle.text = currentItem.title
                binding.tvDetail.text = currentItem.detail
                binding.tvAuthor.text = "published by ${currentItem.author} on ${currentItem.publishedDate}"

                //Set a long click listener to delete a selected user's post
                itemView.setOnLongClickListener{
                    clickListener.onItemLongClickListener(currentItem)
                    true
                }

                //Set a click listener to edit a selected user's post
                itemView.setOnClickListener{
                    clickListener.onItemClickListener((currentItem))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserNewsViewHolder {
        return UserNewsViewHolder(UsersNewsItemBinding.inflate(LayoutInflater.from(context),parent, false ))
    }

    override fun onBindViewHolder(holder: UserNewsViewHolder, position: Int) {
        holder.bind(dataSet[position], clickListener)
    }

    override fun getItemCount(): Int = dataSet.size
}