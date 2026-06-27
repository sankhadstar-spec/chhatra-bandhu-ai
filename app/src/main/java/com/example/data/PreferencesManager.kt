package com.example.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("tuitioni_prefs", Context.MODE_PRIVATE)

    fun getBoard(): String = prefs.getString("selected_board", "WBBSE") ?: "WBBSE"
    fun setBoard(board: String) = prefs.edit().putString("selected_board", board).apply()

    fun getClassName(): String = prefs.getString("selected_class", "Class 10") ?: "Class 10"
    fun setClassName(className: String) = prefs.edit().putString("selected_class", className).apply()

    fun getSubject(): String = prefs.getString("selected_subject", "Physical Science") ?: "Physical Science"
    fun setSubject(subject: String) = prefs.edit().putString("selected_subject", subject).apply()

    fun getLanguage(): String = prefs.getString("selected_language", "Benglish") ?: "Benglish"
    fun setLanguage(language: String) = prefs.edit().putString("selected_language", language).apply()

    fun getStream(): String = prefs.getString("selected_stream", "Science") ?: "Science"
    fun setStream(stream: String) = prefs.edit().putString("selected_stream", stream).apply()

    // Gamification
    fun getPoints(): Int = prefs.getInt("socratic_points", 50) // starts with 50 warm-up points!
    fun addPoints(points: Int) {
        val cur = getPoints()
        prefs.edit().putInt("socratic_points", cur + points).apply()
    }

    fun getStreak(): Int = prefs.getInt("socratic_streak", 1)
    fun incrementStreak() {
        val cur = getStreak()
        prefs.edit().putInt("socratic_streak", cur + 1).apply()
    }

    fun isBadgeUnlocked(badgeId: String): Boolean = prefs.getBoolean("badge_$badgeId", badgeId == "newbie")
    fun unlockBadge(badgeId: String) = prefs.edit().putBoolean("badge_$badgeId", true).apply()

    // Notebook Storage
    fun getNotebooksJson(): String = prefs.getString("study_notebooks_json", "[]") ?: "[]"
    fun saveNotebooksJson(json: String) = prefs.edit().putString("study_notebooks_json", json).apply()
}
