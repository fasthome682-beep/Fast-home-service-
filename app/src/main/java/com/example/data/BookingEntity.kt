package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bookingNumber: String, // e.g. FHS-2026-XXXX
    val customerName: String,
    val customerPhone: String,
    val customerAddress: String,
    val serviceName: String,
    val serviceCategory: String,
    val bookingDate: String,
    val bookingTimeSlot: String,
    val estimatedPrice: String = "₹299 onwards",
    val status: String = STATUS_PENDING, // PENDING, CONFIRMED, COMPLETED, CANCELLED
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val notifiedOwner: Boolean = false
) {
    companion object {
        const val STATUS_PENDING = "PENDING"
        const val STATUS_CONFIRMED = "CONFIRMED"
        const val STATUS_COMPLETED = "COMPLETED"
        const val STATUS_CANCELLED = "CANCELLED"
    }
}
