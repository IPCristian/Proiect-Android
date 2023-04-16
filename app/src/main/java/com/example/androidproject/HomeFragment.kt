package com.example.androidproject

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.example.androidproject.databinding.FragmentHomeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {
    private lateinit var homeFragmentBinding : FragmentHomeBinding
    private lateinit var preferences: SharedPreferences
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val lostItemDatabase by lazy { LostItemDatabase.getDatabase(this.requireContext()).itemDao() }
    private var image: Bitmap? = null
    private var latitude: Float = 0.0f
    private var longitude: Float = 0.0f

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        preferences =
            requireActivity().getSharedPreferences("CurrentUser", AppCompatActivity.MODE_PRIVATE)
        homeFragmentBinding = FragmentHomeBinding.inflate(layoutInflater)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.requireContext())
        homeFragmentBinding.helloUser.text = "Hello, "+ preferences.getString("name","")
        getCurrentLocation()
        createNotificationChannel()

        homeFragmentBinding.imageButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this.requireContext(),
                    Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            ){
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent,CAMERA_REQUEST_CODE)
            }else{
                ActivityCompat.requestPermissions(
                    this.requireActivity(),
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            }
        }

        homeFragmentBinding.submitLost.setOnClickListener {
            if (homeFragmentBinding.editTextItemName.text.isEmpty() || homeFragmentBinding.editTextItemDescription.text.isEmpty())
            {
                Toast.makeText(this.context,"Please insert both an Item name and an short description.", Toast.LENGTH_SHORT).show()
            }
            else if (image == null)
            {
                Toast.makeText(this.context,"Please take a picture of the approximate location of the lost item.", Toast.LENGTH_SHORT).show()
            }
            else
            {
                getCurrentLocation()
                if (latitude != 0.0f && longitude != 0.0f)
                {
                        val lostItem = LostItem(0,homeFragmentBinding.editTextItemName.text.toString(),
                            homeFragmentBinding.editTextItemDescription.text.toString() + "\n\nSubmitted by "+preferences.getString("name",""),
                            image!!,latitude,longitude)

                        lostItemDatabase.insert(lostItem)
                        Toast.makeText(this.context,"Item added successfully", Toast.LENGTH_SHORT).show()
                        showNotification(lostItem)
                        (activity as HomeActivity).fragmentSwitch(HomeFragment())
                }
            }
        }

        return homeFragmentBinding.root
    }

    private fun showNotification(lostItem : LostItem)
    {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.action = Intent.ACTION_MAIN;
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.putExtra("FragmentName","Maps")
        intent.putExtra("Longitude",lostItem.longitude)
        intent.putExtra("Latitude",lostItem.latitude)

//        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this.requireContext()).run {
//            addNextIntentWithParentStack(intent)
//            getPendingIntent(0,
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
//        }

        val resultPendingIntent = PendingIntent.getActivity(requireContext(),123,intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),PendingIntent.FLAG_IMMUTABLE
                or PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationBuilder = NotificationCompat.Builder(requireContext(),CHANNEL_ID)
        notificationBuilder.setSmallIcon(R.drawable.simple_logo)
        notificationBuilder.setContentText("A new item has been added!")
        notificationBuilder.priority = NotificationCompat.PRIORITY_DEFAULT
        notificationBuilder.setAutoCancel(true)
        notificationBuilder.setContentIntent(resultPendingIntent)

        val notificationManager = NotificationManagerCompat.from(requireContext())
        if (ActivityCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                PERMISSION_REQUEST_NOTIFICATIONS
            )
            return
        }
        notificationManager.notify(SimpleDateFormat("ddHHmmss", Locale.US).format(Date()).toInt(),notificationBuilder.build())
    }

    private fun createNotificationChannel() {

        val name = "My notification"
        val descriptionText = "My description"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager = requireContext().getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun getCurrentLocation()
    {
        if ((ContextCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            && (ContextCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED))
        {
            if (isLocationEnabled())
            {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this.requireActivity()) {task ->
                    val location: Location?= task.result
                    if (location == null)
                    {
                        Toast.makeText(this.context,"Your location couldn't be found, please try again later.", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        latitude = location.latitude.toFloat()
                        longitude = location.longitude.toFloat()
                    }

                }
            }else
            {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }
        else
        {
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSION_REQUEST_ACCESS_LOCATION
            )
        }
    }

    private fun isLocationEnabled():Boolean {
        val locationManager: LocationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent,CAMERA_REQUEST_CODE)
            }
            else
            {
                Toast.makeText(this.context,"Camera permission denied. Allow it in the settings to continue.", Toast.LENGTH_SHORT).show()
            }
        }
        else if (requestCode == PERMISSION_REQUEST_ACCESS_LOCATION)
        {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent,CAMERA_REQUEST_CODE)
            }
            else
            {
                Toast.makeText(this.context,"Location access denied. Allow it in the settings to continue.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == CAMERA_REQUEST_CODE)
            {
                val thumbnail : Bitmap = data!!.extras!!.get("data") as Bitmap
                homeFragmentBinding.imageView.setImageBitmap(thumbnail)
                homeFragmentBinding.imageView.setBackground(resources.getDrawable(R.drawable.customborder2))
                image = thumbnail

            }
        }
    }

    companion object {
        private const val CAMERA_PERMISSION_CODE = 1
        private const val CAMERA_REQUEST_CODE = 2
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 3
        private const val PERMISSION_REQUEST_NOTIFICATIONS = 4
        private const val CHANNEL_ID = "channel1"
    }
}