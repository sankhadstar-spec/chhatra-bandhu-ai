package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PreferencesManager
import com.example.data.Localization

data class ToolItem(
    val id: String,
    val title: String,
    val banglaTitle: String,
    val description: String,
    val icon: ImageVector,
    val tint: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    prefs: PreferencesManager,
    modifier: Modifier = Modifier,
    onNavigate: (String) -> Unit
) {
    // Refresh stats dynamically
    var points by remember { mutableStateOf(prefs.getPoints()) }
    var streak by remember { mutableStateOf(prefs.getStreak()) }

    // Settings States
    var currentBoard by remember { mutableStateOf(prefs.getBoard()) }
    var currentClass by remember { mutableStateOf(prefs.getClassName()) }
    var currentSubject by remember { mutableStateOf(prefs.getSubject()) }
    var currentLanguage by remember { mutableStateOf(prefs.getLanguage()) }

    var showProfileSettings by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        points = prefs.getPoints()
        streak = prefs.getStreak()
    }

    val tools = listOf(
        ToolItem(
            "tutor",
            "Socratic Chat",
            "ছাত্রবন্ধু কথোপকথন",
            "Live Socratic Voice Tutor.",
            Icons.Default.Send,
            MaterialTheme.colorScheme.primary
        ),
        ToolItem(
            "notebook",
            "Gemini Study Notebook",
            "স্মার্ট স্টাডি নোটবুক",
            "Interactive study guides, practice quizzes & web search.",
            Icons.Default.Edit,
            Color(0xFF6366F1) // Indigo / Gemini brand
        ),
        ToolItem(
            "curriculum",
            "Physics Wallah Mode",
            "পাঠ্যপুস্তক ও সূত্র",
            "Textbook Hub & formulas.",
            Icons.Default.List,
            Color(0xFF3B82F6) // Blue
        ),
        ToolItem(
            "labs",
            "BYJU'S Visual Labs",
            "রান্নাঘর বিজ্ঞান পরীক্ষা",
            "Zero-cost kitchen experiments.",
            Icons.Default.Info,
            Color(0xFF10B981) // Emerald
        ),
        ToolItem(
            "arena",
            "Unacademy Arena",
            "অনলাইন মক টেস্ট",
            "Mock tests & global badges.",
            Icons.Default.Search,
            Color(0xFFD97706) // Amber
        ),
        ToolItem(
            "maps",
            "Google Maps Lab",
            "ভূগোল ও স্থানিক গণিত",
            "West Bengal topographic lab.",
            Icons.Default.LocationOn,
            Color(0xFF8B5CF6) // Purple
        ),
        ToolItem(
            "scanner",
            "Socratic Scanner",
            "ক্যামেরা হোমওয়ার্ক স্ক্যান",
            "Lens worksheet interpreter.",
            Icons.Default.Add,
            Color(0xFFEC4899) // Pink
        ),
        ToolItem(
            "sponsor",
            "CSR Welfare Hub",
            "কর্পোরেট গৌরব তহবিল",
            "Scholarships & subsidies.",
            Icons.Default.Favorite,
            Color(0xFF14B8A6) // Teal
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = "Tuitioni",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                Localization.get("app_title", currentLanguage),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                Localization.get("app_subtitle", currentLanguage),
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showProfileSettings = !showProfileSettings }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Syllabus Settings",
                            tint = if (showProfileSettings) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = Color(0xFFFAF9F6) // Warm off-white paper canvas
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Animated Profile settings panel
            AnimatedVisibility(visible = showProfileSettings) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color.LightGray)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            Localization.get("select_curriculum", currentLanguage),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Board selector
                        Column {
                            Text(Localization.get("academic_board", currentLanguage), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                                listOf("WBBSE", "WBCHSE", "CBSE", "ICSE").forEach { board ->
                                    val isSelected = currentBoard == board
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            currentBoard = board
                                            prefs.setBoard(board)
                                        },
                                        label = { Text(board, fontSize = 11.sp) }
                                    )
                                }
                            }
                        }

                        // Class selector
                        Column {
                            Text(Localization.get("standard_class", currentLanguage), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                                listOf("Class 5", "Class 8", "Class 10", "Class 11", "Class 12").forEach { cls ->
                                    val isSelected = currentClass == cls
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            currentClass = cls
                                            prefs.setClassName(cls)
                                        },
                                        label = { Text(cls, fontSize = 11.sp) }
                                    )
                                }
                            }
                        }

                        // Preferred subject
                        Column {
                            Text(Localization.get("subject_focus", currentLanguage), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                                listOf("Physical Science", "Life Science", "Mathematics", "History", "Geography").forEach { subj ->
                                    val isSelected = currentSubject == subj
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            currentSubject = subj
                                            prefs.setSubject(subj)
                                        },
                                        label = { Text(subj, fontSize = 11.sp) }
                                    )
                                }
                            }
                        }

                        // Language selector
                        Column {
                            Text(Localization.get("linguistic_pref", currentLanguage), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                                listOf("Bengali", "English", "Hindi").forEach { lang ->
                                    val isSelected = currentLanguage == lang || (lang == "Bengali" && currentLanguage == "Benglish")
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            currentLanguage = lang
                                            prefs.setLanguage(lang)
                                        },
                                        label = { Text(lang, fontSize = 11.sp) }
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = { showProfileSettings = false },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text(Localization.get("save_profile", currentLanguage), fontSize = 12.sp)
                        }
                    }
                }
            }

            // Student Welcoming Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        Localization.get("welcome_title", currentLanguage),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        Localization.get("welcome_desc", currentLanguage),
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = "School", tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "$currentBoard • $currentClass ($currentLanguage)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                              )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🔥 $streak days • 🪙 $points pts",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD97706)
                            )
                        }
                    }
                }
            }

            // Mode Selector Grid
            Text(
                Localization.get("modules_header", currentLanguage),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
            )

            tools.forEach { tool ->
                Card(
                    onClick = { onNavigate(tool.id) },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
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
                                .clip(RoundedCornerShape(8.dp))
                                .background(tool.tint.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = tool.icon,
                                contentDescription = tool.title,
                                tint = tool.tint,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = Localization.get("m_${tool.id}_title", currentLanguage),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(
                                    text = when (currentLanguage) {
                                        "Bengali", "Benglish" -> "বাংলা"
                                        "Hindi" -> "हिन्दी"
                                        else -> "English"
                                    },
                                    fontSize = 10.sp,
                                    color = tool.tint,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .background(tool.tint.copy(alpha = 0.08f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = Localization.get("m_${tool.id}_desc", currentLanguage),
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
