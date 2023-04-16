package com.example.androidproject

import androidx.room.*

@Dao
interface UserDAO {
    @Query("SELECT * FROM user")
    fun getUsers(): List<User>

    @Query("SELECT * FROM user WHERE email LIKE :email")
    fun getUserByEmail(email: String): User

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(user: User)

    @Delete
    fun delete(user: User)
}