package com.example.ics342app

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyLocationService : Service() {

    private lateinit var fusedClient: FusedLocationProviderClient
    private var isForegroundStarted = false

    override fun onCreate() {
        super.onCreate()
        fusedClient = LocationServices.getFusedLocationProviderClient(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        getLocationAndNotify()
        return START_STICKY
    }

    private fun getLocationAndNotify() {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            Log.d("SERVICE", "Permission not granted")
            return
        }

        Log.d("SERVICE", "✅ Permission granted — requesting location...")
        fusedClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val lat = location.latitude
                val lon = location.longitude
                Log.d("SERVICE", "📍 Got location: Lat=$lat, Lon=$lon")
                fetchWeatherAndNotify(lat, lon)
            } else {
                showNotification("SkyCast", "Location unavailable.")
            }
        }
    }

    private fun fetchWeatherAndNotify(lat: Double, lon: Double) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(WeatherByLocationApi::class.java)

        val call = service.getWeatherByLocation(
            lat = lat,
            lon = lon,
            units = "imperial",
            apiKey = API_KEY
        )

        call.enqueue(object : Callback<WeatherByLocationResponse> {
            override fun onResponse(
                call: Call<WeatherByLocationResponse>,
                response: Response<WeatherByLocationResponse>
            ) {
                if (response.isSuccessful) {
                    val weather = response.body()
                    val temp = weather?.main?.temp?.toInt()
                    val city = weather?.name ?: "Unknown"
                    if (temp != null) {
                        Log.d("SERVICE", "✅ Weather: $city - $temp°F")
                        showNotification("SkyCast", "$city: $temp°F")
                    }
                } else {
                    showNotification("SkyCast", "Weather unavailable")
                }
            }

            override fun onFailure(call: Call<WeatherByLocationResponse>, t: Throwable) {
                showNotification("SkyCast", "Failed: ${t.localizedMessage}")
            }
        })
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "weather_channel"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setAutoCancel(false)
            .setOngoing(true)
            .build()

        if (!isForegroundStarted) {
            Log.d("SERVICE", "🚀 Starting foreground service")
            startForeground(1, notification)
            isForegroundStarted = true
        } else {
            Log.d("SERVICE", "🔄 Updating existing notification")
            notificationManager.notify(1, notification)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "weather_channel",
                "Weather Updates",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val API_KEY = "3d4e715a4658b370c46c5dfb1b11ce7e" // replace with your key if needed
    }
}