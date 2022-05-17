package com.example.flo

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flo.databinding.ItemLookChartBinding

class ChartRVAdapter(val context: Context, val result: FloChartResult) : RecyclerView.Adapter<ChartRVAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChartRVAdapter.ViewHolder {
        val binding : ItemLookChartBinding = ItemLookChartBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(result.songs[position].coverImgUrl == "" || result.songs[position].coverImgUrl == null) {
            // url 값이 비어있지 않으면
        }
        else {  // url을 넣어줌
            Log.d("image", result.songs[position].coverImgUrl)
            Glide.with(context).load(result.songs[position].coverImgUrl).into(holder.coverImg)
        }

        // 제목, 가수도 적용
        holder.title.text = result.songs[position].title
        holder.singer.text = result.songs[position].singer
    }

    override fun getItemCount(): Int = result.songs.size

    inner class ViewHolder(val binding: ItemLookChartBinding) : RecyclerView.ViewHolder(binding.root) {
        val coverImg : ImageView = binding.itemLookChartCoverImgIv
        val title : TextView = binding.itemLookChartTitleTv
        val singer : TextView = binding.itemLookChartSingerTv
    }

    interface MyItemClickListener {
        fun onRemoveSong(songId : Int)
    }

    private lateinit var albumClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener) {
        albumClickListener = itemClickListener
    }

}