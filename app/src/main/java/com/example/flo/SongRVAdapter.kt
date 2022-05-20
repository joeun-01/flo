package com.example.flo

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flo.databinding.ItemSongsBinding

class SongRVAdapter(private val context : Context, private val songs: TrackResult) : RecyclerView.Adapter<SongRVAdapter.ViewHolder>() {
    interface MyItemClickListener{
        fun onPlayAlbum(song : Track)
    }

    private lateinit var mItemClickListener: MyItemClickListener
    fun setMyItemClickListener(itemClickListener : MyItemClickListener){
        mItemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): SongRVAdapter.ViewHolder {
        val binding : ItemSongsBinding = ItemSongsBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)  // ViewHolder를 생성
    }

    override fun onBindViewHolder(holder: SongRVAdapter.ViewHolder, position: Int) {
        // holder.bind(songs!![position])

        // binding
        holder.order.text = position.toString()
        holder.title.text = songs.track[position].title
        holder.singer.text = songs.track[position].singer

        // 클릭 이벤트
        holder.binding.itemSongsPlayIv.setOnClickListener{ mItemClickListener.onPlayAlbum(songs.track[position]) }  // AlbumFragment로 넘어가도록
    }

    // data set의 크기를 알려줌
    override fun getItemCount(): Int = songs.track.size

    inner class ViewHolder(val binding : ItemSongsBinding) : RecyclerView.ViewHolder(binding.root){
        // ItemView를 잡아주는 ViewHolder
//        fun bind(song: Song){
//            binding.itemSongsOrderTv.text = song.number
//            binding.itemSongsTitleTv.text = song.title
//            binding.itemSongsSingerTv.text = song.singer
//        }

        val order : TextView = binding.itemSongsOrderTv
        val title : TextView = binding.itemSongsTitleTv
        val singer : TextView = binding.itemSongsSingerTv

    }

}