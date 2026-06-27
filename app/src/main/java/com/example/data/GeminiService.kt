package com.example.data

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {
    private const val TAG = "GeminiService"
    private const val MODEL_NAME = "gemini-3.5-flash"
    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun generateContent(
        prompt: String,
        history: List<Pair<String, String>> = emptyList(),
        board: String = "WBBSE",
        className: String = "Class 10",
        subject: String = "Physical Science",
        language: String = "Benglish"
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isEmpty() || apiKey.contains("MY_GEMINI_API_KEY")) {
            return@withContext "Chhatra Bandhu AI is running offline. Please configure your GEMINI_API_KEY in the Secrets panel."
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent?key=$apiKey"

        val systemInstructionText = """
            You are "Chhatra Bandhu AI" (ছাত্রবন্ধু), a realistic, warm, and highly encouraging virtual private tutor embedded in the "Tuitioni" platform, built to democratize education for underprivileged students.
            
            Current context:
            - Student Board: $board
            - Class: $className
            - Subject: $subject
            - Preferred Language: $language
            
            Strictly follow the four "SHANKH" Pedagogical Commandments:
            
            RULE 1: THE SOCRATIC BOUNDARY (Never Do Homework)
            - Trigger: If the student asks to write an essay, write completed scripts, solve homework directly, or give full answers.
            - Action: Explicitly and politely decline. Turn the question back onto the student with an open-ended foundational prompt to activate their brain.
            - Rejection template: "I cannot complete your homework or write that file for you, as my core mission is to sharpen your own brain! However, let's act as partners to build an outstanding assignment together. Tell me, what do you think is the primary cause behind this event?"
            
            RULE 2: MICRO-STEP REASONING (Math, Science, Coding)
            - Action: Break mathematical derivations, formulas, or syntax into simple sequential micro-steps.
            - Rule: Do not show Step N+1 until the student responds to the current step N.
            
            RULE 3: ACADEMIC INTEGRITY & REGIONAL CITATION HUB
            - Action: Teach citation of sources. Suggest standard APA 7th Edition or MLA 9th Edition.
            
            RULE 4: DIY KITCHEN SCIENCE & LOCAL PROJECT INCUBATION
            - Action: Suggest zero-cost, safe, hands-on experiments using household materials (like lemon batteries or turmeric pH indicator).
            
            Linguistic Voice:
            - Default to "Benglish" (a friendly mix of Bengali and English) for West Bengal Board students (WBBSE/WBCHSE). Explain theories in encouraging, localized Bengali prose, but keep technical terms (e.g., Photosynthesis, Momentum, Quadratic Equation) in formal English to prepare them for national standards.
            - If national board (CBSE/ICSE/ISC) is selected, use elegant, academic English, placing emphasis on structured vocabulary and composition.
            
            Always end your message with a Socratic question asking the student to respond or prompt the next step, ensuring they stay active in the learning process!
        """.trimIndent()

        try {
            val jsonBody = JSONObject().apply {
                val contentsArray = JSONArray()

                // Add history
                history.forEach { (role, text) ->
                    val contentObj = JSONObject().apply {
                        put("role", if (role == "user") "user" else "model")
                        val partsArray = JSONArray().apply {
                            put(JSONObject().apply { put("text", text) })
                        }
                        put("parts", partsArray)
                    }
                    contentsArray.put(contentObj)
                }

                // Add current prompt
                contentsArray.put(JSONObject().apply {
                    put("role", "user")
                    val partsArray = JSONArray().apply {
                        put(JSONObject().apply { put("text", prompt) })
                    }
                    put("parts", partsArray)
                })

                put("contents", contentsArray)

                // Add tools for Google Search Grounding
                put("tools", JSONArray().apply {
                    put(JSONObject().apply {
                        put("googleSearch", JSONObject())
                    })
                })

                // Add system instructions
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", systemInstructionText) })
                    })
                })

                // Add configurations
                put("config", JSONObject().apply {
                    put("temperature", 0.7)
                })
            }

            val request = Request.Builder()
                .url(url)
                .addHeader("User-Agent", "aistudio-build")
                .post(jsonBody.toString().toRequestBody(JSON_MEDIA_TYPE))
                .build()

            client.newCall(request).execute().use { response ->
                val responseBodyStr = response.body?.string() ?: ""
                if (!response.isSuccessful) {
                    Log.e(TAG, "Request failed: ${response.code} $responseBodyStr")
                    return@withContext "Chhatra Bandhu AI: Socratic connection issue. (Error code: ${response.code}). Let's continue offline! What part of $subject are we looking at today?"
                }

                val responseJson = JSONObject(responseBodyStr)
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        var generatedText = parts.getJSONObject(0).optString("text", "Let's work through this step-by-step together!")
                        
                        // Parse Grounding Metadata for internet search sources
                        val groundingMetadata = candidate.optJSONObject("groundingMetadata")
                        if (groundingMetadata != null) {
                            val groundingChunks = groundingMetadata.optJSONArray("groundingChunks")
                            if (groundingChunks != null && groundingChunks.length() > 0) {
                                val citations = StringBuilder()
                                citations.append("\n\n📚 **Verified Internet Sources:**\n")
                                val seenUris = mutableSetOf<String>()
                                for (i in 0 until groundingChunks.length()) {
                                    val chunk = groundingChunks.getJSONObject(i)
                                    val web = chunk.optJSONObject("web")
                                    if (web != null) {
                                        val title = web.optString("title")
                                        val uri = web.optString("uri")
                                        if (uri.isNotEmpty() && seenUris.add(uri)) {
                                            citations.append("- [${title.ifEmpty { "Source" }}]($uri)\n")
                                        }
                                    }
                                }
                                if (seenUris.isNotEmpty()) {
                                    generatedText += citations.toString()
                                }
                            }
                        }
                        return@withContext generatedText
                    }
                }
                return@withContext "Let's explore this together! What are your thoughts on the first step of this problem?"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception calling Gemini: ${e.message}", e)
            return@withContext "Chhatra Bandhu AI is running in low-bandwidth local mode due to network latency. Let's work on this topic together! Tell me: what do you already know about this question?"
        }
    }

    suspend fun generateContentStream(
        prompt: String,
        history: List<Pair<String, String>> = emptyList(),
        board: String = "WBBSE",
        className: String = "Class 10",
        subject: String = "Physical Science",
        language: String = "Benglish"
    ): kotlinx.coroutines.flow.Flow<String> = kotlinx.coroutines.flow.flow {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isEmpty() || apiKey.contains("MY_GEMINI_API_KEY")) {
            emit("Chhatra Bandhu AI is running offline. Please configure your GEMINI_API_KEY in the Secrets panel.")
            return@flow
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:streamGenerateContent?key=$apiKey"

        val systemInstructionText = """
            You are "Chhatra Bandhu AI" (ছাত্রবন্ধু), a realistic, warm, and highly encouraging virtual private tutor embedded in the "Tuitioni" platform, built to democratize education for underprivileged students.
            
            Current context:
            - Student Board: $board
            - Class: $className
            - Subject: $subject
            - Preferred Language: $language
            
            Strictly follow the four "SHANKH" Pedagogical Commandments:
            
            RULE 1: THE SOCRATIC BOUNDARY (Never Do Homework)
            - Trigger: If the student asks to write an essay, write completed scripts, solve homework directly, or give full answers.
            - Action: Explicitly and politely decline. Turn the question back onto the student with an open-ended foundational prompt to activate their brain.
            - Rejection template: "I cannot complete your homework or write that file for you, as my core mission is to sharpen your own brain! However, let's act as partners to build an outstanding assignment together. Tell me, what do you think is the primary cause behind this event?"
            
            RULE 2: MICRO-STEP REASONING (Math, Science, Coding)
            - Action: Break mathematical derivations, formulas, or syntax into simple sequential micro-steps.
            - Rule: Do not show Step N+1 until the student responds to the current step N.
            
            RULE 3: ACADEMIC INTEGRITY & REGIONAL CITATION HUB
            - Action: Teach citation of sources. Suggest standard APA 7th Edition or MLA 9th Edition.
            
            RULE 4: DIY KITCHEN SCIENCE & LOCAL PROJECT INCUBATION
            - Action: Suggest zero-cost, safe, hands-on experiments using household materials (like lemon batteries or turmeric pH indicator).
            
            Linguistic Voice:
            - Default to "Benglish" (a friendly mix of Bengali and English) for West Bengal Board students (WBBSE/WBCHSE). Explain theories in encouraging, localized Bengali prose, but keep technical terms (e.g., Photosynthesis, Momentum, Quadratic Equation) in formal English to prepare them for national standards.
            - If national board (CBSE/ICSE/ISC) is selected, use elegant, academic English, placing emphasis on structured vocabulary and composition.
            - When $language is selected, respond and guide in that specific language preference while still maintaining regional terminology.
            
            Always end your message with a Socratic question asking the student to respond or prompt the next step, ensuring they stay active in the learning process!
        """.trimIndent()

        val jsonBody = JSONObject().apply {
            val contentsArray = JSONArray()

            // Add history
            history.forEach { (role, text) ->
                val contentObj = JSONObject().apply {
                    put("role", if (role == "user") "user" else "model")
                    val partsArray = JSONArray().apply {
                        put(JSONObject().apply { put("text", text) })
                    }
                    put("parts", partsArray)
                }
                contentsArray.put(contentObj)
            }

            // Add current prompt
            contentsArray.put(JSONObject().apply {
                put("role", "user")
                val partsArray = JSONArray().apply {
                    put(JSONObject().apply { put("text", prompt) })
                }
                put("parts", partsArray)
            })

            put("contents", contentsArray)

            // Add tools for Google Search Grounding
            put("tools", JSONArray().apply {
                put(JSONObject().apply {
                    put("googleSearch", JSONObject())
                })
            })

            // Add system instructions
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply { put("text", systemInstructionText) })
                })
            })

            // Add configurations
            put("config", JSONObject().apply {
                put("temperature", 0.7)
            })
        }

        val request = Request.Builder()
            .url(url)
            .addHeader("User-Agent", "aistudio-build")
            .post(jsonBody.toString().toRequestBody(JSON_MEDIA_TYPE))
            .build()

        try {
            val response = withContext(Dispatchers.IO) { client.newCall(request).execute() }
            if (!response.isSuccessful) {
                val errBody = response.body?.string() ?: ""
                Log.e(TAG, "Stream Request failed: ${response.code} $errBody")
                emit("Chhatra Bandhu AI: Socratic connection issue. (Error code: ${response.code}). Let's continue offline!")
                response.close()
                return@flow
            }

            val source = response.body?.source()
            if (source == null) {
                emit("Chhatra Bandhu AI: Empty connection stream. Let's work offline!")
                response.close()
                return@flow
            }

            val seenUris = mutableSetOf<String>()
            val citationsList = mutableListOf<Pair<String, String>>()

            withContext(Dispatchers.IO) {
                val reader = java.io.BufferedReader(java.io.InputStreamReader(source.inputStream()))
                var line: String?
                val objectBuffer = StringBuilder()
                var openBraces = 0
                var inString = false
                var escapeNext = false

                while (reader.readLine().also { line = it } != null) {
                    val currentLine = line ?: break
                    
                    for (i in currentLine.indices) {
                        val c = currentLine[i]
                        objectBuffer.append(c)

                        if (escapeNext) {
                            escapeNext = false
                            continue
                        }

                        if (c == '\\') {
                            escapeNext = true
                            continue
                        }

                        if (c == '"') {
                            inString = !inString
                            continue
                        }

                        if (!inString) {
                            if (c == '{') {
                                openBraces++
                            } else if (c == '}') {
                                openBraces--
                                if (openBraces == 0 && objectBuffer.isNotEmpty()) {
                                    val potentialJson = objectBuffer.toString().trim()
                                    var cleanJson = potentialJson
                                    if (cleanJson.startsWith(",")) {
                                        cleanJson = cleanJson.substring(1).trim()
                                    }
                                    try {
                                        val json = JSONObject(cleanJson)
                                        val candidates = json.optJSONArray("candidates")
                                        if (candidates != null && candidates.length() > 0) {
                                            val candidate = candidates.getJSONObject(0)
                                            
                                            // Extract Grounding Metadata
                                            val groundingMetadata = candidate.optJSONObject("groundingMetadata")
                                            if (groundingMetadata != null) {
                                                val groundingChunks = groundingMetadata.optJSONArray("groundingChunks")
                                                if (groundingChunks != null) {
                                                    for (idx in 0 until groundingChunks.length()) {
                                                        val chunk = groundingChunks.getJSONObject(idx)
                                                        val web = chunk.optJSONObject("web")
                                                        if (web != null) {
                                                            val title = web.optString("title")
                                                            val uri = web.optString("uri")
                                                            if (uri.isNotEmpty() && seenUris.add(uri)) {
                                                                citationsList.add(Pair(title, uri))
                                                            }
                                                        }
                                                    }
                                                }
                                            }

                                            val content = candidate.optJSONObject("content")
                                            val parts = content?.optJSONArray("parts")
                                            if (parts != null && parts.length() > 0) {
                                                val text = parts.getJSONObject(0).optString("text")
                                                if (text.isNotEmpty()) {
                                                    emit(text)
                                                }
                                            }
                                        }
                                    } catch (e: Exception) {
                                        Log.w(TAG, "Failed to parse JSON object chunk: ${e.message}")
                                    }
                                    objectBuffer.setLength(0)
                                }
                            }
                        }
                    }
                    if (openBraces > 0) {
                        objectBuffer.append("\n")
                    }
                }
            }
            
            // Emit collected sources at the end of the stream
            if (citationsList.isNotEmpty()) {
                val citationsText = StringBuilder()
                citationsText.append("\n\n📚 **Verified Internet Sources:**\n")
                citationsList.forEach { (title, uri) ->
                    citationsText.append("- [${title.ifEmpty { "Source" }}]($uri)\n")
                }
                emit(citationsText.toString())
            }

            response.close()
        } catch (e: Exception) {
            Log.e(TAG, "Stream Exception: ${e.message}", e)
            emit("Chhatra Bandhu AI: Socratic connection lost. Let's work offline!")
        }
    }
}
