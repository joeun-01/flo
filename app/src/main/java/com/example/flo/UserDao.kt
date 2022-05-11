package com.example.flo

import androidx.room.*

@Dao
interface UserDao {

    @Insert
    fun insert(user : User)

    @Update
    fun update(user : User)

    @Delete
    fun delete(user: User)

    @Query("SELECT * FROM UserTable")
    fun getUsers() : List<User>

    @Query("SELECT * FROM UserTable WHERE email = :email AND password = :password")
    fun getUser(email : String, password : String) : User?

    @Query("SELECT name FROM UserTable WHERE id = :id")
    fun getUserName(id : Int) : String

}