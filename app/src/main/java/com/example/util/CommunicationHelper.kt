package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.data.BookingEntity
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object CommunicationHelper {
    const val OWNER_PHONE = "7254894909"
    const val OWNER_PHONE_INTERNATIONAL = "+917254894909"

    fun formatBookingMessageForOwner(booking: BookingEntity): String {
        return """
            🚨 नई सर्विस बुकिंग - FAST HOME SERVICE 🚨
            ━━━━━━━━━━━━━━━━━━━━
            📋 बुकिंग आईडी: #${booking.bookingNumber}
            👤 ग्राहक का नाम: ${booking.customerName}
            📞 मोबाइल नंबर: ${booking.customerPhone}
            🏠 पता: ${booking.customerAddress}
            🔧 सर्विस: ${booking.serviceName}
            📅 तारीख: ${booking.bookingDate}
            ⏰ समय: ${booking.bookingTimeSlot}
            💰 अनुमानित शुल्क: ${booking.estimatedPrice}
            📝 टिप्पणी: ${if (booking.notes.isNotBlank()) booking.notes else "कोई विशेष निर्देश नहीं"}
            ━━━━━━━━━━━━━━━━━━━━
            कृपया तुरंत ग्राहक से संपर्क कर बुकिंग कन्फर्म करें!
        """.trimIndent()
    }

    fun sendSmsToOwner(context: Context, booking: BookingEntity) {
        try {
            val message = formatBookingMessageForOwner(booking)
            val uri = Uri.parse("smsto:$OWNER_PHONE")
            val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
                putExtra("sms_body", message)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "SMS ऐप खोलने में असमर्थ: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendWhatsAppToOwner(context: Context, booking: BookingEntity) {
        try {
            val message = formatBookingMessageForOwner(booking)
            val encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.toString())
            val url = "https://api.whatsapp.com/send?phone=91$OWNER_PHONE&text=$encodedMessage"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "WhatsApp खोलने में असमर्थ: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun callOwner(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$OWNER_PHONE")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "कॉल करने में असमर्थ: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun callCustomer(context: Context, phone: String) {
        try {
            val cleanPhone = phone.replace(Regex("[^0-9+]"), "")
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanPhone")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "कॉल करने में असमर्थ: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendWhatsAppToCustomer(context: Context, booking: BookingEntity) {
        try {
            val cleanPhone = booking.customerPhone.replace(Regex("[^0-9]"), "")
            val phoneFormatted = if (cleanPhone.length == 10) "91$cleanPhone" else cleanPhone
            val text = """
                नमस्ते ${booking.customerName} जी,
                FAST HOME SERVICE में आपकी बुकिंग #${booking.bookingNumber} प्राप्त हो गई है।
                सर्विस: ${booking.serviceName}
                तारीख: ${booking.bookingDate} (${booking.bookingTimeSlot})
                हमारे सर्विस एजेंट जल्द ही आपसे संपर्क करेंगे।
                हेल्पलाइन: $OWNER_PHONE
            """.trimIndent()
            val encoded = URLEncoder.encode(text, StandardCharsets.UTF_8.toString())
            val url = "https://api.whatsapp.com/send?phone=$phoneFormatted&text=$encoded"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "WhatsApp खोलने में असमर्थ: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareBookingDetails(context: Context, booking: BookingEntity) {
        try {
            val message = formatBookingMessageForOwner(booking)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "FAST HOME SERVICE - नई बुकिंग #${booking.bookingNumber}")
                putExtra(Intent.EXTRA_TEXT, message)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(Intent.createChooser(intent, "बुकिंग विवरण साझा करें"))
        } catch (e: Exception) {
            Toast.makeText(context, "साझा करने में असमर्थ: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
