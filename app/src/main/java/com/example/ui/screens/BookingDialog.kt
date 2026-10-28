package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.ServiceItem
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun BookingDialog(
    service: ServiceItem,
    onDismiss: () -> Unit,
    onConfirm: (
        name: String,
        phone: String,
        address: String,
        date: String,
        timeSlot: String,
        notes: String
    ) -> Unit
) {
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var customerAddress by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val today = remember { dateFormat.format(Date()) }
    val tomorrow = remember {
        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
        dateFormat.format(cal.time)
    }
    val dayAfter = remember {
        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 2) }
        dateFormat.format(cal.time)
    }

    var selectedDate by remember { mutableStateOf(today) }

    val timeSlots = listOf(
        "सुबह 09:00 - 12:00",
        "दोपहर 12:00 - 03:00",
        "शाम 03:00 - 07:00"
    )
    var selectedTimeSlot by remember { mutableStateOf(timeSlots[0]) }

    var nameError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }
    var addressError by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 16.dp)
                .testTag("booking_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "सर्विस बुक करें",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold
                            )
                        )
                        Text(
                            text = "फास्ट होम सर्विस • त्वरित रिस्पांस",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_booking_dialog")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "बंद करें")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Service Preview Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = service.icon,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = service.nameHi,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = service.nameEn,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        Text(
                            text = service.basePrice,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 1. Customer Name
                OutlinedTextField(
                    value = customerName,
                    onValueChange = {
                        customerName = it
                        nameError = it.isBlank()
                    },
                    label = { Text("ग्राहक का नाम *") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null)
                    },
                    isError = nameError,
                    supportingText = {
                        if (nameError) Text("कृपया ग्राहक का नाम दर्ज करें", color = MaterialTheme.colorScheme.error)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("booking_input_name"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 2. Customer Phone
                OutlinedTextField(
                    value = customerPhone,
                    onValueChange = {
                        customerPhone = it.filter { char -> char.isDigit() }.take(10)
                        phoneError = customerPhone.length < 10
                    },
                    label = { Text("मोबाइल नंबर (10 अंक) *") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    isError = phoneError,
                    supportingText = {
                        if (phoneError) Text("कृपया मान्य 10-अंकों का मोबाइल नंबर दर्ज करें", color = MaterialTheme.colorScheme.error)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("booking_input_phone"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 3. Customer Address
                OutlinedTextField(
                    value = customerAddress,
                    onValueChange = {
                        customerAddress = it
                        addressError = it.isBlank()
                    },
                    label = { Text("घर का पूरा पता व लैंडमार्क *") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Home, contentDescription = null)
                    },
                    isError = addressError,
                    supportingText = {
                        if (addressError) Text("कृपया पूरा पता दर्ज करें ताकि तकनीशियन पहुँच सके", color = MaterialTheme.colorScheme.error)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("booking_input_address"),
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 4. Booking Date Selection
                Text(
                    text = "सर्विस की तारीख चुनें",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val dateOptions = listOf(
                        Pair("आज", today),
                        Pair("कल", tomorrow),
                        Pair("परसों", dayAfter)
                    )
                    dateOptions.forEach { (label, dateVal) ->
                        val isSelected = selectedDate == dateVal
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedDate = dateVal },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = label,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = dateVal.take(6),
                                    fontSize = 10.sp,
                                    color = if (isSelected) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 5. Time Slot Selection
                Text(
                    text = "पसंदीदा समय (Time Slot)",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    timeSlots.forEach { slot ->
                        val isSelected = selectedTimeSlot == slot
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedTimeSlot = slot },
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = slot,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Notes / Instructions
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("कोई विशेष समस्या या निर्देश (वैकल्पिक)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("booking_input_notes"),
                    maxLines = 2,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Confirm Booking Button
                Button(
                    onClick = {
                        val isNameValid = customerName.isNotBlank()
                        val isPhoneValid = customerPhone.length == 10
                        val isAddressValid = customerAddress.isNotBlank()

                        nameError = !isNameValid
                        phoneError = !isPhoneValid
                        addressError = !isAddressValid

                        if (isNameValid && isPhoneValid && isAddressValid) {
                            onConfirm(
                                customerName,
                                customerPhone,
                                customerAddress,
                                selectedDate,
                                selectedTimeSlot,
                                notes
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("confirm_booking_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "बुकिंग कन्फर्म करें (Save in DB)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
