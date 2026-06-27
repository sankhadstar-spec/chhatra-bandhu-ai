package com.example.ui.tutor

import android.speech.tts.TextToSpeech
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.os.Bundle
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.Manifest
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GeminiService
import com.example.data.PreferencesManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

data class Message(
    val id: String,
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorScreen(
    prefs: PreferencesManager,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Chat states
    var inputText by remember { mutableStateOf("") }
    val messages = remember {
        mutableStateListOf(
            Message(
                "welcome",
                "নমস্কার! I am Chhatra Bandhu AI (ছাত্রবন্ধু) on Tuitioni. I am here to guide you step-by-step through your studies. I will never do your homework, but I will help you master any subject!\n\nTo begin, please tell me: **What is your Class, Subject, and Board?** Let's learn together!",
                isUser = false
            )
        )
    }
    var isGenerating by remember { mutableStateOf(false) }
    var currentLanguage by remember { mutableStateOf(prefs.getLanguage()) }

    // TTS states
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    var isTtsEnabled by remember { mutableStateOf(true) }
    var isSpeaking by remember { mutableStateOf(false) }

    // Speech simulation states
    var isRecordingMode by remember { mutableStateOf(false) }
    var isRecordingActive by remember { mutableStateOf(false) }
    var recordingTimer by remember { mutableStateOf(0) }
    var speechErrorText by remember { mutableStateOf<String?>(null) }

    // Initialize Native Android Text-to-Speech
    DisposableEffect(Unit) {
        val initializedTts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Try setting language to Bengali first, or fallback to Indian English
                val bengaliLocale = Locale("bn", "IN")
                val result = tts?.setLanguage(bengaliLocale)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts?.setLanguage(Locale("en", "IN"))
                }
            }
        }
        tts = initializedTts

        onDispose {
            initializedTts.stop()
            initializedTts.shutdown()
        }
    }

    // Speak a text
    val speakText = { text: String ->
        if (isTtsEnabled) {
            tts?.stop()
            isSpeaking = true
            // Strip markdown chars from spoken text
            val cleanText = text
                .replace("**", "")
                .replace("*", "")
                .replace("`", "")
                .replace("#", "")
            
            val params = android.os.Bundle()
            params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "ChhatraSpeech")
            tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, params, "ChhatraSpeech")
            
            // Auto turn-off speaking state after estimation or callback
            coroutineScope.launch {
                delay((cleanText.length * 60L).coerceAtLeast(1500L))
                isSpeaking = false
            }
        }
    }

    // Stop speaking
    val stopSpeaking = {
        tts?.stop()
        isSpeaking = false
    }

    // Auto-scroll on new message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Real Speech Recognizer Engine Setup
    val speechRecognizer = remember { SpeechRecognizer.createSpeechRecognizer(context) }
    
    DisposableEffect(speechRecognizer) {
        onDispose {
            try {
                speechRecognizer.destroy()
            } catch (e: Exception) {
                Log.e("TutorScreen", "Error destroying speech recognizer", e)
            }
        }
    }

    val recognitionListener = remember {
        object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                speechErrorText = null
            }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {
                isRecordingActive = false
            }
            override fun onError(error: Int) {
                isRecordingActive = false
                val errMsg = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
                    SpeechRecognizer.ERROR_CLIENT -> "Client error"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Permission required"
                    SpeechRecognizer.ERROR_NETWORK -> "Network error"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized. Speak clearly."
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Service busy, try again"
                    SpeechRecognizer.ERROR_SERVER -> "Server error"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected"
                    else -> "Speech recognition failed"
                }
                speechErrorText = errMsg
                Log.e("TutorScreen", "Speech Recognizer Error: $errMsg (code $error)")
            }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val recognizedText = matches[0]
                    inputText = recognizedText
                    speechErrorText = null
                    isRecordingMode = false
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    inputText = matches[0]
                }
            }
            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }

    LaunchedEffect(speechRecognizer) {
        speechRecognizer.setRecognitionListener(recognitionListener)
    }

    val startListening = {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, when (currentLanguage) {
                "Benglish" -> "bn-IN"
                "Hindi" -> "hi-IN"
                else -> "en-US"
            })
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }
        try {
            speechRecognizer.startListening(intent)
            isRecordingActive = true
            speechErrorText = null
        } catch (e: Exception) {
            speechErrorText = "Mic speech not supported: ${e.localizedMessage}"
            Log.e("TutorScreen", "Error starting speech recognition", e)
        }
    }

    val stopListening = {
        try {
            speechRecognizer.stopListening()
        } catch (e: Exception) {
            Log.e("TutorScreen", "Error stopping speech recognition", e)
        }
        isRecordingActive = false
    }

    val hasAudioPermission = {
        ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            isRecordingMode = true
            startListening()
        } else {
            Toast.makeText(context, "Microphone permission is required for voice input", Toast.LENGTH_SHORT).show()
        }
    }

    // API Generation helper
    val triggerResponse = { prompt: String ->
        if (prompt.isNotBlank() && !isGenerating) {
            val userMsg = Message(java.util.UUID.randomUUID().toString(), prompt, isUser = true)
            messages.add(userMsg)
            inputText = ""
            isGenerating = true
            stopSpeaking()

            // Build history
            val history = messages.dropLast(1).map { Pair(if (it.isUser) "user" else "model", it.text) }

            coroutineScope.launch {
                val board = prefs.getBoard()
                val className = prefs.getClassName()
                val subject = prefs.getSubject()
                val language = currentLanguage

                val botMessageId = java.util.UUID.randomUUID().toString()
                val botMsg = Message(botMessageId, "", isUser = false)
                messages.add(botMsg)

                var accumulatedReply = ""
                try {
                    GeminiService.generateContentStream(
                        prompt = prompt,
                        history = history,
                        board = board,
                        className = className,
                        subject = subject,
                        language = language
                    ).collect { chunk ->
                        accumulatedReply += chunk
                        val index = messages.indexOfFirst { it.id == botMessageId }
                        if (index != -1) {
                            messages[index] = messages[index].copy(text = accumulatedReply)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("TutorScreen", "Error collecting stream: ${e.message}", e)
                    if (accumulatedReply.isEmpty()) {
                        val index = messages.indexOfFirst { it.id == botMessageId }
                        if (index != -1) {
                            messages[index] = messages[index].copy(text = "Chhatra Bandhu AI: Socratic connection issue. Let's work offline!")
                        }
                    }
                } finally {
                    isGenerating = false
                    
                    // Add streak points for participating in Socratic reasoning
                    prefs.addPoints(10)
                    if (messages.size % 4 == 0) {
                        prefs.incrementStreak()
                    }

                    // Auto speak Chhatra Bandhu's Socratic wisdom at the end of streaming
                    if (accumulatedReply.isNotEmpty()) {
                        speakText(accumulatedReply)
                    }
                }
            }
        }
    }

    // Speech recording timer effect
    LaunchedEffect(isRecordingActive) {
        if (isRecordingActive) {
            recordingTimer = 0
            while (isRecordingActive) {
                delay(1000)
                recordingTimer++
            }
        }
    }

    // Audio Wave animation for recording mode
    val infiniteTransition = rememberInfiniteTransition(label = "audioWave")
    val waveScale1 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave1"
    )
    val waveScale2 by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave2"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Chhatra Bandhu AI (ছাত্রবন্ধু)",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            "Socratic Private Tutor • Live",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { isTtsEnabled = !isTtsEnabled }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Toggle TTS",
                            tint = if (isTtsEnabled) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                    if (isSpeaking) {
                        IconButton(onClick = { stopSpeaking() }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Stop Speech",
                                tint = Color.Red
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        containerColor = Color(0xFFFAF9F6) // Warm paper background
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Context Info Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f))
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = "Academic details",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${prefs.getBoard()} • ${prefs.getClassName()} • ${prefs.getSubject()}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.DarkGray
                    )
                }
                Text(
                    text = "🔥 Streak: ${prefs.getStreak()} days • 🪙 ${prefs.getPoints()} pts",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD97706) // Dark amber
                )
            }

            // Interactive Language Selector Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Language / ভাষা:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                
                listOf("Benglish", "English", "Hindi").forEach { lang ->
                    val isSelected = currentLanguage == lang
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                else Color(0xFFF3F4F6)
                            )
                            .clickable {
                                currentLanguage = lang
                                prefs.setLanguage(lang)
                            }
                            .border(
                                width = 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = when (lang) {
                                "Benglish" -> "Benglish (বাংলা + Eng)"
                                "English" -> "English"
                                "Hindi" -> "Hindi (हिंदी)"
                                else -> lang
                            },
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.DarkGray
                        )
                    }
                }
            }

            // Quick Starter Socratic Prompts (only visible when message log is short)
            if (messages.size <= 2) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        "Try standard Socratic dialogues with Chhatra Bandhu:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val starters = listOf(
                            "Photosynthesis in Benglish" to "Please explain Photosynthesis to me in Benglish.",
                            "Derive v=u+at" to "Derive v = u + at step-by-step.",
                            "APA Bishnupur Citation" to "How do I cite the Bishnupur Heritage using APA 7th Edition?"
                        )
                        starters.forEach { (label, qText) ->
                            Card(
                                onClick = { triggerResponse(qText) },
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                                ),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.primary,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Chat conversation log
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                state = listState,
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(messages) { msg ->
                    ChatBubble(message = msg)
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (isGenerating) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                "Chhatra Bandhu is listening and reasoning...",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Input Cockpit (Text and Voice triggers)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    if (isRecordingMode) {
                        // Voice Interactive Panel
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .background(Color(0xFFF0FDF4), RoundedCornerShape(12.dp)) // soft green
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isRecordingActive) "Chhatra Bandhu is listening..." else "Tap Mic to speak verbally",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF15803D)
                                )
                                Text(
                                    text = when {
                                        speechErrorText != null -> speechErrorText!!
                                        inputText.isNotEmpty() -> "You: $inputText"
                                        isRecordingActive -> "Speak now... (00:${recordingTimer.toString().padStart(2, '0')})"
                                        else -> "Ask questions without typing!"
                                    },
                                    fontSize = 11.sp,
                                    color = if (speechErrorText != null) Color.Red else Color.Gray,
                                    maxLines = 2
                                )
                            }

                            // Pulsing / glowing microphone ring
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(56.dp)
                            ) {
                                if (isRecordingActive) {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .graphicsLayer(scaleX = waveScale1, scaleY = waveScale1)
                                            .clip(CircleShape)
                                            .background(Color(0xFF22C55E).copy(alpha = 0.15f))
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .graphicsLayer(scaleX = waveScale2, scaleY = waveScale2)
                                            .clip(CircleShape)
                                            .background(Color(0xFF22C55E).copy(alpha = 0.25f))
                                    )
                                }

                                FloatingActionButton(
                                    onClick = {
                                        if (isRecordingActive) {
                                            stopListening()
                                        } else {
                                            startListening()
                                        }
                                    },
                                    containerColor = if (isRecordingActive) Color.Red else Color(0xFF22C55E),
                                    contentColor = Color.White,
                                    shape = CircleShape,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Text(
                                        text = if (isRecordingActive) "⏹️" else "🎙️",
                                        fontSize = 18.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (inputText.isNotBlank()) {
                                    IconButton(onClick = {
                                        stopListening()
                                        triggerResponse(inputText)
                                        isRecordingMode = false
                                    }) {
                                        Icon(
                                            Icons.Default.Send,
                                            contentDescription = "Send Transcribed Text",
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                                IconButton(onClick = {
                                    stopListening()
                                    isRecordingMode = false
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Keyboard input", tint = Color.Gray)
                                }
                            }
                        }
                    } else {
                        // Standard Text Field Input
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                if (hasAudioPermission()) {
                                    isRecordingMode = true
                                    startListening()
                                } else {
                                    permissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                                }
                            }) {
                                Text(
                                    text = "🎙️",
                                    fontSize = 22.sp
                                )
                            }

                            OutlinedTextField(
                                value = inputText,
                                onValueChange = { inputText = it },
                                placeholder = { Text("Ask your private tutor... (e.g. explain gravity)") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(24.dp),
                                maxLines = 3,
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Send
                                ),
                                keyboardActions = KeyboardActions(
                                    onSend = {
                                        if (inputText.isNotBlank()) {
                                            keyboardController?.hide()
                                            triggerResponse(inputText)
                                        }
                                    }
                                ),
                                trailingIcon = {
                                    if (inputText.isNotBlank()) {
                                        IconButton(onClick = {
                                            keyboardController?.hide()
                                            triggerResponse(inputText)
                                        }) {
                                            Icon(
                                                Icons.Default.Send,
                                                contentDescription = "Send",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = Color.LightGray
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: Message) {
    val bubbleColor = if (message.isUser) {
        MaterialTheme.colorScheme.primary
    } else {
        Color.White
    }

    val textColor = if (message.isUser) {
        Color.White
    } else {
        Color.Black
    }

    val alignment = if (message.isUser) Alignment.End else Alignment.Start
    val shape = if (message.isUser) {
        RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 290.dp)
                .shadow(
                    elevation = if (message.isUser) 1.dp else 2.dp,
                    shape = shape
                )
                .background(bubbleColor, shape)
                .padding(14.dp)
        ) {
            Column {
                if (!message.isUser) {
                    Text(
                        "ছাত্রবন্ধু",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                
                // Markdown Parser Simulation for Bold Text (**text**) and Line Breaks
                MarkdownText(
                    text = message.text,
                    textColor = textColor,
                    fontSize = 14.sp
                )
            }
        }
        Text(
            text = if (message.isUser) "You" else "Chhatra Bandhu AI",
            fontSize = 9.sp,
            color = Color.LightGray,
            modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
        )
    }
}

@Composable
fun MarkdownText(
    text: String,
    textColor: Color,
    fontSize: androidx.compose.ui.unit.TextUnit
) {
    // Basic Markdown Simulation
    // Supports bold (**text**), lists, and formulas
    Column {
        val lines = text.split("\n")
        lines.forEach { line ->
            if (line.trim().startsWith("-") || line.trim().startsWith("* ")) {
                Row(modifier = Modifier.padding(start = 8.dp, top = 2.dp, bottom = 2.dp)) {
                    Text("•", color = textColor, fontSize = fontSize)
                    Spacer(modifier = Modifier.width(6.dp))
                    val cleanLine = line.trim().substring(1).trim()
                    BoldParseText(cleanLine, textColor, fontSize)
                }
            } else {
                if (line.isNotEmpty()) {
                    BoldParseText(line, textColor, fontSize)
                    Spacer(modifier = Modifier.height(3.dp))
                }
            }
        }
    }
}

@Composable
fun BoldParseText(
    line: String,
    textColor: Color,
    fontSize: androidx.compose.ui.unit.TextUnit
) {
    val uriHandler = LocalUriHandler.current
    
    val annotatedString = buildAnnotatedString {
        var remaining = line
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
                        color = MaterialTheme.colorScheme.primary,
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
