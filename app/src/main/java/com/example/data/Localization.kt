package com.example.data

object Localization {
    private val translations = mapOf(
        "app_title" to mapOf(
            "English" to "Chhatra Bandhu AI",
            "Bengali" to "ছাত্রবন্ধু এআই",
            "Benglish" to "ছাত্রবন্ধু এআই",
            "Hindi" to "छात्र बंधु एआई"
        ),
        "app_subtitle" to mapOf(
            "English" to "Tuitioni Educational Companion",
            "Bengali" to "টুইশনি শিক্ষামূলক সহকারী",
            "Benglish" to "টুইশনি শিক্ষামূলক সহকারী",
            "Hindi" to "ट्यूशन शैक्षिक साथी"
        ),
        "welcome_title" to mapOf(
            "English" to "Welcome, Socratic Learner! 👋",
            "Bengali" to "স্বাগতম, ছাত্রবন্ধু! 👋",
            "Benglish" to "স্বাগতম, ছাত্রবন্ধু! 👋",
            "Hindi" to "स्वागत है, छात्र बंधु! 👋"
        ),
        "welcome_desc" to mapOf(
            "English" to "Your Socratic private coach on the Tuitioni Platform is ready to support you.",
            "Bengali" to "টুইশনি প্ল্যাটফর্মে আপনার সক্রেটিক প্রাইভেট কোচ আপনাকে সাহায্য করার জন্য প্রস্তুত।",
            "Benglish" to "টুইশনি প্ল্যাটফর্মে আপনার সক্রেটিক প্রাইভেট কোচ আপনাকে সাহায্য করার জন্য প্রস্তুত।",
            "Hindi" to "ट्यूशन प्लेटफॉर्म पर आपका सुकराती निजी कोच आपकी मदद के लिए तैयार है।"
        ),
        "modules_header" to mapOf(
            "English" to "Syllabus Study Modules:",
            "Bengali" to "সিলেবাস স্টাডি মডিউল (গৌরব পাঠশালা):",
            "Benglish" to "সিলেবাস স্টাডি মডিউল (গৌরব পাঠশালা):",
            "Hindi" to "पाठ्यक्रम अध्ययन मॉड्यूल (गौरव पाठशाला):"
        ),
        "academic_board" to mapOf(
            "English" to "Academic Board:",
            "Bengali" to "একাডেমিক বোর্ড:",
            "Benglish" to "একাডেমিক বোর্ড:",
            "Hindi" to "शैक्षणिक बोर्ड:"
        ),
        "standard_class" to mapOf(
            "English" to "Standard Class:",
            "Bengali" to "শ্রেণী:",
            "Benglish" to "শ্রেণী:",
            "Hindi" to "कक्षा:"
        ),
        "subject_focus" to mapOf(
            "English" to "Subject Focus:",
            "Bengali" to "বিষয়:",
            "Benglish" to "বিষয়:",
            "Hindi" to "विषय:"
        ),
        "linguistic_pref" to mapOf(
            "English" to "Linguistic Preference:",
            "Bengali" to "ভাষা পছন্দ:",
            "Benglish" to "ভাষা পছন্দ:",
            "Hindi" to "भाषा प्राथमिकता:"
        ),
        "save_profile" to mapOf(
            "English" to "Save Profile",
            "Bengali" to "প্রোফাইল সেভ করুন",
            "Benglish" to "প্রোফাইল সেভ করুন",
            "Hindi" to "प्रोफ़ाइल सहेजें"
        ),
        "select_curriculum" to mapOf(
            "English" to "Select Your Curriculum Profile:",
            "Bengali" to "আপনার প্রোফাইল সিলেক্ট করুন:",
            "Benglish" to "আপনার প্রোফাইল সিলেক্ট করুন:",
            "Hindi" to "अपने प्रोफ़ाइल का चयन करें:"
        ),
        
        // Modules
        "m_tutor_title" to mapOf(
            "English" to "Socratic Chat",
            "Bengali" to "ছাত্রবন্ধু সক্রেটিক চ্যাট",
            "Benglish" to "ছাত্রবন্ধু সক্রেটিক চ্যাট",
            "Hindi" to "सुकराती चैट"
        ),
        "m_tutor_desc" to mapOf(
            "English" to "Live Socratic Voice Tutor.",
            "Bengali" to "লাইভ সক্রেটিক ভয়েস টিউটর।",
            "Benglish" to "লাইভ সক্রেটিক ভয়েস টিউটর।",
            "Hindi" to "लाइव सुकराती वॉयस ट्यूटर।"
        ),
        "m_notebook_title" to mapOf(
            "English" to "Gemini Study Notebook",
            "Bengali" to "স্মার্ট স্টাডি নোটবুক",
            "Benglish" to "স্মার্ট স্টাডি নোটবুক",
            "Hindi" to "जेमिनी अध्ययन नोटबुक"
        ),
        "m_notebook_desc" to mapOf(
            "English" to "Interactive study guides, practice quizzes & web search.",
            "Bengali" to "স্মার্ট স্টাডি গাইড, প্র্যাকটিস কুইজ এবং ওয়েব সার্চ।",
            "Benglish" to "স্মার্ট স্টাডি গাইড, প্র্যাকটিস কুইজ এবং ওয়েব সার্চ।",
            "Hindi" to "इंटरैक्टिव अध्ययन गाइड, अभ्यास प्रश्नोत्तरी और वेब खोज।"
        ),
        "m_curriculum_title" to mapOf(
            "English" to "Physics Wallah Mode",
            "Bengali" to "পাঠ্যপুস্তক ও সূত্র মোড",
            "Benglish" to "পাঠ্যপুস্তক ও সূত্র মোড",
            "Hindi" to "भौतिकी और सूत्र मोड"
        ),
        "m_curriculum_desc" to mapOf(
            "English" to "Textbook Hub & formulas.",
            "Bengali" to "ডিজিটাল পাঠ্যপুস্তক ও সূত্র সম্ভার।",
            "Benglish" to "ডিজিটাল পাঠ্যপুস্তক ও সূত্র সম্ভার।",
            "Hindi" to "पाठ्यपुस्तक हब और सूत्र।"
        ),
        "m_labs_title" to mapOf(
            "English" to "BYJU'S Visual Labs",
            "Bengali" to "রান্নাঘর বিজ্ঞান পরীক্ষা",
            "Benglish" to "রান্নাঘর বিজ্ঞান পরীক্ষা",
            "Hindi" to "दृश्य प्रयोगशाला"
        ),
        "m_labs_desc" to mapOf(
            "English" to "Zero-cost kitchen experiments.",
            "Bengali" to "বিনামূল্যে রান্নাঘর বিজ্ঞান পরীক্ষা।",
            "Benglish" to "বিনামূল্যে রান্নাঘর বিজ্ঞান পরীক্ষা।",
            "Hindi" to "शून्य लागत रसोई विज्ञान प्रयोग।"
        ),
        "m_arena_title" to mapOf(
            "English" to "Unacademy Arena",
            "Bengali" to "অনলাইন মক টেস্ট",
            "Benglish" to "অনলাইন মক টেস্ট",
            "Hindi" to "अनएकेडमी अखाड़ा"
        ),
        "m_arena_desc" to mapOf(
            "English" to "Mock tests & global badges.",
            "Bengali" to "মক টেস্ট এবং বৈশ্বিক ব্যাজ।",
            "Benglish" to "মক টেস্ট এবং বৈশ্বিক ব্যাজ।",
            "Hindi" to "मॉक टेस्ट और वैश्विक बैज।"
        ),
        "m_maps_title" to mapOf(
            "English" to "Google Maps Lab",
            "Bengali" to "ভূগোল ও স্থানিক গণিত",
            "Benglish" to "ভূগোল ও স্থানিক গণিত",
            "Hindi" to "गूगल मैप्स लैब"
        ),
        "m_maps_desc" to mapOf(
            "English" to "West Bengal topographic lab.",
            "Bengali" to "পশ্চিমবঙ্গ ভূসংস্থানিক ল্যাব।",
            "Benglish" to "পশ্চিমবঙ্গ ভূসংস্থানিক ল্যাব।",
            "Hindi" to "पश्चिम बंगाल स्थलाकृतिक लैब।"
        ),
        "m_scanner_title" to mapOf(
            "English" to "Socratic Scanner",
            "Bengali" to "ক্যামেরা হোমওয়ার্ক স্ক্যান",
            "Benglish" to "ক্যামেরা হোমওয়ার্ক স্ক্যান",
            "Hindi" to "सुकराती स्कैनर"
        ),
        "m_scanner_desc" to mapOf(
            "English" to "Lens worksheet interpreter.",
            "Bengali" to "ক্যামেরা লেন্সের সাহায্যে হোমওয়ার্ক সমাধান।",
            "Benglish" to "ক্যামেরা লেন্সের সাহায্যে হোমওয়ার্ক সমাধান।",
            "Hindi" to "लेंस वर्कशीट व्याख्याकार।"
        ),
        "m_sponsor_title" to mapOf(
            "English" to "CSR Welfare Hub",
            "Bengali" to "কর্পোরেট গৌরব তহবিল",
            "Benglish" to "কর্পোরেট গৌরব তহবিল",
            "Hindi" to "सीएसआर कल्याण हब"
        ),
        "m_sponsor_desc" to mapOf(
            "English" to "Scholarships & subsidies.",
            "Bengali" to "বৃত্তি এবং শিক্ষাভাতা।",
            "Benglish" to "বৃত্তি এবং শিক্ষাভাতা।",
            "Hindi" to "छात्रवृत्ति और सब्सिडी।"
        )
    )

    fun get(key: String, language: String): String {
        val normalizedLanguage = when (language) {
            "Bengali", "Benglish" -> "Bengali"
            "Hindi" -> "Hindi"
            else -> "English"
        }
        val langMap = translations[key] ?: return key
        return langMap[normalizedLanguage] ?: langMap["English"] ?: key
    }
}
