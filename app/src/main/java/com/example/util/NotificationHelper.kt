package com.example.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.data.BookingEntity

object NotificationHelper {
    private const val CHANNEL_ID = "fast_home_service_bookings"
    private const val CHANNEL_NAME = "New Service Bookings"
    private const val CHANNEL_DESC = "Notifications for newly booked home services"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 200, 300)
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showBookingNotification(context: Context, booking: BookingEntity) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("OPEN_ADMIN", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            booking.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0)
        )

        val title = "🔔 नई बुकिंग प्राप्त! #${booking.bookingNumber}"
        val body = "ग्राहक: ${booking.customerName} (${booking.customerPhone}) | सर्विस: ${booking.serviceName} | तारीख: ${booking.bookingDate}"

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(
                "बुकिंग नं: ${booking.bookingNumber}\n" +
                "ग्राहक का नाम: ${booking.customerName}\n" +
                "मोबाइल: ${booking.customerPhone}\n" +
                "पता: ${booking.customerAddress}\n" +
                "सर्विस: ${booking.serviceName}\n" +
                "तारीख: ${booking.bookingDate} (${booking.bookingTimeSlot})\n" +
                "मालिक संपर्क: 7254894909"
            ))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = (booking.id.takeIf { it > 0 } ?: System.currentTimeMillis()).toInt()
        notificationManager.notify(notificationId, builder.build())
    }
}
