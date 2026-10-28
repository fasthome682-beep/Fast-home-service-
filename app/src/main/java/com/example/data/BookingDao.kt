package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooking(booking: BookingEntity): Long

    @Update
    suspend fun updateBooking(booking: BookingEntity)

    @Delete
    suspend fun deleteBooking(booking: BookingEntity)

    @Query("SELECT * FROM bookings ORDER BY createdAt DESC")
    fun getAllBookings(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE status = :status ORDER BY createdAt DESC")
    fun getBookingsByStatus(status: String): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE id = :id LIMIT 1")
    suspend fun getBookingById(id: Long): BookingEntity?

    @Query("SELECT * FROM bookings WHERE bookingNumber = :bookingNumber LIMIT 1")
    suspend fun getBookingByNumber(bookingNumber: String): BookingEntity?

    @Query("SELECT COUNT(*) FROM bookings")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM bookings WHERE status = :status")
    fun getCountByStatus(status: String): Flow<Int>

    @Query("UPDATE bookings SET status = :newStatus WHERE id = :id")
    suspend fun updateStatus(id: Long, newStatus: String)

    @Query("UPDATE bookings SET notifiedOwner = 1 WHERE id = :id")
    suspend fun markAsNotified(id: Long)
}
