package com.example.androidproject

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val mTitle : TextView = itemView.findViewById(R.id.recycle_title)
    private val mDescription : TextView = itemView.findViewById(R.id.recycle_description)
    private val mPicture : ImageView = itemView.findViewById(R.id.recycle_picture)


    fun bind(lostItem : LostItem)
    {
        val title = lostItem.title
        val picture = lostItem.image
        val description = lostItem.description

        mPicture.setImageBitmap(picture)
        mDescription.text = description
        mTitle.text = title
    }
}