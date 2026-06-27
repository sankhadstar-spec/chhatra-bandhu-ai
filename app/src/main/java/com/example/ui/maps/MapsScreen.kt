package com.example.ui.maps

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapsScreen(
    prefs: PreferencesManager,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    var selectedChallenge by remember { mutableStateOf<GeogChallenge?>(null) }
    var activeAnswer by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }
    var socraticResponse by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (selectedChallenge != null) selectedChallenge!!.region else "Google Maps Geography",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (selectedChallenge != null) {
                                selectedChallenge = null
                                activeAnswer = ""
                                isSubmitted = false
                                socraticResponse = ""
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
            if (selectedChallenge == null) {
                // Main map selection dashboard
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
                                    "Google Maps Geography Connector 🗺️",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    "Connect regional spatial geography with high-school mathematics and biology. Select a West Bengal pilot coordinate to explore!",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.85f),
                                    modifier = Modifier.padding(top = 6.dp)
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            "Select coordinates to analyze:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }

                    items(CurriculumData.geogChallenges) { challenge ->
                        Card(
                            onClick = { selectedChallenge = challenge },
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = challenge.region,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .background(Color(0xFFEFF6FF), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Icon(Icons.Default.LocationOn, contentDescription = "Loc", tint = Color(0xFF3B82F6), modifier = Modifier.size(10.dp))
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(challenge.coordinates.split(",")[0], fontSize = 9.sp, color = Color(0xFF3B82F6), fontFamily = FontFamily.Monospace)
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = challenge.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black
                                )
                                Text(
                                    text = challenge.description,
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                // Coordinates Lab View
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        // Simulated Interactive Map Frame
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE2E8F0))
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                // Background grid mimicking map lines
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val gridCount = 8
                                    val w = size.width
                                    val h = size.height
                                    for (i in 1..gridCount) {
                                        val x = w * (i.toFloat() / gridCount)
                                        drawLine(Color.LightGray, start = androidx.compose.ui.geometry.Offset(x, 0f), end = androidx.compose.ui.geometry.Offset(x, h), strokeWidth = 1f)
                                        val y = h * (i.toFloat() / gridCount)
                                        drawLine(Color.LightGray, start = androidx.compose.ui.geometry.Offset(0f, y), end = androidx.compose.ui.geometry.Offset(w, y), strokeWidth = 1f)
                                    }
                                }

                                // Interactive landmarks
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .background(Color.White, RoundedCornerShape(4.dp))
                                                .padding(6.dp)
                                        ) {
                                            Icon(Icons.Default.LocationOn, contentDescription = "Position", tint = Color.Red, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = selectedChallenge!!.coordinates,
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            )
                                        }
                                        Text(
                                            text = "Gps: Lock Established",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF16A34A)
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                        Icon(
                                            Icons.Default.LocationOn,
                                            contentDescription = "Pin",
                                            tint = Color.Red,
                                            modifier = Modifier.size(36.dp)
                                        )
                                        Text(
                                            text = selectedChallenge!!.region,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black,
                                            modifier = Modifier
                                                .background(Color.White, RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(1.dp))
                                }
                            }
                        }
                    }

                    // Regional Story Card
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Regional Geography Blueprint",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = selectedChallenge!!.description,
                                    fontSize = 13.sp,
                                    color = Color.Black,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    // Socratic Spatial Question
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)), // light green
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFFBBF7D0), RoundedCornerShape(12.dp))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Info, contentDescription = "Query", tint = Color(0xFF16A34A))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Topographical Socratic Challenge", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = selectedChallenge!!.socraticQuest,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.DarkGray,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    // Active Exercise Input
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)), // light yellow
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(12.dp))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Create, contentDescription = "Exercise", tint = Color(0xFFD97706))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Active Analytical Exercise", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = selectedChallenge!!.activeExercise,
                                    fontSize = 13.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 18.sp
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                OutlinedTextField(
                                    value = activeAnswer,
                                    onValueChange = { if (!isSubmitted) activeAnswer = it },
                                    placeholder = { Text("Show your mathematical/scientific reasoning step-by-step...") },
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 3,
                                    shape = RoundedCornerShape(8.dp)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        if (activeAnswer.isNotBlank()) {
                                            isSubmitted = true
                                            prefs.addPoints(25)
                                            socraticResponse = if (activeAnswer.length < 20) {
                                                "Socratic hint: What physical parameters are we missing? Make sure to compute the proportion of submersion or the boiling curves thoroughly. Write down your steps!"
                                            } else {
                                                "Masterful geographic calculation! Chhatra Bandhu AI approves of your analytical layout. This is top-tier academic competence. (+25 points!)"
                                            }
                                        }
                                    },
                                    modifier = Modifier.align(Alignment.End),
                                    enabled = activeAnswer.isNotBlank() && !isSubmitted
                                ) {
                                    Text("Submit Assessment")
                                }

                                AnimatedVisibility(visible = isSubmitted) {
                                    Column(modifier = Modifier.padding(top = 12.dp)) {
                                        Divider(color = Color.LightGray)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            "Chhatra Bandhu's Socratic Review:",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFD97706)
                                        )
                                        Text(
                                            text = socraticResponse,
                                            fontSize = 13.sp,
                                            color = Color.DarkGray,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
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
