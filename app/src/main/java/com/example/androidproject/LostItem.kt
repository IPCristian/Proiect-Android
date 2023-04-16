package com.example.androidproject

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LostItem(
    @PrimaryKey(autoGenerate = true) var id: Int,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "image") var image: Bitmap,
    @ColumnInfo(name = "latitude") var latitude : Float,
    @ColumnInfo(name = "longitude") var longitude : Float
)