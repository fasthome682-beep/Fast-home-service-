package com.example.data

import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class BookingRepository(private val bookingDao: BookingDao) {

    val allBookings: Flow<List<BookingEntity>> = bookingDao.getAllBookings()

    val totalCount: Flow<Int> = bookingDao.getTotalCount()

    suspend fun insertBooking(booking: BookingEntity): Long {
        return bookingDao.insertBooking(booking)
    }

    suspend fun updateStatus(id: Long, status: String) {
        bookingDao.updateStatus(id, status)
    }

    suspend fun markAsNotified(id: Long) {
        bookingDao.markAsNotified(id)
    }

    suspend fun deleteBooking(booking: BookingEntity) {
        bookingDao.deleteBooking(booking)
    }

    fun generateBookingNumber(): String {
        val rand = Random.nextInt(1000, 9999)
        val datePart = SimpleDateFormat("yyMM", Locale.getDefault()).format(Date())
        return "FHS-$datePart-$rand"
    }

    suspend fun createTestBooking(): BookingEntity {
        val testBooking = BookingEntity(
            bookingNumber = generateBookingNumber(),
            customerName = "राजेश कुमार (जाँच ग्राहक)",
            customerPhone = "9876543210",
            customerAddress = "फ्लैट 402, शांति अपार्टमेंट, मेन मार्केट रोड",
            serviceName = "AC Repair & Servicing (एसी सर्विस)",
            serviceCategory = "कूलिंग उपकरण",
            bookingDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
            bookingTimeSlot = "सुबह 10:00 - 12:00",
            estimatedPrice = "₹499",
            status = BookingEntity.STATUS_PENDING,
            notes = "सिस्टम टेस्ट बुकिंग - डेटाबेस व एडमिन पैनल की पुष्टि हेतु",
            createdAt = System.currentTimeMillis()
        )
        val id = insertBooking(testBooking)
        return testBooking.copy(id = id)
    }
}
