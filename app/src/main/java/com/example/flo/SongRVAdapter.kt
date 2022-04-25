package com.example.flo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.databinding.ItemSongsBinding

class SongRVAdapter(private val songs : ArrayList<Song>) : RecyclerView.Adapter<SongRVAdapter.ViewHolder>() {
    interface MyItemClickListener{
        fun onItemClick(song: Song) {
            //changeSongFragment(song)
        }
    }

    private lateinit var mItemClickListener: MyItemClickListener
    fun setMyItemClickListener(itemClickListener : MyItemClickListener){
        mItemClickListener = itemClickListener
    }

    fun addItem(song: Song){
        songs.add(song)
        notifyDataSetChanged()  // data가 바꼈다는 것을 알려줌
    }

    fun removeItem(position: Int){
        songs.removeAt(position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): SongRVAdapter.ViewHolder {
        val binding : ItemSongsBinding = ItemSongsBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)  // ViewHolder를 생성
    }

    override fun onBindViewHolder(holder: SongRVAdapter.ViewHolder, position: Int) {
        // ViewHolder에 데이터를 binding할 때마다 호출 = 스크롤할 때 굉장히 많이 호출
        // 해당 position에 대한 데이터를 binding
        holder.bind(songs[position])
        //holder.itemView.setOnClickListener{ mItemClickListener.onItemClick(songs[position]) }  // AlbumFragment로 넘어가도록
        //holder.binding.itemSaveMoreIv.setOnClickListener{ mItemClickListener.onRemoveAlbum(position) }
    }

    // data set의 크기를 알려줌
    override fun getItemCount(): Int = songs.size

    inner class ViewHolder(val binding : ItemSongsBinding) : RecyclerView.ViewHolder(binding.root){
        // ItemView를 잡아주는 ViewHolder
        fun bind(song: Song){
            binding.itemSongsOrderTv.text = song.order
            binding.itemSongsTitleTv.text = song.title
            binding.itemSongsSingerTv.text = song.singer
        }

    }

}