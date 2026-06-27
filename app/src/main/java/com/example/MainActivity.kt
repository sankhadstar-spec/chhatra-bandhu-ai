package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PreferencesManager
import com.example.ui.DashboardScreen
import com.example.ui.arena.ArenaScreen
import com.example.ui.curriculum.CurriculumScreen
import com.example.ui.labs.LabsScreen
import com.example.ui.maps.MapsScreen
import com.example.ui.scanner.ScannerScreen
import com.example.ui.sponsor.SponsorScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.tutor.TutorScreen
import com.example.ui.notebook.NotebookScreen

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    // Initialize SharedPreferences manager
    val prefs = PreferencesManager(this)

    setContent {
      MyApplicationTheme {
        var currentScreen by remember { mutableStateOf("dashboard") }
        var globalLanguage by remember { mutableStateOf(prefs.getLanguage()) }

        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          Column(
            modifier = Modifier
              .fillMaxSize()
              .padding(innerPadding)
          ) {
            // Persistent global language selection bar
            LanguageSelectionBar(
              currentLanguage = globalLanguage,
              onLanguageSelected = { lang ->
                prefs.setLanguage(lang)
                globalLanguage = lang
              }
            )

            // Dynamic screen container
            Box(modifier = Modifier.weight(1f)) {
              key(globalLanguage) {
                when (currentScreen) {
                  "dashboard" -> {
                    DashboardScreen(
                      prefs = prefs,
                      modifier = Modifier.fillMaxSize(),
                      onNavigate = { screen -> currentScreen = screen }
                    )
                  }
                  "tutor" -> {
                    TutorScreen(
                      prefs = prefs,
                      modifier = Modifier.fillMaxSize(),
                      onBack = { currentScreen = "dashboard" }
                    )
                  }
                  "curriculum" -> {
                    CurriculumScreen(
                      prefs = prefs,
                      modifier = Modifier.fillMaxSize(),
                      onBack = { currentScreen = "dashboard" }
                    )
                  }
                  "labs" -> {
                    LabsScreen(
                      prefs = prefs,
                      modifier = Modifier.fillMaxSize(),
                      onBack = { currentScreen = "dashboard" }
                    )
                  }
                  "maps" -> {
                    MapsScreen(
                      prefs = prefs,
                      modifier = Modifier.fillMaxSize(),
                      onBack = { currentScreen = "dashboard" }
                    )
                  }
                  "arena" -> {
                    ArenaScreen(
                      prefs = prefs,
                      modifier = Modifier.fillMaxSize(),
                      onBack = { currentScreen = "dashboard" }
                    )
                  }
                  "scanner" -> {
                    ScannerScreen(
                      prefs = prefs,
                      modifier = Modifier.fillMaxSize(),
                      onBack = { currentScreen = "dashboard" }
                    )
                  }
                  "sponsor" -> {
                    SponsorScreen(
                      prefs = prefs,
                      modifier = Modifier.fillMaxSize(),
                      onBack = { currentScreen = "dashboard" }
                    )
                  }
                  "notebook" -> {
                    NotebookScreen(
                      prefs = prefs,
                      modifier = Modifier.fillMaxSize(),
                      onBack = { currentScreen = "dashboard" }
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

@Composable
fun LanguageSelectionBar(
  currentLanguage: String,
  onLanguageSelected: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    color = Color(0xFF1E293B), // Premium Deep Slate Background
    contentColor = Color.White,
    modifier = modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Info,
          contentDescription = "Language",
          tint = Color(0xFF38BDF8), // Gemini Sky Blue
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = when (currentLanguage) {
            "Bengali", "Benglish" -> "ভাষা পছন্দ:"
            "Hindi" -> "भाषा प्राथमिकता:"
            else -> "Language:"
          },
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White.copy(alpha = 0.9f)
        )
      }

      Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        val languages = listOf(
          Triple("Bengali", "বাংলা", "🇧🇩"),
          Triple("Hindi", "हिन्दी", "🇮🇳"),
          Triple("English", "English", "🇬🇧")
        )

        languages.forEach { (code, label, flag) ->
          val isSelected = currentLanguage == code || (code == "Bengali" && currentLanguage == "Benglish")
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(
                if (isSelected) Color(0xFF0284C7) else Color.White.copy(alpha = 0.1f)
              )
              .clickable { onLanguageSelected(code) }
              .padding(horizontal = 10.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Text(flag, fontSize = 12.sp)
              Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f)
              )
            }
          }
        }
      }
    }
  }
}

