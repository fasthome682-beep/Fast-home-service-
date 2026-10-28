package com.example.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector

data class ServiceItem(
    val id: String,
    val nameEn: String,
    val nameHi: String,
    val category: String,
    val icon: ImageVector,
    val basePrice: String,
    val description: String,
    val popular: Boolean = false
)

object ServiceCatalog {
    val services = listOf(
        ServiceItem(
            id = "ac_service",
            nameEn = "AC Repair & Servicing",
            nameHi = "एसी सर्विस एवं गैस रीफिल",
            category = "कूलिंग उपकरण",
            icon = Icons.Default.AcUnit,
            basePrice = "₹499",
            description = "डीप जेट सर्विस, गैस चार्जिंग एवं लीकेज रिपेयर",
            popular = true
        ),
        ServiceItem(
            id = "electrician",
            nameEn = "Electrician Services",
            nameHi = "इलेक्ट्रीशियन (स्विच, पंखा, वायरिंग)",
            category = "इलेक्ट्रिकल",
            icon = Icons.Default.Bolt,
            basePrice = "₹199",
            description = "पंखा, MCB, इन्वर्टर, शॉर्ट सर्किट एवं संपूर्ण वायरिंग",
            popular = true
        ),
        ServiceItem(
            id = "plumber",
            nameEn = "Plumbing & Motor Repair",
            nameHi = "प्लंबर एवं वाटर मोटर रिपेयर",
            category = "प्लंबिंग",
            icon = Icons.Default.Plumbing,
            basePrice = "₹249",
            description = "नल, सिंक, पाइप लीकेज, मोटर रिपेयर व वाटर टैंक फिटिंग",
            popular = true
        ),
        ServiceItem(
            id = "appliances",
            nameEn = "Appliance Repair (Fridge/WM)",
            nameHi = "फ्रिज, वाशिंग मशीन व माइक्रोवेव",
            category = "होम अप्लायंसेज",
            icon = Icons.Default.Kitchen,
            basePrice = "₹349",
            description = "फ्रिज कूलिंग, वाशिंग मशीन मोटर व माइक्रोवेव समाधान",
            popular = false
        ),
        ServiceItem(
            id = "home_cleaning",
            nameEn = "Deep Home Cleaning",
            nameHi = "घर की डीप क्लीनिंग एवं सैनिटाइजेशन",
            category = "सफाई",
            icon = Icons.Default.CleaningServices,
            basePrice = "₹799",
            description = "किचन, बाथरूम, सोफा व पूरा घर चमकाएं",
            popular = true
        ),
        ServiceItem(
            id = "painting",
            nameEn = "House Painting & Putty",
            nameHi = "पेंटिंग एवं वाटरप्रूफिंग",
            category = "पेंटिंग",
            icon = Icons.Default.FormatPaint,
            basePrice = "₹999",
            description = "दीवार पेंट, पुट्टी वर्क, सीलन व वाटरप्रूफिंग समाधान",
            popular = false
        ),
        ServiceItem(
            id = "carpentry",
            nameEn = "Carpentry & Furniture",
            nameHi = "कारपेंटर / बढ़ई कार्य",
            category = "फर्नीचर",
            icon = Icons.Default.Handyman,
            basePrice = "₹299",
            description = "दरवाजा, खिड़की, लॉक, अलमारी एवं बेड रिपेयर",
            popular = false
        ),
        ServiceItem(
            id = "ro_service",
            nameEn = "RO Water Purifier Service",
            nameHi = "RO वाटर प्यूरीफायर सर्विस",
            category = "वाटर प्यूरीफायर",
            icon = Icons.Default.WaterDrop,
            basePrice = "₹299",
            description = "फिल्टर चेंज, मेंब्रेन रिप्लेसमेंट एवं संपूर्ण सर्विसिंग",
            popular = true
        )
    )
}
