package com.example.androidproject

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @ColumnInfo(name = "username") var name: String,
    @PrimaryKey @ColumnInfo(name = "email") var email: String,
    @ColumnInfo(name = "password") var password: String,
)