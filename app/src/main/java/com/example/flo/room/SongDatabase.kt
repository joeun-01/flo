package com.example.flo.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.flo.data.entities.Album
import com.example.flo.data.entities.Like
import com.example.flo.data.entities.Song
import com.example.flo.data.entities.User

@Database(entities = [Song::class, Album::class, User::class, Like::class], version = 1, exportSchema = false)
abstract class SongDatabase : RoomDatabase() {
    abstract fun songDao() : SongDao
    abstract fun albumDao() : AlbumDao
    abstract fun userDao() : UserDao

    companion object {
        private var instance : SongDatabase? = null

        @Synchronized
        fun getInstance(context: Context) : SongDatabase? {
            if(instance == null){
                synchronized(SongDatabase::class){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SongDatabase::class.java,
                        "song-database"
                    ).allowMainThreadQueries().build()
                }
            }
            return instance
        }
    }
}