package com.example.ui.screens

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BookingEntity
import com.example.util.CommunicationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    bookings: List<BookingEntity>,
    allBookingsCount: Int,
    searchQuery: String,
    selectedFilter: String,
    onSearchChange: (String) -> Unit,
    onFilterChange: (String) -> Unit,
    onStatusUpdate: (Long, String) -> Unit,
    onDeleteBooking: (BookingEntity) -> Unit,
    onTestBooking: () -> Unit,
    onBackToHome: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    BackHandler { onBackToHome() }

    val pendingCount = remember(bookings) { bookings.count { it.status == BookingEntity.STATUS_PENDING } }
    val confirmedCount = remember(bookings) { bookings.count { it.status == BookingEntity.STATUS_CONFIRMED } }
    val completedCount = remember(bookings) { bookings.count { it.status == BookingEntity.STATUS_COMPLETED } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "एडमिन डैशबोर्ड",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = "मालिक संपर्क: ${CommunicationHelper.OWNER_PHONE}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackToHome,
                        modifier = Modifier.testTag("admin_back_to_home_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "होम स्क्रीन पर वापस जाएं",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    Button(
                        onClick = onTestBooking,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("admin_test_booking_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "जाँच बुकिंग",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onTestBooking,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("admin_fab_test_booking")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "टेस्ट बुकिंग")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("नई टेस्ट बुकिंग", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .testTag("admin_bookings_list"),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Live Owner Alert Banner
            item {
                OwnerNotificationBanner(context = context)
            }

            // Stat Cards
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        title = "कुल बुकिंग",
                        count = allBookingsCount.toString(),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "पेंडिंग",
                        count = pendingCount.toString(),
                        color = Color(0xFFEA580C),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "कन्फर्म",
                        count = confirmedCount.toString(),
                        color = Color(0xFF0284C7),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "पूर्ण",
                        count = completedCount.toString(),
                        color = Color(0xFF16A34A),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .testTag("admin_search_input"),
                    placeholder = { Text("ग्राहक का नाम, मोबाइल, पता या बुकिंग नं खोजें...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "हटाएं")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Status Filter Chips
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filters = listOf(
                        Pair("ALL", "सभी बुकिंग (${bookings.size})"),
                        Pair(BookingEntity.STATUS_PENDING, "पेंडिंग"),
                        Pair(BookingEntity.STATUS_CONFIRMED, "कन्फर्म"),
                        Pair(BookingEntity.STATUS_COMPLETED, "पूर्ण"),
                        Pair(BookingEntity.STATUS_CANCELLED, "रद्द")
                    )
                    items(filters) { (key, label) ->
                        FilterChip(
                            selected = selectedFilter == key,
                            onClick = { onFilterChange(key) },
                            label = { Text(label, fontSize = 12.sp) },
                            modifier = Modifier.testTag("filter_chip_$key"),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Bookings Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "बुकिंग सूची (${bookings.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "डेटाबेस से लाइव सिंक ✅",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = Color(0xFF16A34A)
                    )
                }
            }

            // If empty
            if (bookings.isEmpty()) {
                item {
                    EmptyBookingsView(
                        onTestBooking = onTestBooking,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                items(items = bookings, key = { it.id }) { booking ->
                    BookingAdminCard(
                        booking = booking,
                        context = context,
                        onStatusUpdate = { newStatus -> onStatusUpdate(booking.id, newStatus) },
                        onDeleteBooking = { onDeleteBooking(booking) },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
fun OwnerNotificationBanner(context: Context) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("owner_notification_banner"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFEFF6FF)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3B82F6)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "मालिक अलर्ट नंबर: 7254894909",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E3A8A)
                        )
                    )
                    Text(
                        text = "हर नई बुकिंग पर SMS व ऐप नोटिफिकेशन भेजी जाती है",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = Color(0xFF475569)
                    )
                }
            }

            IconButton(
                onClick = { CommunicationHelper.callOwner(context) },
                modifier = Modifier.testTag("call_owner_banner_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "मालिक को कॉल करें",
                    tint = Color(0xFF1E3A8A)
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    count: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = color
                )
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun BookingAdminCard(
    booking: BookingEntity,
    context: Context,
    onStatusUpdate: (String) -> Unit,
    onDeleteBooking: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showStatusMenu by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val statusColor = when (booking.status) {
        BookingEntity.STATUS_PENDING -> Color(0xFFEA580C)
        BookingEntity.STATUS_CONFIRMED -> Color(0xFF0284C7)
        BookingEntity.STATUS_COMPLETED -> Color(0xFF16A34A)
        BookingEntity.STATUS_CANCELLED -> Color(0xFFDC2626)
        else -> MaterialTheme.colorScheme.primary
    }

    val statusHindi = when (booking.status) {
        BookingEntity.STATUS_PENDING -> "पेंडिंग (नई बुकिंग)"
        BookingEntity.STATUS_CONFIRMED -> "स्वीकृत (कन्फर्म)"
        BookingEntity.STATUS_COMPLETED -> "सफलतापूर्वक पूर्ण"
        BookingEntity.STATUS_CANCELLED -> "रद्द"
        else -> booking.status
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("booking_card_${booking.bookingNumber}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top Bar of Card: Booking Number & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "#${booking.bookingNumber}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Box {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = statusColor.copy(alpha = 0.15f),
                        modifier = Modifier.clickable { showStatusMenu = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(statusColor)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = statusHindi,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = statusColor
                                )
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showStatusMenu,
                        onDismissRequest = { showStatusMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("पेंडिंग (PENDING)") },
                            onClick = {
                                onStatusUpdate(BookingEntity.STATUS_PENDING)
                                showStatusMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("कन्फर्म करें (CONFIRMED)") },
                            onClick = {
                                onStatusUpdate(BookingEntity.STATUS_CONFIRMED)
                                showStatusMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("पूर्ण चिह्नित करें (COMPLETED)") },
                            onClick = {
                                onStatusUpdate(BookingEntity.STATUS_COMPLETED)
                                showStatusMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("रद्द करें (CANCELLED)") },
                            onClick = {
                                onStatusUpdate(BookingEntity.STATUS_CANCELLED)
                                showStatusMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Customer Name & Mobile
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = booking.customerName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Row {
                    IconButton(
                        onClick = { CommunicationHelper.callCustomer(context, booking.customerPhone) },
                        modifier = Modifier.size(34.dp).testTag("call_customer_${booking.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "ग्राहक को कॉल करें",
                            tint = Color(0xFF16A34A),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = { CommunicationHelper.sendWhatsAppToCustomer(context, booking) },
                        modifier = Modifier.size(34.dp).testTag("whatsapp_customer_${booking.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "ग्राहक को WhatsApp भेजें",
                            tint = Color(0xFF25D366),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Customer Phone Number
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = booking.customerPhone,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Service details
            Text(
                text = "🔧 सर्विस: ${booking.serviceName}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary
            )

            // Date & Time
            Row(
                modifier = Modifier.padding(top = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${booking.bookingDate} • ${booking.bookingTimeSlot}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Address
            Row(
                modifier = Modifier.padding(top = 4.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp).padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = booking.customerAddress,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (booking.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "📝 नोट: ${booking.notes}",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Share to owner / team (7254894909)
                OutlinedButton(
                    onClick = { CommunicationHelper.shareBookingDetails(context, booking) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("share_booking_details_${booking.id}"),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "मालिक को शेयर करें",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Delete button
                IconButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier.testTag("delete_booking_button_${booking.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "हटाएं",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("बुकिंग हटाएं?") },
            text = { Text("क्या आप वास्तव में बुकिंग #${booking.bookingNumber} को डेटाबेस से हटाना चाहते हैं?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteBooking()
                        showDeleteConfirm = false
                    }
                ) {
                    Text("हाँ, हटाएं", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("रद्द करें")
                }
            }
        )
    }
}

@Composable
fun EmptyBookingsView(
    onTestBooking: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.BusinessCenter,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "अभी कोई बुकिंग नहीं मिली",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "जैसे ही कोई ग्राहक सर्विस बुक करेगा, वह तुरंत यहाँ लाइव दिखाई देगी।",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onTestBooking,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("empty_state_test_booking_button")
            ) {
                Text("एक टेस्ट बुकिंग बनाकर जाँचें", fontWeight = FontWeight.Bold)
            }
        }
    }
}
