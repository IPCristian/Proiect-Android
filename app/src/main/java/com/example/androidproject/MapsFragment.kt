package com.example.androidproject

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment() : Fragment() {

    private val lostItemDatabase by lazy { LostItemDatabase.getDatabase(this.requireContext()).itemDao() }
    private val args : MapsFragmentArgs by navArgs()
    private val latitude by lazy { args.latitude.toDouble() }
    private val longitude by lazy { args.longitude.toDouble() }
    private val callback = OnMapReadyCallback { googleMap ->

        googleMap.setOnInfoWindowClickListener {
            val builder = AlertDialog.Builder(this.requireContext())
            builder.setMessage("Did you find the item ?")
                .setCancelable(false)

                .setPositiveButton("Yes")
                { dialog, id ->
                    lostItemDatabase.delete(lostItemDatabase.getItemByID(it.zIndex.toInt()))
                    (activity as HomeActivity).fragmentSwitch(MapsFragment())
                }

                .setNegativeButton("No")
                { dialog, id ->
                    dialog.cancel()
                }

            builder.show()
        }

        googleMap.setInfoWindowAdapter(InfoWindowAdapter(requireContext()))
        for (item in lostItemDatabase.getLostItems())
        {
            googleMap.addMarker(MarkerOptions()
                .position(LatLng(item.latitude.toDouble(), item.longitude.toDouble()))
                .title(item.title)
                .zIndex(item.id.toFloat())
                .snippet(item.description))
        }

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(44.435470,26.102446),10.0f))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}