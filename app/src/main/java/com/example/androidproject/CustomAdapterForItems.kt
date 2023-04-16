package com.example.androidproject

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

class CustomAdapterForItems(mContext : Context) : RecyclerView.Adapter<ViewHolder>() {

    private val lostItemDatabase by lazy { LostItemDatabase.getDatabase(mContext).itemDao() }
    private var allItems = lostItemDatabase.getLostItems()
    private val context = mContext

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.recycleitem, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(allItems[position])
        holder.itemView.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Check out this lost item! It's coordinates are: "+allItems[position].longitude+" longitude, "+allItems[position].latitude+" latitude.")
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(context,shareIntent, null)
        }
        // Log.e("ABBABAB",allItems.size.toString())
    }

    override fun getItemCount(): Int {
        return allItems.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setAllItems(items : List<LostItem>) {
        allItems  = items
        notifyDataSetChanged()
    }
}