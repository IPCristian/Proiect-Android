package com.example.androidproject

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class InfoWindowAdapter(mContext : Context) : GoogleMap.InfoWindowAdapter {

    private val lostItemDatabase by lazy { LostItemDatabase.getDatabase(mContext).itemDao() }

    @SuppressLint("InflateParams")
    var mWindow: View = LayoutInflater.from(mContext).inflate(R.layout.markerinfo, null)

    private fun setInfoWindowText(marker: Marker) {
        val tvImage = mWindow.findViewById<ImageView>(R.id.markerImage)
        val tvTitle = mWindow.findViewById<TextView>(R.id.title)
        val tvSnippet = mWindow.findViewById<TextView>(R.id.snippet)

        tvImage.setImageBitmap(lostItemDatabase.getItemByID(marker.zIndex.toInt()).image)
        tvTitle.text = marker.title
        tvSnippet.text = marker.snippet
    }

    override fun getInfoContents(p0: Marker): View? {
        setInfoWindowText(p0)
        return mWindow
    }

    override fun getInfoWindow(p0: Marker): View? {
        setInfoWindowText(p0)
        return mWindow
    }
}