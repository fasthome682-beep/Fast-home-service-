package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.BookingDao
import com.example.data.BookingEntity
import com.example.data.BookingRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var database: AppDatabase
    private lateinit var bookingDao: BookingDao
    private lateinit var repository: BookingRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        bookingDao = database.bookingDao()
        repository = BookingRepository(bookingDao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("FAST HOME SERVICE", appName)
    }

    @Test
    fun `test booking insertion and retrieval from database`() = runBlocking {
        val testBooking = BookingEntity(
            bookingNumber = "FHS-2026-TEST",
            customerName = "अनिल कुमार",
            customerPhone = "7254894909",
            customerAddress = "मकान नं 12, गांधी नगर",
            serviceName = "AC Repair & Servicing",
            serviceCategory = "कूलिंग उपकरण",
            bookingDate = "28 Oct 2026",
            bookingTimeSlot = "सुबह 10:00 - 12:00",
            status = BookingEntity.STATUS_PENDING
        )

        val id = bookingDao.insertBooking(testBooking)
        assertTrue(id > 0)

        val retrieved = bookingDao.getBookingById(id)
        assertNotNull(retrieved)
        assertEquals("FHS-2026-TEST", retrieved?.bookingNumber)
        assertEquals("अनिल कुमार", retrieved?.customerName)
        assertEquals("7254894909", retrieved?.customerPhone)
        assertEquals(BookingEntity.STATUS_PENDING, retrieved?.status)

        // Test status update
        bookingDao.updateStatus(id, BookingEntity.STATUS_CONFIRMED)
        val updated = bookingDao.getBookingById(id)
        assertEquals(BookingEntity.STATUS_CONFIRMED, updated?.status)

        // Test list stream
        val list = bookingDao.getAllBookings().first()
        assertEquals(1, list.size)
    }

    @Test
    fun `test createTestBooking creates valid booking in repository`() = runBlocking {
        val booking = repository.createTestBooking()
        assertNotNull(booking)
        assertTrue(booking.id > 0)
        assertTrue(booking.bookingNumber.startsWith("FHS-"))
        val all = repository.allBookings.first()
        assertTrue(all.isNotEmpty())
    }
}
