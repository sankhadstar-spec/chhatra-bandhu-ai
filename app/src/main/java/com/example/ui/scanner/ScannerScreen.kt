package com.example.ui.scanner

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class Worksheet(
    val id: String,
    val title: String,
    val description: String,
    val subject: String,
    val mockImageSketch: String, // String representation of ASCII or styled sketch
    val socraticSteps: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
    prefs: PreferencesManager,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedSheet by remember { mutableStateOf<Worksheet?>(null) }
    var isScanning by remember { mutableStateOf(false) }
    var scanProgress by remember { mutableStateOf(0f) }
    var activeStepIdx by remember { mutableStateOf(0) }

    val worksheets = listOf(
        Worksheet(
            id = "geo_proof",
            title = "Geometry Transversal Proof",
            description = "Parallel lines AB and CD intersected by transversal EF at angles 1 and 2.",
            subject = "Mathematics",
            mockImageSketch = """
                  E
                  |    /
              A --+---/--- B   (Angle 1)
                  |  /
                  | /
              C --+/------ D   (Angle 2)
                  /|
                 / F
            """.trimIndent(),
            socraticSteps = listOf(
                "Let's identify the given variables. We have two horizontal lines, AB and CD, which are parallel. Line EF cuts through both. What is the spatial name of line EF?",
                "Angle 1 is corresponding to Angle 2. If lines are parallel, what fundamental theorem relates corresponding angles?",
                "Excellent! Since corresponding angles are equal, Angle 1 = Angle 2. If Angle 1 is measured as 110 degrees, what is Angle 2? Tell me the final step!"
            )
        ),
        Worksheet(
            id = "circuit_diagram",
            title = "Resistor Network Schematic",
            description = "A battery in series with a parallel junction of two 10-ohm resistors.",
            subject = "Physical Science",
            mockImageSketch = """
                 +---[ 10 ohm ]---+
                 |                |
              ---+                +--- (Battery 12V)
                 |                |
                 +---[ 10 ohm ]---+
            """.trimIndent(),
            socraticSteps = listOf(
                "First, analyze the parallel branch containing two 10-ohm resistors. What is the reciprocal formula for equivalent parallel resistance (1 / Rp)?",
                "Great! 1/Rp = 1/10 + 1/10 = 2/10. So Rp = 5 ohms. Now, we apply Ohm's Law (V = I * R) to find the total loop current. What is the total voltage of the battery?",
                "Applying V = I * R, with 12V = I * 5 ohms. Can you compute the loop current I? Show me your Socratic step!"
            )
        ),
        Worksheet(
            id = "botany_cell",
            title = "Plant Cell Photosynthetic Organelle",
            description = "Hand-sketched biological cell structure with double membranes and flat thylakoid sacs.",
            subject = "Life Science",
            mockImageSketch = """
               ___________________
              (  ___    ___    ___ )  <- Outer Boundary
              ( |   |  |   |  |   | ) <- Flat Sacs
              ( |___|  |___|  |___| )
              (_____________________)
            """.trimIndent(),
            socraticSteps = listOf(
                "Examine the flat sacs stacked inside this membrane. These individual sacs are called Thylakoids. What is a complete stack of thylakoids called?",
                "These thylakoids contain green pigments. What is the name of this light-absorbing pigment responsible for chemical absorption?",
                "Perfect! Chlorophyll captures sunlight to initiate Photosynthesis. What are the two primary gaseous molecules exchanged during this leaf lifecycle? Tell me!"
            )
        )
    )

    // Animated scanning laser line sweep using generic float
    val infiniteTransition = rememberInfiniteTransition(label = "scanLaser")
    val laserOffsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 500f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "laser"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (selectedSheet != null) "Socratic Homework Helper" else "Camera Homework Scanner",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (selectedSheet != null) {
                                selectedSheet = null
                                activeStepIdx = 0
                                isScanning = false
                                scanProgress = 0f
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
            if (selectedSheet == null) {
                // Gallery picker / Scanner capture cockpit
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
                                    "Multimodal Socratic Scanner 📷",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    "Scan a hand-drawn diagram or physics proof. Our lens parses variables instantly and overlays Socratic guides instead of cheats!",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.85f),
                                    modifier = Modifier.padding(top = 6.dp)
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            "Simulate Scanning a Homework Worksheet:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }

                    items(worksheets) { sheet ->
                        Card(
                            onClick = {
                                selectedSheet = sheet
                                isScanning = true
                                scanProgress = 0f
                                coroutineScope.launch {
                                    while (scanProgress < 1f) {
                                        delay(150)
                                        scanProgress += 0.1f
                                    }
                                    isScanning = false
                                }
                            },
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Camera", tint = MaterialTheme.colorScheme.primary)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = sheet.title,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = "${sheet.subject} • Click to simulate scan",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                                Icon(Icons.Default.PlayArrow, contentDescription = "Select", tint = Color.LightGray)
                            }
                        }
                    }
                }
            } else {
                // Interactive Scanner Frame and Socratic steps
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)), // Deep midnight black
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                if (isScanning) {
                                    // Animated swipe down scanning beam
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val y = laserOffsetY
                                        drawLine(
                                            color = Color(0xFF22C55E), // Neon Green
                                            start = androidx.compose.ui.geometry.Offset(0f, y),
                                            end = androidx.compose.ui.geometry.Offset(size.width, y),
                                            strokeWidth = 4f
                                        )
                                    }

                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        CircularProgressIndicator(color = Color(0xFF22C55E))
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "PARSING HANDWRITTEN SCHEMATIC: ${(scanProgress * 100).toInt()}%",
                                            color = Color(0xFF22C55E),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                } else {
                                    // Render parsed ASCII worksheet diagram
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                "Socratic Lens: Lock Established",
                                                color = Color(0xFF22C55E),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace
                                            )
                                            Text(
                                                "[100% MATH_CORRELATION]",
                                                color = Color(0xFF22C55E),
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = selectedSheet!!.mockImageSketch,
                                                color = Color.White,
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 13.sp,
                                                textAlign = TextAlign.Center,
                                                lineHeight = 16.sp
                                            )
                                        }

                                        Text(
                                            "Double-tap to adjust crop brackets",
                                            color = Color.Gray,
                                            fontSize = 9.sp,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (!isScanning) {
                        // Socratic Guide Milestones
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Star, contentDescription = "Active Step", tint = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Socratic Step ${activeStepIdx + 1} of ${selectedSheet!!.socraticSteps.size}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = selectedSheet!!.socraticSteps[activeStepIdx],
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Black,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { if (activeStepIdx > 0) activeStepIdx-- },
                                    enabled = activeStepIdx > 0
                                ) {
                                    Text("Previous Step")
                                }

                                if (activeStepIdx == selectedSheet!!.socraticSteps.size - 1) {
                                    Button(
                                        onClick = {
                                            prefs.addPoints(15)
                                            selectedSheet = null
                                            activeStepIdx = 0
                                        }
                                    ) {
                                        Text("Complete Proof (+15 pts)")
                                    }
                                } else {
                                    Button(
                                        onClick = { if (activeStepIdx < selectedSheet!!.socraticSteps.size - 1) activeStepIdx++ }
                                    ) {
                                        Text("Submit Reasoning Step")
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
