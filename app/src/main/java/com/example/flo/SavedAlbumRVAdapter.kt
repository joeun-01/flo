package com.example.flo

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.databinding.ItemLockerAlbumBinding
import com.example.flo.databinding.ItemSaveBinding

class SavedAlbumRVAdapter() : RecyclerView.Adapter<SavedAlbumRVAdapter.ViewHolder>() {
    private val albums = ArrayList<Album>()

    interface MyItemClickListener{
        fun onRemoveSong(songId: Int)
    }

    private lateinit var mItemClickListener: MyItemClickListener
    fun setMyItemClickListener(itemClickListener : MyItemClickListener){
        mItemClickListener = itemClickListener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addAlbums(albums: ArrayList<Album>){
        this.albums.clear()
        this.albums.addAll(albums)

        notifyDataSetChanged()  // data가 바꼈다는 것을 알려줌
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeSongs(position: Int){
        albums.removeAt(position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): SavedAlbumRVAdapter.ViewHolder {
        val binding : ItemLockerAlbumBinding = ItemLockerAlbumBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)  // ViewHolder를 생성
    }

    override fun onBindViewHolder(holder: SavedAlbumRVAdapter.ViewHolder, position: Int) {
        // ViewHolder에 데이터를 binding할 때마다 호출 = 스크롤할 때 굉장히 많이 호출
        // 해당 position에 대한 데이터를 binding
        holder.bind(albums[position])
        holder.binding.itemAlbumLockerMoreIv.setOnClickListener {
            mItemClickListener.onRemoveSong(albums[position].id)
            removeSongs(position)
        }
    }

    // data set의 크기를 알려줌
    override fun getItemCount(): Int = albums.size

    inner class ViewHolder(val binding : ItemLockerAlbumBinding) : RecyclerView.ViewHolder(binding.root){
        // ItemView를 잡아주는 ViewHolder
        fun bind(album: Album){
            binding.itemAlbumLockerTitleTv.text = album.title
            binding.itemAlbumLockerSingerTv.text = album.singer
            binding.itemAlbumLockerCoverImgIv.setImageResource(album.coverImg!!)
        }

    }

}