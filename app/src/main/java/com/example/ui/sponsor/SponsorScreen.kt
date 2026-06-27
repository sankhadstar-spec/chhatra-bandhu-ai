package com.example.ui.sponsor

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*

data class WelfareGrant(
    val id: String,
    val sponsorName: String,
    val grantTitle: String,
    val benefits: String,
    val eligibility: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SponsorScreen(
    prefs: PreferencesManager,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    var selectedGrant by remember { mutableStateOf<WelfareGrant?>(null) }
    
    // Application Form States
    var schoolName by remember { mutableStateOf("") }
    var incomeRange by remember { mutableStateOf("") }
    var classVerified by remember { mutableStateOf(false) }
    var isSubmitted by remember { mutableStateOf(false) }

    val grants = listOf(
        WelfareGrant(
            id = "tata_subsidy",
            sponsorName = "Tata Trusts Educational Initiative",
            grantTitle = "Tuitioni Digital Literacy Grant",
            benefits = "Free high-speed internet data vouchers + unlimited Chhatra Bandhu Socratic server inquiries.",
            eligibility = "Annual family income less than ₹1,20,000. Underrepresented West Bengal rural regions."
        ),
        WelfareGrant(
            id = "reliance_foundation",
            sponsorName = "Reliance Foundation CSR Wing",
            grantTitle = "Bengal Pilot Server-Runtime Scholarship",
            benefits = "100% subsidy for commercial AI and database API calls. 1 Free educational tablet for district toppers.",
            eligibility = "Class 5 to 12 students enrolled in West Bengal Govt. schools."
        ),
        WelfareGrant(
            id = "wipro_care",
            sponsorName = "Wipro Cares Community Upliftment",
            grantTitle = "Underprivileged Academic Companion Fund",
            benefits = "Free supplementary physical textbooks + monthly stationary allowances and digital mentorship.",
            eligibility = "Under-resourced families from tribal/hill areas of Batasia, Sundarbans, or border districts."
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (selectedGrant != null) "Sponsorship Form" else "CSR Sponsor Welfare",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (selectedGrant != null) {
                                selectedGrant = null
                                schoolName = ""
                                incomeRange = ""
                                classVerified = false
                                isSubmitted = false
                            } else {
                                onBack()
                            }
                        }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        containerColor = Color(0xFFFAF9F6)
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (selectedGrant == null) {
                // Welfare catalog list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "CSR Sponsor Welfare Hub (গৌরব তহবিল) 🌟",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    "Tuitioni is completely free for underprivileged households. Our model is fully sponsored by corporate welfare grants absorbing 100% of runtime server & API costs. Check your grant eligibility below!",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.85f),
                                    modifier = Modifier.padding(top = 6.dp)
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            "Available Corporate Subsidies & Grants:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }

                    items(grants) { grant ->
                        Card(
                            onClick = { selectedGrant = grant },
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = grant.sponsorName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = grant.grantTitle,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Benefits: ${grant.benefits}",
                                    fontSize = 12.sp,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Info, contentDescription = "Eligibility", tint = Color.Gray, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Eligible: ${grant.eligibility}",
                                        fontSize = 10.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Interactive sponsorship application form
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = selectedGrant!!.grantTitle,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Sponsor: ${selectedGrant!!.sponsorName}",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }

                    if (!isSubmitted) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text("Sponsorship Verification Form", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)

                                    OutlinedTextField(
                                        value = schoolName,
                                        onValueChange = { schoolName = it },
                                        label = { Text("Government School Name") },
                                        placeholder = { Text("e.g. Batasia High School") },
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    OutlinedTextField(
                                        value = incomeRange,
                                        onValueChange = { incomeRange = it },
                                        label = { Text("Estimated Family Annual Income") },
                                        placeholder = { Text("e.g. ₹60,000") },
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Checkbox(
                                            checked = classVerified,
                                            onCheckedChange = { classVerified = it }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "I verify that I am currently a student in ${prefs.getClassName()}.",
                                            fontSize = 12.sp,
                                            color = Color.DarkGray
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            if (schoolName.isNotBlank() && incomeRange.isNotBlank() && classVerified) {
                                                isSubmitted = true
                                                prefs.addPoints(30)
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        enabled = schoolName.isNotBlank() && incomeRange.isNotBlank() && classVerified
                                    ) {
                                        Text("Submit Sponsorship Application")
                                    }
                                }
                            }
                        }
                    } else {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7)), // light green
                                border = BorderStroke(2.dp, Color(0xFF22C55E)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Approved",
                                        tint = Color(0xFF16A34A),
                                        modifier = Modifier.size(56.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "SPONSORSHIP CONFIRMED",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF16A34A)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Congratulations! ${selectedGrant!!.sponsorName} has fully approved your digital scholarship. Your Socratic server queries on Tuitioni are 100% free and fully subsidized.\n\nEnjoy unlimited, uninterrupted tutoring with Chhatra Bandhu AI!",
                                        fontSize = 13.sp,
                                        color = Color.DarkGray,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 18.sp
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Button(
                                        onClick = { selectedGrant = null }
                                    ) {
                                        Text("Back to Welfare Catalog")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
