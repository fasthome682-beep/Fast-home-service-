package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AppScreen
import com.example.ui.FastHomeServiceViewModel
import com.example.ui.components.AppTopBar
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AdminLoginDialog
import com.example.ui.screens.BookingDialog
import com.example.ui.screens.BookingSuccessDialog
import com.example.ui.screens.CustomerHomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.util.CommunicationHelper
import com.example.util.NotificationHelper

class MainActivity : ComponentActivity() {

    private val viewModel: FastHomeServiceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Notification Channels
        NotificationHelper.createNotificationChannel(this)

        if (intent.getBooleanExtra("OPEN_ADMIN", false)) {
            viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD)
        }

        setContent {
            MyApplicationTheme {
                MainContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainContent(viewModel: FastHomeServiceViewModel) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Request notification permission on Android 13+
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { /* Permission result handled */ }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // Collect UI events
    LaunchedEffect(Unit) {
        viewModel.uiEvents.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsStateWithLifecycle()
    val showAdminLoginDialog by viewModel.showAdminLoginDialog.collectAsStateWithLifecycle()
    val adminPinError by viewModel.adminPinError.collectAsStateWithLifecycle()
    val selectedServiceForBooking by viewModel.selectedServiceForBooking.collectAsStateWithLifecycle()
    val lastConfirmedBooking by viewModel.lastConfirmedBooking.collectAsStateWithLifecycle()
    val showSuccessDialog by viewModel.showSuccessDialog.collectAsStateWithLifecycle()
    val allBookings by viewModel.allBookings.collectAsStateWithLifecycle()
    val filteredBookings by viewModel.filteredBookings.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedStatusFilter.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (currentScreen == AppScreen.CUSTOMER_HOME) {
                AppTopBar(
                    currentScreen = currentScreen,
                    isAdminLoggedIn = isAdminLoggedIn,
                    onAdminClick = { viewModel.openAdminLogin() },
                    onLogoutClick = { viewModel.logoutAdmin() },
                    onEmergencyCallClick = { CommunicationHelper.callOwner(context) },
                    onTestBookingClick = { viewModel.performTestBooking(context) }
                )
            }
        }
    ) { innerPadding ->
        when (currentScreen) {
            AppScreen.CUSTOMER_HOME -> {
                CustomerHomeScreen(
                    isAdminLoggedIn = isAdminLoggedIn,
                    onAdminLoginClick = { viewModel.openAdminLogin() },
                    onOpenAdminDashboard = { viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD) },
                    onBookService = { service -> viewModel.openBookingDialog(service) },
                    onTestBooking = { viewModel.performTestBooking(context) },
                    modifier = Modifier.padding(innerPadding)
                )
            }

            AppScreen.ADMIN_DASHBOARD -> {
                AdminDashboardScreen(
                    bookings = filteredBookings,
                    allBookingsCount = allBookings.size,
                    searchQuery = searchQuery,
                    selectedFilter = selectedFilter,
                    onSearchChange = { viewModel.setSearchQuery(it) },
                    onFilterChange = { viewModel.setStatusFilter(it) },
                    onStatusUpdate = { id, status -> viewModel.updateBookingStatus(id, status) },
                    onDeleteBooking = { booking -> viewModel.deleteBooking(booking) },
                    onTestBooking = { viewModel.performTestBooking(context) },
                    onBackToHome = { viewModel.navigateTo(AppScreen.CUSTOMER_HOME) },
                    onLogout = { viewModel.logoutAdmin() },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    // Booking Dialog
    selectedServiceForBooking?.let { service ->
        BookingDialog(
            service = service,
            onDismiss = { viewModel.closeBookingDialog() },
            onConfirm = { name, phone, address, date, timeSlot, notes ->
                viewModel.bookService(
                    customerName = name,
                    customerPhone = phone,
                    customerAddress = address,
                    service = service,
                    date = date,
                    timeSlot = timeSlot,
                    notes = notes,
                    context = context
                )
            }
        )
    }

    // Booking Success Dialog with Owner Notification Triggers (7254894909)
    if (showSuccessDialog && lastConfirmedBooking != null) {
        BookingSuccessDialog(
            booking = lastConfirmedBooking!!,
            onDismiss = { viewModel.closeSuccessDialog() },
            onViewInAdmin = {
                viewModel.closeSuccessDialog()
                if (isAdminLoggedIn) {
                    viewModel.navigateTo(AppScreen.ADMIN_DASHBOARD)
                } else {
                    viewModel.openAdminLogin()
                }
            }
        )
    }

    // Admin Login Dialog
    if (showAdminLoginDialog) {
        AdminLoginDialog(
            errorMessage = adminPinError,
            onDismiss = { viewModel.closeAdminLogin() },
            onLogin = { pin ->
                viewModel.verifyAdminPin(pin)
            }
        )
    }
}
