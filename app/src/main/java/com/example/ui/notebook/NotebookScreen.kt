package com.example.ui.notebook

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GeminiService
import com.example.data.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

// Data structures
data class NoteItem(
    val id: String,
    val content: String,
    val timestamp: Long
)

data class QuizItem(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    var userAnswerIndex: Int? = null
)

data class Notebook(
    val id: String,
    val title: String,
    val createdAt: Long,
    val notes: List<NoteItem>,
    val aiSummary: String?,
    val quizzes: List<QuizItem>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotebookScreen(
    prefs: PreferencesManager,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // Notebook list state
    val notebooks = remember { mutableStateListOf<Notebook>() }
    var selectedNotebookId by remember { mutableStateOf<String?>(null) }
    
    // Load notebooks on start
    LaunchedEffect(Unit) {
        notebooks.clear()
        notebooks.addAll(loadNotebooks(prefs))
    }
    
    val selectedNotebook = notebooks.find { it.id == selectedNotebookId }
    
    // Dialog states
    var showCreateDialog by remember { mutableStateOf(false) }
    var newNotebookTitle by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (selectedNotebook != null) selectedNotebook.title else "Gemini AI Notebooks",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (selectedNotebook != null) "স্মার্ট স্টাডি মেন্টর" else "গৌরব স্টাডি নোটবুক",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (selectedNotebookId != null) {
                            selectedNotebookId = null
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color(0xFFFAF9F6) // Soft off-white paper canvas
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (selectedNotebook == null) {
                // List of notebooks view
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Quick stats header
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Notebook AI Mentor 📚",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    "Store notes. Generate summaries & quizzes instantly!",
                                    fontSize = 11.sp,
                                    color = Color.DarkGray
                                )
                            }
                            Text(
                                "🪙 ${prefs.getPoints()} pts",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .background(Color.White, RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    // Floating Create Button
                    Button(
                        onClick = { showCreateDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "New Notebook")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Create New Notebook (নতুন নোটবুক)")
                    }

                    if (notebooks.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Empty",
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(64.dp)
                                )
                                Text(
                                    "No notebooks created yet.",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    "Tap above to begin your digital study folder!",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(notebooks, key = { it.id }) { notebook ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedNotebookId = notebook.id },
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = notebook.title,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black
                                            )
                                            IconButton(
                                                onClick = {
                                                    notebooks.remove(notebook)
                                                    saveNotebooks(prefs, notebooks)
                                                    Toast.makeText(context, "Notebook Deleted", Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Delete,
                                                    contentDescription = "Delete",
                                                    tint = Color.Red.copy(alpha = 0.7f),
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                        
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "📝 ${notebook.notes.size} Notes added",
                                                fontSize = 12.sp,
                                                color = Color.Gray
                                            )
                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                if (notebook.aiSummary != null) {
                                                    Text(
                                                        text = "AI Guide Ready",
                                                        fontSize = 10.sp,
                                                        color = Color(0xFF10B981),
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier
                                                            .background(Color(0xFFE6F4EA), RoundedCornerShape(4.dp))
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                                if (notebook.quizzes.isNotEmpty()) {
                                                    Text(
                                                        text = "Quiz Ready",
                                                        fontSize = 10.sp,
                                                        color = Color(0xFF8B5CF6),
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier
                                                            .background(Color(0xFFF3E8FF), RoundedCornerShape(4.dp))
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
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
            } else {
                // Detail view for a selected notebook
                NotebookDetailView(
                    notebook = selectedNotebook,
                    prefs = prefs,
                    onUpdateNotebook = { updatedNotebook ->
                        val index = notebooks.indexOfFirst { it.id == updatedNotebook.id }
                        if (index != -1) {
                            notebooks[index] = updatedNotebook
                            saveNotebooks(prefs, notebooks)
                        }
                    }
                )
            }
        }
    }

    // Create Notebook Dialog
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Create New Notebook", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newNotebookTitle,
                    onValueChange = { newNotebookTitle = it },
                    label = { Text("Notebook Title (e.g., Physics Mechanics)") },
                    placeholder = { Text("e.g. Madhyamik Physical Science") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newNotebookTitle.trim().isNotEmpty()) {
                            val newNotebook = Notebook(
                                id = UUID.randomUUID().toString(),
                                title = newNotebookTitle.trim(),
                                createdAt = System.currentTimeMillis(),
                                notes = emptyList(),
                                aiSummary = null,
                                quizzes = emptyList()
                            )
                            notebooks.add(newNotebook)
                            saveNotebooks(prefs, notebooks)
                            prefs.addPoints(5) // Bonus points for organizing!
                            Toast.makeText(context, "Notebook Created! +5 pts", Toast.LENGTH_SHORT).show()
                            newNotebookTitle = ""
                            showCreateDialog = false
                        }
                    }
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun NotebookDetailView(
    notebook: Notebook,
    prefs: PreferencesManager,
    onUpdateNotebook: (Notebook) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("My Notes (নোট)", "AI Study Guide (মেন্টর)", "Mock Quizzes (কুইজ)")
    
    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.White,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }
        
        when (selectedTab) {
            0 -> NotesTab(notebook, onUpdateNotebook)
            1 -> AiMentorTab(notebook, prefs, onUpdateNotebook)
            2 -> QuizTab(notebook, prefs, onUpdateNotebook)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesTab(
    notebook: Notebook,
    onUpdateNotebook: (Notebook) -> Unit
) {
    val context = LocalContext.current
    var newNoteContent by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Add note field
        OutlinedTextField(
            value = newNoteContent,
            onValueChange = { newNoteContent = it },
            label = { Text("Write, paste or log study concepts here:", fontSize = 12.sp) },
            placeholder = { Text("e.g., Ohm's law states that V = IR. V is voltage, I is current, and R is resistance...") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 4,
            trailingIcon = {
                if (newNoteContent.trim().isNotEmpty()) {
                    IconButton(onClick = {
                        val newNote = NoteItem(
                            id = UUID.randomUUID().toString(),
                            content = newNoteContent.trim(),
                            timestamp = System.currentTimeMillis()
                        )
                        val updatedNotes = notebook.notes + newNote
                        onUpdateNotebook(notebook.copy(notes = updatedNotes))
                        newNoteContent = ""
                        Toast.makeText(context, "Note added!", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Send, contentDescription = "Add Note", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        )
        
        Text(
            "Saved Notes (আমার জমানো সূত্র):",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        
        if (notebook.notes.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "This folder is empty. Write key statements or copy-paste text above to teach Chhatra Bandhu AI about your curriculum!",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(24.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notebook.notes.reversed(), key = { it.id }) { note ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = note.content,
                                    fontSize = 14.sp,
                                    color = Color.Black,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = {
                                        val updatedNotes = notebook.notes.filter { it.id != note.id }
                                        onUpdateNotebook(notebook.copy(notes = updatedNotes))
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Delete note",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            
                            val dateStr = android.text.format.DateFormat.format("MMM dd, yyyy - hh:mm a", note.timestamp).toString()
                            Text(
                                text = dateStr,
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AiMentorTab(
    notebook: Notebook,
    prefs: PreferencesManager,
    onUpdateNotebook: (Notebook) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isGenerating by remember { mutableStateOf(false) }
    
    // Mini Socratic chat inside notebook
    var qaPrompt by remember { mutableStateOf("") }
    var qaResponse by remember { mutableStateOf("") }
    var isQaLoading by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "AI Study Guide (স্মার্ট সংক্ষিপ্ত সার)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    "Chhatra Bandhu compiles a rigorous, verified study summary based on your notes, checking real-time internet definitions via live search grounding.",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
                
                if (notebook.aiSummary != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF9F9FB), RoundedCornerShape(8.dp))
                            .border(1.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        BoldParseText(
                            line = notebook.aiSummary,
                            textColor = Color.DarkGray,
                            fontSize = 13.sp
                        )
                    }
                }
                
                Button(
                    onClick = {
                        if (notebook.notes.isEmpty()) {
                            return@Button
                        }
                        coroutineScope.launch {
                            isGenerating = true
                            val notesText = notebook.notes.joinToString("\n\n") { "- ${it.content}" }
                            
                            val prompt = """
                                Based on these study notes provided by the student:
                                $notesText
                                
                                Write a comprehensive, beautifully structured, and highly encouraging Study Guide.
                                Use clear markdown headings, bullet points, and explain the concepts in a friendly Socratic way.
                                Maintain linguistic preference: ${prefs.getLanguage()} (Benglish/Bengali or English).
                                Regional context: Board: ${prefs.getBoard()}, Class: ${prefs.getClassName()}, Subject: ${prefs.getSubject()}.
                                
                                We have activated Google Search grounding, so please search the web for any updated information, authoritative explanations, or physical/historical citations, and append verified inline markdown sources like [Source Title](URL) so poor students have direct free reference! Keep formatting clear.
                            """.trimIndent()
                            
                            val guide = GeminiService.generateContent(
                                prompt = prompt,
                                board = prefs.getBoard(),
                                className = prefs.getClassName(),
                                subject = prefs.getSubject(),
                                language = prefs.getLanguage()
                            )
                            
                            onUpdateNotebook(notebook.copy(aiSummary = guide))
                            prefs.addPoints(15) // reward points!
                            isGenerating = false
                        }
                    },
                    enabled = notebook.notes.isNotEmpty() && !isGenerating,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Compiling AI Study Guide...")
                    } else {
                        Icon(Icons.Default.Refresh, contentDescription = "Generate")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (notebook.aiSummary != null) "Refresh AI Study Guide (+15 pts)" else "Generate AI Study Guide (+15 pts)")
                    }
                }
                
                if (notebook.notes.isEmpty()) {
                    Text(
                        "⚠️ Please add some notes in the 'My Notes' tab first to generate your AI Study Guide.",
                        fontSize = 11.sp,
                        color = Color.Red.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        
        // Notebook Specific Q&A Coach
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Face, contentDescription = "Mentor", tint = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Socratic Notebook Coach",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                
                Text(
                    "Ask Chhatra Bandhu a targeted question grounded specifically on the study notes inside this notebook folder.",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
                
                OutlinedTextField(
                    value = qaPrompt,
                    onValueChange = { qaPrompt = it },
                    placeholder = { Text("Ask about these formulas or notes...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        if (qaPrompt.trim().isNotEmpty() && !isQaLoading) {
                            coroutineScope.launch {
                                isQaLoading = true
                                val notesText = notebook.notes.joinToString("\n\n") { "- ${it.content}" }
                                val socraticPrompt = """
                                    You are the Socratic Notebook Coach of Chhatra Bandhu AI.
                                    The student is asking: "${qaPrompt.trim()}"
                                    
                                    Here are the context study notes in their notebook:
                                    $notesText
                                    
                                    Answer the question clearly in ${prefs.getLanguage()} by applying Socratic micro-reasoning (RULE 2). Do not write essays or solve homework directly. Guide them step-by-step. Since Google Search grounding is active, please ground the scientific laws or regional Bengali facts correctly, and cite URLs if necessary.
                                """.trimIndent()
                                
                                val answer = GeminiService.generateContent(
                                    prompt = socraticPrompt,
                                    board = prefs.getBoard(),
                                    className = prefs.getClassName(),
                                    subject = prefs.getSubject(),
                                    language = prefs.getLanguage()
                                )
                                qaResponse = answer
                                qaPrompt = ""
                                isQaLoading = false
                            }
                        }
                    })
                )
                
                if (qaPrompt.trim().isNotEmpty()) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                isQaLoading = true
                                val notesText = notebook.notes.joinToString("\n\n") { "- ${it.content}" }
                                val socraticPrompt = """
                                    You are the Socratic Notebook Coach of Chhatra Bandhu AI.
                                    The student is asking: "${qaPrompt.trim()}"
                                    
                                    Here are the context study notes in their notebook:
                                    $notesText
                                    
                                    Answer the question clearly in ${prefs.getLanguage()} by applying Socratic micro-reasoning (RULE 2). Do not write essays or solve homework directly. Guide them step-by-step. Since Google Search grounding is active, please ground the scientific laws or regional Bengali facts correctly, and cite URLs if necessary.
                                """.trimIndent()
                                
                                val answer = GeminiService.generateContent(
                                    prompt = socraticPrompt,
                                    board = prefs.getBoard(),
                                    className = prefs.getClassName(),
                                    subject = prefs.getSubject(),
                                    language = prefs.getLanguage()
                                )
                                qaResponse = answer
                                qaPrompt = ""
                                isQaLoading = false
                            }
                        },
                        modifier = Modifier.align(Alignment.End),
                        enabled = !isQaLoading
                    ) {
                        Text("Search & Ask")
                    }
                }
                
                if (isQaLoading) {
                    Box(modifier = Modifier.fillMaxWidth().padding(12.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.5.dp)
                    }
                } else if (qaResponse.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFE8F0FE), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFFD2E3FC), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Answer", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Chhatra Bandhu AI Coach:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            BoldParseText(line = qaResponse, textColor = Color.Black, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuizTab(
    notebook: Notebook,
    prefs: PreferencesManager,
    onUpdateNotebook: (Notebook) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var isGenerating by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "AI Practice Quiz (স্মার্ট মক টেস্ট)",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8B5CF6)
                )
                
                Text(
                    "Assess your understanding with 3 dynamic, interactive MCQs based on your saved syllabus notes. Gain points for correct answers!",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
                
                Button(
                    onClick = {
                        if (notebook.notes.isEmpty()) {
                            return@Button
                        }
                        coroutineScope.launch {
                            isGenerating = true
                            val notesText = notebook.notes.joinToString("\n\n") { "- ${it.content}" }
                            
                            val prompt = """
                                Generate exactly 3 educational multiple-choice questions (MCQs) for practice based on these study notes:
                                $notesText
                                
                                You MUST respond ONLY with a valid JSON array matching this strict schema, no other text or explanation:
                                [
                                  {
                                    "question": "Clear educational question text in Benglish or simple Bengali",
                                    "options": ["Option A", "Option B", "Option C", "Option D"],
                                    "correctIndex": 0,
                                    "explanation": "Detailed pedagogical explanation of why the correct option is true and others are false, written in Bengali and English mixed."
                                  }
                                ]
                                Do not surround with markdown code blocks like ```json. Output plain JSON text.
                            """.trimIndent()
                            
                            val responseText = GeminiService.generateContent(
                                prompt = prompt,
                                board = prefs.getBoard(),
                                className = prefs.getClassName(),
                                subject = prefs.getSubject(),
                                language = prefs.getLanguage()
                            )
                            
                            try {
                                // Strip potential markdown code wrapper if any
                                var cleanJson = responseText.trim()
                                if (cleanJson.startsWith("```")) {
                                    val lines = cleanJson.lines()
                                    cleanJson = lines.filter { !it.startsWith("```") }.joinToString("\n")
                                }
                                cleanJson = cleanJson.trim()
                                
                                val jsonArray = JSONArray(cleanJson)
                                val list = mutableListOf<QuizItem>()
                                for (i in 0 until jsonArray.length()) {
                                    val obj = jsonArray.getJSONObject(i)
                                    val question = obj.getString("question")
                                    val optionsArr = obj.getJSONArray("options")
                                    val options = mutableListOf<String>()
                                    for (j in 0 until optionsArr.length()) {
                                        options.add(optionsArr.getString(j))
                                    }
                                    val correctIndex = obj.getInt("correctIndex")
                                    val explanation = obj.getString("explanation")
                                    list.add(QuizItem(question, options, correctIndex, explanation))
                                }
                                
                                if (list.isNotEmpty()) {
                                    onUpdateNotebook(notebook.copy(quizzes = list))
                                    Toast.makeText(context, "Quiz Created! Solve to earn points!", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Log.e("QuizTab", "Failed parsing quiz JSON: ${e.message}\nRaw: $responseText", e)
                                Toast.makeText(context, "High congestion. Retrying locally...", Toast.LENGTH_SHORT).show()
                            } finally {
                                isGenerating = false
                            }
                        }
                    },
                    enabled = notebook.notes.isNotEmpty() && !isGenerating,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6))
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Drafting quiz questions...")
                    } else {
                        Icon(Icons.Default.PlayArrow, contentDescription = "Start")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (notebook.quizzes.isNotEmpty()) "Regenerate Interactive Quiz" else "Generate 3 Socratic MCQs")
                    }
                }
                
                if (notebook.notes.isEmpty()) {
                    Text(
                        "⚠️ Please add study notes in 'My Notes' tab before generating study quizzes.",
                        fontSize = 11.sp,
                        color = Color.Red.copy(alpha = 0.8f)
                    )
                }
            }
        }
        
        // Render current quizzes
        if (notebook.quizzes.isNotEmpty()) {
            Text(
                "Interactive Practice Board:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
            
            notebook.quizzes.forEachIndexed { qIdx, quiz ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Q${qIdx + 1}: ${quiz.question}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        
                        quiz.options.forEachIndexed { oIdx, option ->
                            val isSelected = quiz.userAnswerIndex == oIdx
                            val isCorrect = quiz.correctIndex == oIdx
                            val alreadyAnswered = quiz.userAnswerIndex != null
                            
                            val optionBgColor = when {
                                isSelected && isCorrect -> Color(0xFFE6F4EA) // Correct selected
                                isSelected && !isCorrect -> Color(0xFFFCE8E6) // Incorrect selected
                                alreadyAnswered && isCorrect -> Color(0xFFE6F4EA) // Show correct answer anyway
                                else -> Color(0xFFF1F3F4)
                            }
                            
                            val optionBorderColor = when {
                                isSelected && isCorrect -> Color(0xFF137333)
                                isSelected && !isCorrect -> Color(0xFFC5221F)
                                else -> Color.Transparent
                            }
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(optionBgColor)
                                    .border(1.dp, optionBorderColor, RoundedCornerShape(8.dp))
                                    .clickable(enabled = !alreadyAnswered) {
                                        quiz.userAnswerIndex = oIdx
                                        if (isCorrect) {
                                            prefs.addPoints(10) // +10 points for correct answer!
                                            Toast.makeText(context, "Correct! 🎉 +10 Points", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "Keep trying! Let's read the explanation.", Toast.LENGTH_SHORT).show()
                                        }
                                        onUpdateNotebook(notebook) // save state
                                    }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.White, CircleShape)
                                        .border(1.dp, Color.Gray, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = ('A'.code + oIdx).toChar().toString(),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else Color.Black
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = option,
                                    fontSize = 13.sp,
                                    color = Color.Black
                                )
                            }
                        }
                        
                        // Show educational feedback / Socratic explanation
                        if (quiz.userAnswerIndex != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF8F9FA), RoundedCornerShape(6.dp))
                                    .padding(10.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = if (quiz.userAnswerIndex == quiz.correctIndex) "Excellent! Correct." else "Let's learn why:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (quiz.userAnswerIndex == quiz.correctIndex) Color(0xFF137333) else Color(0xFFC5221F)
                                    )
                                    Text(
                                        text = quiz.explanation,
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

// Clickable link parser / text parser
@Composable
fun BoldParseText(
    line: String,
    textColor: Color,
    fontSize: androidx.compose.ui.unit.TextUnit
) {
    val uriHandler = LocalUriHandler.current
    
    val annotatedString = buildAnnotatedString {
        val tokenRegex = """(\*\*.*?\*\*|\[.*?\]\(.*?\))""".toRegex()
        var lastIdx = 0
        
        tokenRegex.findAll(line).forEach { matchResult ->
            val matchStart = matchResult.range.first
            val matchEnd = matchResult.range.last + 1
            val matchText = matchResult.value
            
            if (matchStart > lastIdx) {
                append(line.substring(lastIdx, matchStart))
            }
            
            if (matchText.startsWith("**") && matchText.endsWith("**")) {
                val boldContent = matchText.substring(2, matchText.length - 2)
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(boldContent)
                }
            } else if (matchText.startsWith("[") && matchText.contains("](")) {
                val closeBracketIdx = matchText.indexOf("](")
                val linkText = matchText.substring(1, closeBracketIdx)
                val url = matchText.substring(closeBracketIdx + 2, matchText.length - 1)
                
                pushStringAnnotation(tag = "URL", annotation = url)
                withStyle(
                    style = SpanStyle(
                        color = Color(0xFF1A73E8),
                        textDecoration = TextDecoration.Underline,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(linkText)
                }
                pop()
            }
            lastIdx = matchEnd
        }
        
        if (lastIdx < line.length) {
            append(line.substring(lastIdx))
        }
    }
    
    androidx.compose.foundation.text.ClickableText(
        text = annotatedString,
        style = androidx.compose.ui.text.TextStyle(
            color = textColor,
            fontSize = fontSize,
            lineHeight = 18.sp
        ),
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    try {
                        uriHandler.openUri(annotation.item)
                    } catch (e: Exception) {
                        Log.e("BoldParseText", "Error opening URI: ${annotation.item}", e)
                    }
                }
        }
    )
}

// Storage loaders
fun loadNotebooks(prefs: PreferencesManager): List<Notebook> {
    try {
        val jsonStr = prefs.getNotebooksJson()
        val jsonArray = JSONArray(jsonStr)
        val list = mutableListOf<Notebook>()
        for (i in 0 until jsonArray.length()) {
            val notebookObj = jsonArray.getJSONObject(i)
            val id = notebookObj.getString("id")
            val title = notebookObj.getString("title")
            val createdAt = notebookObj.getLong("createdAt")
            
            // Notes parsing
            val notesArr = notebookObj.getJSONArray("notes")
            val notes = mutableListOf<NoteItem>()
            for (j in 0 until notesArr.length()) {
                val noteObj = notesArr.getJSONObject(j)
                notes.add(
                    NoteItem(
                        id = noteObj.getString("id"),
                        content = noteObj.getString("content"),
                        timestamp = noteObj.getLong("timestamp")
                    )
                )
            }
            
            // AI summary
            val aiSummary = if (notebookObj.isNull("aiSummary")) null else notebookObj.getString("aiSummary")
            
            // Quizzes parsing
            val quizArr = notebookObj.getJSONArray("quizzes")
            val quizzes = mutableListOf<QuizItem>()
            for (k in 0 until quizArr.length()) {
                val qObj = quizArr.getJSONObject(k)
                val qText = qObj.getString("question")
                val optArr = qObj.getJSONArray("options")
                val options = mutableListOf<String>()
                for (x in 0 until optArr.length()) {
                    options.add(optArr.getString(x))
                }
                val correctIndex = qObj.getInt("correctIndex")
                val explanation = qObj.getString("explanation")
                val userAnswerIndex = if (qObj.isNull("userAnswerIndex")) null else qObj.getInt("userAnswerIndex")
                quizzes.add(QuizItem(qText, options, correctIndex, explanation, userAnswerIndex))
            }
            
            list.add(Notebook(id, title, createdAt, notes, aiSummary, quizzes))
        }
        return list
    } catch (e: Exception) {
        Log.e("NotebookStorage", "Error loading notebooks: ${e.message}", e)
        return emptyList()
    }
}

fun saveNotebooks(prefs: PreferencesManager, notebooks: List<Notebook>) {
    try {
        val jsonArray = JSONArray()
        notebooks.forEach { notebook ->
            val notebookObj = JSONObject()
            notebookObj.put("id", notebook.id)
            notebookObj.put("title", notebook.title)
            notebookObj.put("createdAt", notebook.createdAt)
            
            // Notes serialization
            val notesArr = JSONArray()
            notebook.notes.forEach { note ->
                val noteObj = JSONObject()
                noteObj.put("id", note.id)
                noteObj.put("content", note.content)
                noteObj.put("timestamp", note.timestamp)
                notesArr.put(noteObj)
            }
            notebookObj.put("notes", notesArr)
            
            notebookObj.put("aiSummary", notebook.aiSummary ?: JSONObject.NULL)
            
            // Quizzes serialization
            val quizArr = JSONArray()
            notebook.quizzes.forEach { quiz ->
                val qObj = JSONObject()
                qObj.put("question", quiz.question)
                val optArr = JSONArray()
                quiz.options.forEach { optArr.put(it) }
                qObj.put("options", optArr)
                qObj.put("correctIndex", quiz.correctIndex)
                qObj.put("explanation", quiz.explanation)
                qObj.put("userAnswerIndex", quiz.userAnswerIndex ?: JSONObject.NULL)
                quizArr.put(qObj)
            }
            notebookObj.put("quizzes", quizArr)
            
            jsonArray.put(notebookObj)
        }
        prefs.saveNotebooksJson(jsonArray.toString())
    } catch (e: Exception) {
        Log.e("NotebookStorage", "Error saving notebooks: ${e.message}", e)
    }
}
