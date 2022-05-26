package com.example.flo.ui.main.album

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.flo.databinding.ItemAlbumBinding
import com.example.flo.remote.AlbumResult
import com.example.flo.remote.Albums

class AlbumRVAdapter(private val context : Context, private val albumList : AlbumResult) : RecyclerView.Adapter<AlbumRVAdapter.ViewHolder>() {
    // data 관리

    interface MyItemClickListener{
        fun onItemClick(album : Albums)  // Album 데이터 값을 넘겨줌
        fun onPlayAlbum(albumID: Int)
//        fun onRemoveAlbum(position: Int)
    }

    private lateinit var mItemClickListener: MyItemClickListener
    fun setMyItemClickListener(itemClickListener : MyItemClickListener){
        mItemClickListener = itemClickListener
    }

//    @SuppressLint("NotifyDataSetChanged")
//    fun addItem(album: Album){
//        albumList.add(album)
//        notifyDataSetChanged()  // data가 바꼈다는 것을 알려줌
//    }
//
//    @SuppressLint("NotifyDataSetChanged")
//    fun removeItem(position: Int){
//        albumList.removeAt(position)
//        notifyDataSetChanged()
//    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding : ItemAlbumBinding = ItemAlbumBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)  // ViewHolder를 생성
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // ViewHolder에 데이터를 binding할 때마다 호출 = 스크롤할 때 굉장히 많이 호출
        // 해당 position에 대한 데이터를 binding
        if(albumList.albums[position].coverImgUrl == "" || albumList.albums[position].coverImgUrl == null) {
            // url 값이 비어있지 않으면
        }
        else {  // url을 넣어줌
            Log.d("image", albumList.albums[position].coverImgUrl)
            Glide.with(context).load(albumList.albums[position].coverImgUrl).into(holder.coverImg)
        }

        // 제목, 가수도 적용
        holder.title.text = albumList.albums[position].title
        holder.singer.text = albumList.albums[position].singer

        // 클릭 이벤트
        holder.itemView.setOnClickListener{ mItemClickListener.onItemClick(albumList.albums[position]) }  // AlbumFragment로 넘어가도록
        holder.binding.itemAlbumPlayImgIv.setOnClickListener{ mItemClickListener.onPlayAlbum(albumList.albums[position].albumIdx) }
    }

    // data set의 크기를 알려줌
    override fun getItemCount(): Int = albumList.albums.size

    inner class ViewHolder(val binding : ItemAlbumBinding) : RecyclerView.ViewHolder(binding.root){
        // ItemView를 잡아주는 ViewHolder
        val title : TextView = binding.itemAlbumTitleTv
        val singer : TextView = binding.itemAlbumSingerTv
        val coverImg : ImageView = binding.itemAlbumCoverImgIv

    }

}