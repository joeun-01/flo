package com.example.flo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.databinding.ItemSaveBinding

class SaveRVAdapter(private val songList : ArrayList<Locker>) : RecyclerView.Adapter<SaveRVAdapter.ViewHolder>() {
    interface MyItemClickListener{
        fun onRemoveAlbum(position: Int)
    }

    private lateinit var mItemClickListener: MyItemClickListener
    fun setMyItemClickListener(itemClickListener : MyItemClickListener){
        mItemClickListener = itemClickListener
    }

    fun addItem(locker: Locker){
        songList.add(locker)
        notifyDataSetChanged()  // data가 바꼈다는 것을 알려줌
    }

    fun removeItem(position: Int){
        songList.removeAt(position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): SaveRVAdapter.ViewHolder {
        val binding : ItemSaveBinding = ItemSaveBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)

        return ViewHolder(binding)  // ViewHolder를 생성
    }

    override fun onBindViewHolder(holder: SaveRVAdapter.ViewHolder, position: Int) {
        // ViewHolder에 데이터를 binding할 때마다 호출 = 스크롤할 때 굉장히 많이 호출
        // 해당 position에 대한 데이터를 binding
        holder.bind(songList[position])
        //holder.itemView.setOnClickListener{ mItemClickListener.onItemClick(songList[position]) }  // AlbumFragment로 넘어가도록
        holder.binding.itemSaveMoreIv.setOnClickListener{ mItemClickListener.onRemoveAlbum(position) }
    }

    // data set의 크기를 알려줌
    override fun getItemCount(): Int = songList.size

    inner class ViewHolder(val binding : ItemSaveBinding) : RecyclerView.ViewHolder(binding.root){
        // ItemView를 잡아주는 ViewHolder
        fun bind(locker: Locker){
            binding.itemSaveTitleTv.text = locker.title
            binding.itemSaveSingerTv.text = locker.singer
            binding.itemSaveCoverImgIv.setImageResource(locker.albumImg!!)
        }

    }

}