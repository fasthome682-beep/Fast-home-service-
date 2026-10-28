package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.BookingEntity
import com.example.data.BookingRepository
import com.example.data.ServiceItem
import com.example.util.NotificationHelper
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    CUSTOMER_HOME,
    ADMIN_DASHBOARD
}

class FastHomeServiceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BookingRepository
    val allBookings: StateFlow<List<BookingEntity>>

    init {
        val bookingDao = AppDatabase.getDatabase(application).bookingDao()
        repository = BookingRepository(bookingDao)
        allBookings = repository.allBookings.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    }

    private val _currentScreen = MutableStateFlow(AppScreen.CUSTOMER_HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _isAdminLoggedIn = MutableStateFlow(false)
    val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

    private val _showAdminLoginDialog = MutableStateFlow(false)
    val showAdminLoginDialog: StateFlow<Boolean> = _showAdminLoginDialog.asStateFlow()

    private val _adminPinError = MutableStateFlow<String?>(null)
    val adminPinError: StateFlow<String?> = _adminPinError.asStateFlow()

    private val _selectedServiceForBooking = MutableStateFlow<ServiceItem?>(null)
    val selectedServiceForBooking: StateFlow<ServiceItem?> = _selectedServiceForBooking.asStateFlow()

    private val _lastConfirmedBooking = MutableStateFlow<BookingEntity?>(null)
    val lastConfirmedBooking: StateFlow<BookingEntity?> = _lastConfirmedBooking.asStateFlow()

    private val _showSuccessDialog = MutableStateFlow(false)
    val showSuccessDialog: StateFlow<Boolean> = _showSuccessDialog.asStateFlow()

    private val _selectedStatusFilter = MutableStateFlow("ALL")
    val selectedStatusFilter: StateFlow<String> = _selectedStatusFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _uiEvents = MutableSharedFlow<String>()
    val uiEvents = _uiEvents.asSharedFlow()

    // Filtered bookings for Admin
    val filteredBookings: StateFlow<List<BookingEntity>> = combine(
        allBookings,
        _selectedStatusFilter,
        _searchQuery
    ) { bookings, filter, query ->
        bookings.filter { booking ->
            val matchesFilter = when (filter) {
                "ALL" -> true
                else -> booking.status.equals(filter, ignoreCase = true)
            }
            val matchesQuery = if (query.isBlank()) {
                true
            } else {
                val q = query.trim().lowercase()
                booking.customerName.lowercase().contains(q) ||
                    booking.customerPhone.contains(q) ||
                    booking.customerAddress.lowercase().contains(q) ||
                    booking.bookingNumber.lowercase().contains(q) ||
                    booking.serviceName.lowercase().contains(q)
            }
            matchesFilter && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin default PIN is 7254 (first 4 digits of owner phone 7254894909)
    fun verifyAdminPin(pin: String): Boolean {
        return if (pin.trim() == "7254" || pin.trim() == "7254894909" || pin.trim() == "1234") {
            _isAdminLoggedIn.value = true
            _showAdminLoginDialog.value = false
            _adminPinError.value = null
            _currentScreen.value = AppScreen.ADMIN_DASHBOARD
            viewModelScope.launch {
                _uiEvents.emit("एडमिन लॉगिन सफल! स्वागत है।")
            }
            true
        } else {
            _adminPinError.value = "गलत सुरक्षा पिन! कृपया 7254 दर्ज करें।"
            false
        }
    }

    fun openAdminLogin() {
        _adminPinError.value = null
        _showAdminLoginDialog.value = true
    }

    fun closeAdminLogin() {
        _showAdminLoginDialog.value = false
        _adminPinError.value = null
    }

    fun logoutAdmin() {
        _isAdminLoggedIn.value = false
        _currentScreen.value = AppScreen.CUSTOMER_HOME
        viewModelScope.launch {
            _uiEvents.emit("एडमिन लॉगआउट हो गया।")
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun openBookingDialog(service: ServiceItem) {
        _selectedServiceForBooking.value = service
    }

    fun closeBookingDialog() {
        _selectedServiceForBooking.value = null
    }

    fun closeSuccessDialog() {
        _showSuccessDialog.value = false
    }

    fun setStatusFilter(filter: String) {
        _selectedStatusFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun bookService(
        customerName: String,
        customerPhone: String,
        customerAddress: String,
        service: ServiceItem,
        date: String,
        timeSlot: String,
        notes: String,
        context: Context
    ) {
        viewModelScope.launch {
            val bookingNumber = repository.generateBookingNumber()
            val newBooking = BookingEntity(
                bookingNumber = bookingNumber,
                customerName = customerName.trim(),
                customerPhone = customerPhone.trim(),
                customerAddress = customerAddress.trim(),
                serviceName = "${service.nameEn} (${service.nameHi})",
                serviceCategory = service.category,
                bookingDate = date,
                bookingTimeSlot = timeSlot,
                estimatedPrice = service.basePrice,
                status = BookingEntity.STATUS_PENDING,
                notes = notes.trim(),
                createdAt = System.currentTimeMillis()
            )

            val insertedId = repository.insertBooking(newBooking)
            val completeBooking = newBooking.copy(id = insertedId)

            _lastConfirmedBooking.value = completeBooking
            _selectedServiceForBooking.value = null
            _showSuccessDialog.value = true

            // Send System Notification to business owner / device
            NotificationHelper.showBookingNotification(context, completeBooking)
            _uiEvents.emit("बुकिंग सफल! आईडी: #${completeBooking.bookingNumber} डेटाबेस में सुरक्षित सेव हुई।")
        }
    }

    fun performTestBooking(context: Context) {
        viewModelScope.launch {
            val testBooking = repository.createTestBooking()
            NotificationHelper.showBookingNotification(context, testBooking)
            _lastConfirmedBooking.value = testBooking
            _uiEvents.emit("✅ टेस्ट बुकिंग सफल! #${testBooking.bookingNumber} डेटाबेस और एडमिन में दर्ज हुई।")
        }
    }

    fun updateBookingStatus(bookingId: Long, newStatus: String) {
        viewModelScope.launch {
            repository.updateStatus(bookingId, newStatus)
            _uiEvents.emit("बुकिंग स्थिति अपडेट: $newStatus")
        }
    }

    fun deleteBooking(booking: BookingEntity) {
        viewModelScope.launch {
            repository.deleteBooking(booking)
            _uiEvents.emit("बुकिंग #${booking.bookingNumber} हटा दी गई।")
        }
    }
}
