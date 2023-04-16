package com.example.androidproject

import androidx.room.*

@Dao
interface LostItemDAO {
    @Query("SELECT * FROM lostitem")
    fun getLostItems(): List<LostItem>

    @Query("SELECT * FROM lostitem WHERE id = :id")
    fun getItemByID(id: Int): LostItem

    @Query("SELECT * FROM lostitem WHERE latitude = :latitude AND longitude = :longitude")
    fun getItemByCoordinates(latitude: Float, longitude: Float): List<LostItem>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(lostItem: LostItem)

    @Delete
    fun delete(lostItem: LostItem)
}