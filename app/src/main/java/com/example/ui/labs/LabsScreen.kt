package com.example.ui.labs

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabsScreen(
    prefs: PreferencesManager,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    var selectedExp by remember { mutableStateOf<DIYExperiment?>(null) }
    var activeStepIdx by remember { mutableStateOf(0) }
    
    // Socratic Prompt Response States
    var studentAnswer by remember { mutableStateOf("") }
    var isAnswerSubmitted by remember { mutableStateOf(false) }
    var socraticFeedback by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (selectedExp != null) "DIY Kitchen Lab" else "BYJU'S Visual Labs",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (selectedExp != null) {
                                selectedExp = null
                                activeStepIdx = 0
                                studentAnswer = ""
                                isAnswerSubmitted = false
                                socraticFeedback = ""
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
            if (selectedExp == null) {
                // Labs Catalog View
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
                                    "Zero-Cost Kitchen Science Labs 🧪",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    "No expensive school labs required! Turn your kitchen into an interactive science hub using common, safe household objects.",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.85f),
                                    modifier = Modifier.padding(top = 6.dp)
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            "Select an Experiment to Conduct:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }

                    items(CurriculumData.diyExperiments) { exp ->
                        Card(
                            onClick = { selectedExp = exp },
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
                                        text = exp.title,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Icon(
                                        Icons.Default.Build,
                                        contentDescription = "Experiment",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Text(
                                    text = exp.objective,
                                    fontSize = 13.sp,
                                    color = Color.DarkGray,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Build,
                                        contentDescription = "Materials",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Requires: ${exp.householdMaterials.take(3).joinToString(", ")}...",
                                        fontSize = 11.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Interactive Lab Experiment Run-through
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
                                    text = selectedExp!!.title,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Objective: ${selectedExp!!.objective}",
                                    fontSize = 13.sp,
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }

                    // Materials Card
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ShoppingCart, contentDescription = "Shopping", tint = Color.DarkGray)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Household Materials Needed", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                selectedExp!!.householdMaterials.forEach { mat ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = "Check",
                                            tint = Color(0xFF16A34A),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(mat, fontSize = 13.sp, color = Color.Black)
                                    }
                                }
                            }
                        }
                    }

                    // Steps Navigation Card
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Step ${activeStepIdx + 1} of ${selectedExp!!.steps.size}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = selectedExp!!.steps[activeStepIdx],
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    OutlinedButton(
                                        onClick = { if (activeStepIdx > 0) activeStepIdx-- },
                                        enabled = activeStepIdx > 0
                                    ) {
                                        Text("Previous")
                                    }

                                    Button(
                                        onClick = { if (activeStepIdx < selectedExp!!.steps.size - 1) activeStepIdx++ },
                                        enabled = activeStepIdx < selectedExp!!.steps.size - 1
                                    ) {
                                        Text("Next Step")
                                    }
                                }
                            }
                        }
                    }

                    // Show scientific principle only when on the last step
                    if (activeStepIdx == selectedExp!!.steps.size - 1) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)), // light green
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color(0xFFBBF7D0), RoundedCornerShape(12.dp))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Star, contentDescription = "Principle", tint = Color(0xFF16A34A))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Scientific Principle", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = selectedExp!!.scientificPrinciple,
                                        fontSize = 13.sp,
                                        color = Color.DarkGray,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }

                        // Socratic Challenge Prompt
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)), // light amber
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(12.dp))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Info, contentDescription = "Challenge", tint = Color(0xFFD97706))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Socratic Experiment Quest", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = selectedExp!!.socraticQuestion,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    OutlinedTextField(
                                        value = studentAnswer,
                                        onValueChange = { if (!isAnswerSubmitted) studentAnswer = it },
                                        placeholder = { Text("Write your hypothesis/answer here...") },
                                        modifier = Modifier.fillMaxWidth(),
                                        minLines = 3,
                                        shape = RoundedCornerShape(8.dp)
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Button(
                                        onClick = {
                                            if (studentAnswer.isNotBlank()) {
                                                isAnswerSubmitted = true
                                                prefs.addPoints(20)
                                                socraticFeedback = if (studentAnswer.length < 15) {
                                                    "A promising start! Try to expand on what specific particles (electrons, ions, molecules) are shifting during this process. Hold a lemon coin chain in your mind's eye!"
                                                } else {
                                                    "Excellent formulation! You've successfully mapped the core scientific variables. Chhatra Bandhu awards you +20 Socratic points for this incredible DIY discovery!"
                                                }
                                            }
                                        },
                                        modifier = Modifier.align(Alignment.End),
                                        enabled = studentAnswer.isNotBlank() && !isAnswerSubmitted
                                    ) {
                                        Text("Submit Hypothesis")
                                    }

                                    AnimatedVisibility(visible = isAnswerSubmitted) {
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
                                                text = socraticFeedback,
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
}
