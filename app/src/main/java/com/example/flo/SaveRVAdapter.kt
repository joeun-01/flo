package com.example.flo

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.databinding.ItemSaveBinding

class SaveRVAdapter() : RecyclerView.Adapter<SaveRVAdapter.ViewHolder>() {
    private val songs = ArrayList<Song>()

    interface MyItemClickListener{
        fun onRemoveSong(songId: Int)
    }

    private lateinit var mItemClickListener: MyItemClickListener
    fun setMyItemClickListener(itemClickListener : MyItemClickListener){
        mItemClickListener = itemClickListener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addSongs(songs: ArrayList<Song>){
        this.songs.clear()
        this.songs.addAll(songs)

        notifyDataSetChanged()  // data가 바꼈다는 것을 알려줌
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeSongs(position: Int){
        songs.removeAt(position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): SaveRVAdapter.ViewHolder {
        val binding : ItemSaveBinding = ItemSaveBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)  // ViewHolder를 생성
    }

    override fun onBindViewHolder(holder: SaveRVAdapter.ViewHolder, position: Int) {
        // ViewHolder에 데이터를 binding할 때마다 호출 = 스크롤할 때 굉장히 많이 호출
        // 해당 position에 대한 데이터를 binding
        holder.bind(songs[position])
        holder.binding.itemSaveMoreIv.setOnClickListener {
            mItemClickListener.onRemoveSong(songs[position].id)
            removeSongs(position)
        }
    }

    // data set의 크기를 알려줌
    override fun getItemCount(): Int = songs.size

    inner class ViewHolder(val binding : ItemSaveBinding) : RecyclerView.ViewHolder(binding.root){
        // ItemView를 잡아주는 ViewHolder
        fun bind(song: Song){
            binding.itemSaveTitleTv.text = song.title
            binding.itemSaveSingerTv.text = song.singer
            binding.itemSaveCoverImgIv.setImageResource(song.albumImg!!)
        }

    }

}