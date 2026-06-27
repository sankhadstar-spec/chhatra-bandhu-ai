package com.example.ui.arena

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*

data class Badge(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val tint: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArenaScreen(
    prefs: PreferencesManager,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    // Collect all quick tests in textbooks mapped for current board
    val currentBoard = prefs.getBoard()
    val activeQuestions = remember {
        CurriculumData.textbooks
            .filter { it.board == currentBoard }
            .flatMap { book ->
                book.chapters.flatMap { ch ->
                    ch.quickSelfTest.map { Pair(book.title, it) }
                }
            }
    }

    var quizQuestionIdx by remember { mutableStateOf(-1) } // -1 means dashboard, >= 0 means active quiz
    var selectedOptionIdx by remember { mutableStateOf(-1) }
    var isSubmitted by remember { mutableStateOf(false) }
    var showHint by remember { mutableStateOf(false) }

    val badges = listOf(
        Badge("newbie", "First Steps (নবীন)", "Joined Tuitioni digital companion", Icons.Default.Star, Color(0xFF3B82F6)),
        Badge("detective", "Socratic Detective", "Answered DIY kitchen experiments", Icons.Default.Search, Color(0xFF8B5CF6)),
        Badge("math_wiz", "Math Wizard", "Derived place value or calculus equation", Icons.Default.Add, Color(0xFFD97706)),
        Badge("science_pioneer", "Science Pioneer", "Mastered Boyle's Gas Laws", Icons.Default.Info, Color(0xFF10B981)),
        Badge("historian", "Regional Historian", "Interpreted Bengal revolts context", Icons.Default.List, Color(0xFFEC4899))
    )

    // Unlock badges based on points threshold for simulation
    LaunchedEffect(prefs.getPoints()) {
        val pts = prefs.getPoints()
        if (pts >= 70) prefs.unlockBadge("detective")
        if (pts >= 90) prefs.unlockBadge("math_wiz")
        if (pts >= 110) prefs.unlockBadge("science_pioneer")
        if (pts >= 130) prefs.unlockBadge("historian")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (quizQuestionIdx >= 0) "Diagnostic Checkpoint" else "Test Arena & Analytics",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (quizQuestionIdx >= 0) {
                                quizQuestionIdx = -1
                                selectedOptionIdx = -1
                                isSubmitted = false
                                showHint = false
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
            if (quizQuestionIdx < 0) {
                // Test Arena & Gamified Badge Dashboard
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Profile Header Card
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "SB", // Student Bandhu
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(
                                            text = "Tuitioni Academic Companion",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Text(
                                            text = "${prefs.getBoard()} • ${prefs.getClassName()}",
                                            fontSize = 12.sp,
                                            color = Color.DarkGray
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f))
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Score Points", fontSize = 11.sp, color = Color.Gray)
                                        Text("🪙 ${prefs.getPoints()}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Socratic Streak", fontSize = 11.sp, color = Color.Gray)
                                        Text("🔥 ${prefs.getStreak()} days", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEA580C))
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Unlocked Badges", fontSize = 11.sp, color = Color.Gray)
                                        val unlockedCount = badges.count { prefs.isBadgeUnlocked(it.id) }
                                        Text("🏆 $unlockedCount/5", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }

                    // Socratic Quiz Launcher
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "Ready for active diagnostic checkpoints?",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    "Mock exams on Tuitioni are 100% free. Answer conceptual, high-order queries tailored specifically to your board syllabus.",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(14.dp))

                                if (activeQuestions.isEmpty()) {
                                    Text(
                                        "No checkpoint tests compiled for ${prefs.getBoard()} yet. Select WBBSE in Settings to unlock the full sample bank!",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Red
                                    )
                                } else {
                                    Button(
                                        onClick = { quizQuestionIdx = 0 },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = "Play")
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Launch Subject Checkpoint")
                                    }
                                }
                            }
                        }
                    }

                    // Gamified Badges Shelf
                    item {
                        Text(
                            text = "Your Socratic Badges (ছাত্র গৌরব)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    items(badges) { badge ->
                        val isUnlocked = prefs.isBadgeUnlocked(badge.id)
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isUnlocked) Color.White else Color(0xFFF1F5F9).copy(alpha = 0.6f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(if (isUnlocked) badge.tint.copy(alpha = 0.1f) else Color.LightGray.copy(alpha = 0.3f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = badge.icon,
                                        contentDescription = badge.name,
                                        tint = if (isUnlocked) badge.tint else Color.Gray,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = badge.name,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isUnlocked) Color.Black else Color.Gray
                                        )
                                        Text(
                                            text = if (isUnlocked) "UNLOCKED" else "LOCKED",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isUnlocked) Color(0xFF16A34A) else Color.Gray,
                                            modifier = Modifier
                                                .background(
                                                    color = if (isUnlocked) Color(0xFFDCFCE7) else Color(0xFFE2E8F0),
                                                    shape = RoundedCornerShape(4.dp)
                                                )
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = badge.description,
                                        fontSize = 12.sp,
                                        color = if (isUnlocked) Color.DarkGray else Color.Gray,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Socratic Active Exam Process
                val (bookTitle, question) = activeQuestions[quizQuestionIdx]
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
                                    "Question ${quizQuestionIdx + 1} of ${activeQuestions.size}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Sourced from: $bookTitle",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = question.text,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }

                    // Options list
                    items(question.options.size) { optIdx ->
                        val isSelected = selectedOptionIdx == optIdx
                        val isCorrect = optIdx == question.correctAnswerIdx

                        val btnColor = if (isSubmitted) {
                            if (isCorrect) Color(0xFFDCFCE7) else if (isSelected) Color(0xFFFEE2E2) else Color.White
                        } else {
                            if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.White
                        }

                        val strokeColor = if (isSubmitted) {
                            if (isCorrect) Color(0xFF22C55E) else if (isSelected) Color(0xFFEF4444) else Color.LightGray
                        } else {
                            if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray
                        }

                        Card(
                            onClick = { if (!isSubmitted) selectedOptionIdx = optIdx },
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.5.dp, strokeColor, RoundedCornerShape(8.dp)),
                            colors = CardDefaults.cardColors(containerColor = btnColor)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = question.options[optIdx],
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black,
                                    modifier = Modifier.weight(1f)
                                )
                                if (isSubmitted) {
                                    if (isCorrect) {
                                        Icon(Icons.Default.Check, contentDescription = "Correct", tint = Color(0xFF16A34A))
                                    } else if (isSelected) {
                                        Icon(Icons.Default.Close, contentDescription = "Incorrect", tint = Color(0xFFDC2626))
                                    }
                                } else {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { if (!isSubmitted) selectedOptionIdx = optIdx }
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showHint = !showHint },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD97706))
                            ) {
                                Icon(Icons.Default.Info, contentDescription = "Hint")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Socratic Hint")
                            }

                            Button(
                                onClick = {
                                    if (!isSubmitted) {
                                        isSubmitted = true
                                        if (selectedOptionIdx == question.correctAnswerIdx) {
                                            prefs.addPoints(15)
                                        }
                                    } else {
                                        // Next question or finish
                                        if (quizQuestionIdx < activeQuestions.size - 1) {
                                            quizQuestionIdx++
                                            selectedOptionIdx = -1
                                            isSubmitted = false
                                            showHint = false
                                        } else {
                                            // Finish Quiz, return to dashboard
                                            quizQuestionIdx = -1
                                            selectedOptionIdx = -1
                                            isSubmitted = false
                                            showHint = false
                                        }
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                enabled = selectedOptionIdx != -1
                            ) {
                                Text(if (isSubmitted) "Next Challenge" else "Submit Answer")
                            }
                        }
                    }

                    // Socratic Hint Overlay
                    item {
                        AnimatedVisibility(visible = showHint) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9E6)),
                                border = BorderStroke(1.dp, Color(0xFFFCD34D)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(modifier = Modifier.padding(12.dp)) {
                                    Icon(Icons.Default.Info, contentDescription = "Help", tint = Color(0xFFD97706))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Chhatra Bandhu Socratic Prompt: \"${question.socraticHint}\"",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }
                    }

                    // Answer explanation
                    item {
                        AnimatedVisibility(visible = isSubmitted) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "Socratic Pedagogical Evaluation:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = question.explanation,
                                        fontSize = 13.sp,
                                        color = Color.DarkGray
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
