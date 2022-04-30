package com.example.flo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.databinding.ItemSongsBinding

class SongRVAdapter(private val songs: ArrayList<Song>?) : RecyclerView.Adapter<SongRVAdapter.ViewHolder>() {
    interface MyItemClickListener{
        fun onPlayAlbum(song : Song)
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
        holder.bind(songs!![position])
        holder.binding.itemSongsPlayIv.setOnClickListener{ mItemClickListener.onPlayAlbum(songs[position]) }  // AlbumFragment로 넘어가도록
    }

    // data set의 크기를 알려줌
    override fun getItemCount(): Int = songs!!.size

    inner class ViewHolder(val binding : ItemSongsBinding) : RecyclerView.ViewHolder(binding.root){
        // ItemView를 잡아주는 ViewHolder
        fun bind(song: Song){
            binding.itemSongsOrderTv.text = song.order
            binding.itemSongsTitleTv.text = song.title
            binding.itemSongsSingerTv.text = song.singer
        }

    }

}