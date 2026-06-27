package com.example.ui.curriculum

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurriculumScreen(
    prefs: PreferencesManager,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    var selectedTextbook by remember { mutableStateOf<Textbook?>(null) }
    var selectedChapter by remember { mutableStateOf<Chapter?>(null) }
    
    // Active diagnostic test states
    var activeQuizQuestionIdx by remember { mutableStateOf(-1) }
    var selectedOptionIdx by remember { mutableStateOf(-1) }
    var showHint by remember { mutableStateOf(false) }
    var showExplanation by remember { mutableStateOf(false) }
    var isAnswerCorrect by remember { mutableStateOf(false) }

    var tabIndex by remember { mutableStateOf(0) } // 0: Textbooks, 1: Formula Sheets, 2: Previous Year Papers

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (selectedChapter != null) {
                            selectedChapter!!.banglaTitle
                        } else if (selectedTextbook != null) {
                            selectedTextbook!!.title
                        } else {
                            "Physics Wallah Mode"
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (selectedChapter != null) {
                                selectedChapter = null
                                activeQuizQuestionIdx = -1
                            } else if (selectedTextbook != null) {
                                selectedTextbook = null
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
            if (selectedTextbook == null) {
                // Top Tab Selector for browsing resources
                TabRow(
                    selectedTabIndex = tabIndex,
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                ) {
                    Tab(
                        selected = tabIndex == 0,
                        onClick = { tabIndex = 0 },
                        text = { Text("Textbooks", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                    )
                    Tab(
                        selected = tabIndex == 1,
                        onClick = { tabIndex = 1 },
                        text = { Text("Formulas", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                    )
                    Tab(
                        selected = tabIndex == 2,
                        onClick = { tabIndex = 2 },
                        text = { Text("Madhyamik PYQs", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                    )
                }

                when (tabIndex) {
                    0 -> TextbooksTab(
                        prefs = prefs,
                        onSelect = { selectedTextbook = it }
                    )
                    1 -> FormulasTab(prefs = prefs)
                    2 -> PYQsTab()
                }
            } else if (selectedChapter == null) {
                // Chapter Selector View
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    "${selectedTextbook!!.title} (${selectedTextbook!!.banglaTitle})",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    "Board: ${selectedTextbook!!.board} • ${selectedTextbook!!.className} • Subject: ${selectedTextbook!!.subject}",
                                    fontSize = 12.sp,
                                    color = Color.DarkGray,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }

                    item {
                        Text(
                            "Select a Chapter to Study:",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    items(selectedTextbook!!.chapters) { chapter ->
                        Card(
                            onClick = { selectedChapter = chapter },
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = chapter.banglaTitle,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = chapter.title,
                                        fontSize = 13.sp,
                                        color = Color.Gray
                                    )
                                }
                                Icon(
                                    Icons.Default.PlayArrow,
                                    contentDescription = "Open",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            } else {
                // Detailed Chapter Study Hub and Socratic Quick Quiz
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Title Card
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = selectedChapter!!.banglaTitle,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = selectedChapter!!.title,
                                    fontSize = 14.sp,
                                    color = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }

                    // Key Concepts Card
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.List, contentDescription = "Concepts", tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Core Syllabus Concepts", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                selectedChapter!!.keyConcepts.forEachIndexed { index, concept ->
                                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                        Text("${index + 1}.", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(concept, fontSize = 13.sp, color = Color.DarkGray)
                                    }
                                }
                            }
                        }
                    }

                    // Formulas Sheet inside Chapter if available
                    if (selectedChapter!!.formulas.isNotEmpty()) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)), // light green
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Add, contentDescription = "Formulas", tint = Color(0xFF16A34A))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Regional Textbook Formula Sheet", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    selectedChapter!!.formulas.forEach { (name, value) ->
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 6.dp)
                                                .background(Color.White, RoundedCornerShape(8.dp))
                                                .border(1.dp, Color(0xFFBBF7D0), RoundedCornerShape(8.dp))
                                                .padding(10.dp)
                                        ) {
                                            Text(name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                                            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color.Black)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Socratic Test Section
                    if (selectedChapter!!.quickSelfTest.isNotEmpty()) {
                        item {
                            val question = selectedChapter!!.quickSelfTest[0]
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)), // light amber
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, Color(0xFFFDE68A), RoundedCornerShape(12.dp))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.Info, contentDescription = "Quiz", tint = Color(0xFFD97706))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Socratic Checkpoint", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                                        }
                                        Text("Score: +15 pts", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(question.text, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Answer Options
                                    question.options.forEachIndexed { optIdx, option ->
                                        val isSelected = selectedOptionIdx == optIdx
                                        val btnColor = if (isSelected) {
                                            if (showExplanation) {
                                                if (optIdx == question.correctAnswerIdx) Color(0xFF22C55E) else Color(0xFFEF4444)
                                            } else {
                                                MaterialTheme.colorScheme.primary
                                            }
                                        } else {
                                            Color.White
                                        }

                                        val textBtnColor = if (isSelected) Color.White else Color.Black

                                        Button(
                                            onClick = {
                                                if (!showExplanation) {
                                                    selectedOptionIdx = optIdx
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = btnColor),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .border(1.dp, if (isSelected) Color.Transparent else Color.LightGray, RoundedCornerShape(8.dp)),
                                            contentPadding = PaddingValues(12.dp)
                                        ) {
                                            Text(
                                                text = option,
                                                color = textBtnColor,
                                                fontSize = 13.sp,
                                                textAlign = TextAlign.Start,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Action buttons (Submit / Hint)
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
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Socratic Hint", fontSize = 12.sp)
                                        }

                                        Button(
                                            onClick = {
                                                if (selectedOptionIdx != -1 && !showExplanation) {
                                                    showExplanation = true
                                                    isAnswerCorrect = selectedOptionIdx == question.correctAnswerIdx
                                                    if (isAnswerCorrect) {
                                                        prefs.addPoints(15)
                                                    }
                                                }
                                            },
                                            modifier = Modifier.weight(1f),
                                            enabled = selectedOptionIdx != -1 && !showExplanation
                                        ) {
                                            Text("Submit Answer", fontSize = 12.sp)
                                        }
                                    }

                                    // Animated Socratic Hint display
                                    AnimatedVisibility(visible = showHint) {
                                        Card(
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)), // amber light
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 10.dp)
                                        ) {
                                            Row(modifier = Modifier.padding(10.dp)) {
                                                Icon(Icons.Default.Info, contentDescription = "Ask", tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = "Chhatra Bandhu's Prompt: \"${question.socraticHint}\"",
                                                    fontSize = 12.sp,
                                                    color = Color.DarkGray,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }

                                    // Animated Explanation / Results display
                                    AnimatedVisibility(visible = showExplanation) {
                                        Column(modifier = Modifier.padding(top = 12.dp)) {
                                            Divider(color = Color.LightGray)
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = if (isAnswerCorrect) Icons.Default.Check else Icons.Default.Close,
                                                    contentDescription = "Result",
                                                    tint = if (isAnswerCorrect) Color(0xFF16A34A) else Color(0xFFDC2626)
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = if (isAnswerCorrect) "Correct! Excellent critical thinking. (+15 points!)" else "Oops! Not quite. Think Socratic!",
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isAnswerCorrect) Color(0xFF16A34A) else Color(0xFFDC2626)
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = "Socratic Mastery: ${question.explanation}",
                                                fontSize = 12.sp,
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
    }
}

@Composable
fun TextbooksTab(prefs: PreferencesManager, onSelect: (Textbook) -> Unit) {
    val currentBoard = prefs.getBoard()
    var selectedBoard by remember { mutableStateOf("All Boards") }
    var selectedClass by remember { mutableStateOf("All Classes") }
    var selectedSubject by remember { mutableStateOf("All Subjects") }
    
    val uriHandler = LocalUriHandler.current

    // Initialize board based on preference
    LaunchedEffect(currentBoard) {
        if (currentBoard.isNotBlank()) {
            selectedBoard = currentBoard
        }
    }

    val boards = listOf("All Boards", "WBBSE", "WBCHSE", "WBBME")
    val classes = listOf("All Classes", "Class 5", "Class 10", "Class 11", "Class 12")
    val subjects = listOf("All Subjects", "Mathematics", "Physical Science", "English", "Languages", "Bengali Literature", "Islamic Studies & Arabic")

    val filteredBooks = remember(selectedBoard, selectedClass, selectedSubject) {
        CurriculumData.textbooks.filter { book ->
            val boardMatches = selectedBoard == "All Boards" || book.board == selectedBoard || book.board == "All Boards"
            
            val classMatches = selectedClass == "All Classes" || when (selectedClass) {
                "Class 5" -> book.className == "Class 5" || book.className.contains("1-12") || book.className.contains("1-10")
                "Class 10" -> book.className == "Class 10" || book.className.contains("1-12") || book.className.contains("1-10")
                "Class 11" -> book.className == "Class 11" || book.className.contains("11-12") || book.className.contains("1-12")
                "Class 12" -> book.className == "Class 12" || book.className.contains("11-12") || book.className.contains("1-12")
                else -> book.className.contains(selectedClass, ignoreCase = true)
            }
            
            val subjectMatches = selectedSubject == "All Subjects" || when (selectedSubject) {
                "Mathematics" -> book.subject.contains("Mathematics", ignoreCase = true) || book.subject.contains("All", ignoreCase = true)
                "Physical Science" -> book.subject.contains("Physical Science", ignoreCase = true) || book.subject.contains("All", ignoreCase = true)
                "English" -> book.subject.contains("English", ignoreCase = true) || book.subject.contains("All", ignoreCase = true)
                "Languages" -> book.subject.contains("Languages", ignoreCase = true) || book.subject.contains("English", ignoreCase = true) || book.subject.contains("Literature", ignoreCase = true) || book.subject.contains("All", ignoreCase = true)
                "Bengali Literature" -> book.subject.contains("Bengali", ignoreCase = true) || book.subject.contains("All", ignoreCase = true)
                "Islamic Studies & Arabic" -> book.subject.contains("Islamic", ignoreCase = true) || book.subject.contains("Arabic", ignoreCase = true) || book.subject.contains("All", ignoreCase = true)
                else -> book.subject.contains(selectedSubject, ignoreCase = true)
            }
            
            boardMatches && classMatches && subjectMatches
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Welcome and Reference Help Info Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)), // soft blue
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📢", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Verified Government Portal References",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E40AF)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "We support underprivileged students and Madrasah learners with direct references to authorized textbook download channels. Switch board/class below to filter materials.",
                        fontSize = 11.sp,
                        color = Color(0xFF1E3A8A)
                    )
                }
            }
        }

        // Category Filter - Board
        item {
            Column {
                Text(
                    text = "Filter by Board / Authority:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(boards) { boardName ->
                        val isSelected = selectedBoard == boardName
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.White)
                                .border(1.dp, if (isSelected) Color.Transparent else Color.LightGray, RoundedCornerShape(20.dp))
                                .clickable { selectedBoard = boardName }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = boardName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color.DarkGray
                            )
                        }
                    }
                }
            }
        }

        // Category Filter - Class
        item {
            Column {
                Text(
                    text = "Filter by Class / Grade:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(classes) { className ->
                        val isSelected = selectedClass == className
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) Color(0xFFF59E0B) else Color.White) // Amber color for classes
                                .border(1.dp, if (isSelected) Color.Transparent else Color.LightGray, RoundedCornerShape(20.dp))
                                .clickable { selectedClass = className }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = className,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color.DarkGray
                            )
                        }
                    }
                }
            }
        }

        // Category Filter - Subject
        item {
            Column {
                Text(
                    text = "Filter by Subject / Field:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(subjects) { subjectName ->
                        val isSelected = selectedSubject == subjectName
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) Color(0xFF10B981) else Color.White) // Emerald color for subjects
                                .border(1.dp, if (isSelected) Color.Transparent else Color.LightGray, RoundedCornerShape(20.dp))
                                .clickable { selectedSubject = subjectName }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = subjectName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color.DarkGray
                            )
                        }
                    }
                }
            }
        }

        // Textbooks Header Count
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Available Textbooks & Referrals (${filteredBooks.size}):",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
                if (selectedBoard != "All Boards" || selectedClass != "All Classes" || selectedSubject != "All Subjects") {
                    Text(
                        text = "Clear Filters",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            selectedBoard = "All Boards"
                            selectedClass = "All Classes"
                            selectedSubject = "All Subjects"
                        }
                    )
                }
            }
        }

        if (filteredBooks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "No textbooks matches",
                            tint = Color.LightGray,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "No books matching selected filters.\nTry resetting your filters or selecting 'All Boards'.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(filteredBooks) { book ->
                val isSocraticStudyAvailable = book.chapters.any { it.quickSelfTest.isNotEmpty() || it.keyConcepts.isNotEmpty() }
                
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            if (book.board == "All Boards") Color(0xFFDBEAFE) else Color(0xFFE2E8F0),
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Subject Code Badge / Avatar
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when (book.subject) {
                                            "Mathematics" -> Color(0xFFFEF3C7)
                                            "Physical Science" -> Color(0xFFE0F2FE)
                                            "English", "Languages" -> Color(0xFFF3E8FF)
                                            "Bengali Literature" -> Color(0xFFD1FAE5)
                                            "Islamic Studies & Arabic" -> Color(0xFFFCE7F3)
                                            else -> Color(0xFFF1F5F9)
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = when (book.subject) {
                                        "Mathematics" -> "📐"
                                        "Physical Science" -> "⚡"
                                        "English", "Languages" -> "📖"
                                        "Bengali Literature" -> "✍️"
                                        "Islamic Studies & Arabic" -> "🕌"
                                        else -> "📚"
                                    },
                                    fontSize = 18.sp
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = book.banglaTitle,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                    if (book.board == "All Boards" || book.id.contains("portal") || book.id.contains("backup")) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .background(Color(0xFF2563EB), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("PORTAL", fontSize = 8.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                Text(
                                    text = book.title,
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFE2E8F0), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(book.board, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFFEE2E2), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(book.className, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF991B1B))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Divider(color = Color(0xFFF1F5F9))
                        
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Official download URL action
                            if (book.downloadUrl != null) {
                                Button(
                                    onClick = { uriHandler.openUri(book.downloadUrl) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(vertical = 8.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("🌐 ", fontSize = 12.sp)
                                        Text(
                                            text = if (book.id.contains("portal") || book.id.contains("backup")) "Open Portal" else "Download PDF",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            // Interactive AI Socratic tutor study action
                            Button(
                                onClick = { onSelect(book) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSocraticStudyAvailable) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.5f)
                                ),
                                enabled = isSocraticStudyAvailable,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("🤖 ", fontSize = 12.sp)
                                    Text(
                                        text = "AI Socratic Tutor",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSocraticStudyAvailable) Color.White else Color.DarkGray
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

@Composable
fun FormulasTab(prefs: PreferencesManager) {
    val currentBoard = prefs.getBoard()
    val formulasList = remember {
        CurriculumData.textbooks
            .filter { it.board == currentBoard }
            .flatMap { book ->
                book.chapters.flatMap { ch ->
                    ch.formulas.map { Triple(book.title, ch.title, it) }
                }
            }
    }

    if (formulasList.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "No formulas recorded for $currentBoard. Select WBBSE/WBCHSE to view math & science formulas!",
                fontSize = 13.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(formulasList) { (bookName, chapterName, formula) ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = formula.first,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = formula.second,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Grounded in: $bookName • $chapterName",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PYQsTab() {
    val pyqList = remember { CurriculumData.pyqs }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(pyqList) { paper ->
            Card(
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
                            text = "${paper.subject} (${paper.year})",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "WBBSE Exam",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    paper.questions.forEachIndexed { qIdx, (q, hint) ->
                        var isHintRevealed by remember { mutableStateOf(false) }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Q${qIdx + 1}: $q",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))

                            if (isHintRevealed) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFFEF3C7), RoundedCornerShape(6.dp)) // amber
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = "Hint",
                                        tint = Color(0xFFD97706),
                                        modifier = Modifier.size(14.dp).padding(top = 1.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Chhatra Bandhu Hint: \"$hint\"",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.DarkGray,
                                        lineHeight = 14.sp
                                    )
                                }
                            } else {
                                Button(
                                    onClick = { isHintRevealed = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray.copy(alpha = 0.4f)),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.height(24.dp)
                                ) {
                                    Text("Reveal Socratic Hint", fontSize = 10.sp, color = Color.DarkGray)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
