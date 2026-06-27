package com.example.data

data class Chapter(
    val id: String,
    val title: String,
    val banglaTitle: String,
    val keyConcepts: List<String>,
    val formulas: List<Pair<String, String>> = emptyList(),
    val quickSelfTest: List<Question> = emptyList()
)

data class Question(
    val id: String,
    val text: String,
    val options: List<String>,
    val correctAnswerIdx: Int,
    val socraticHint: String,
    val explanation: String
)

data class Textbook(
    val id: String,
    val title: String,
    val banglaTitle: String,
    val board: String,
    val className: String,
    val subject: String,
    val chapters: List<Chapter>,
    val downloadUrl: String? = null
)

data class PYQ(
    val year: Int,
    val subject: String,
    val questions: List<Pair<String, String>> // Pair of Question and Socratic guiding hint
)

data class DIYExperiment(
    val title: String,
    val objective: String,
    val householdMaterials: List<String>,
    val steps: List<String>,
    val scientificPrinciple: String,
    val socraticQuestion: String
)

data class GeogChallenge(
    val region: String,
    val title: String,
    val description: String,
    val coordinates: String,
    val socraticQuest: String,
    val activeExercise: String
)

object CurriculumData {

    val textbooks = listOf(
        Textbook(
            id = "amar_ganit",
            title = "Amar Ganit",
            banglaTitle = "আমার গণিত",
            board = "WBBSE",
            className = "Class 5",
            subject = "Mathematics",
            chapters = listOf(
                Chapter(
                    id = "ag_ch1",
                    title = "Concept of Numbers and Place Value",
                    banglaTitle = "স্থানীয় মান ও সংখ্যার ধারণা",
                    keyConcepts = listOf("Place Value (স্থানীয় মান)", "Face Value (প্রকৃত মান)", "Addition & Subtraction of large numbers"),
                    quickSelfTest = listOf(
                        Question(
                            "ag_q1",
                            "In 34,562, what is the place value of 4?",
                            listOf("400", "4,000", "40,000", "4"),
                            1,
                            "Remember, place value tells us how much the digit is worth based on where it sits. What position is '4' occupying?",
                            "The digit 4 sits in the thousands place. Therefore, its place value is 4 * 1000 = 4,000."
                        )
                    )
                ),
                Chapter(
                    id = "ag_ch2",
                    title = "Multiplication and Division",
                    banglaTitle = "গুণ ও ভাগ",
                    keyConcepts = listOf("Product (গুণফল)", "Quotient (ভাগফল)", "Remainder (ভাগশেষ)", "Dividend (ভাজ্য) = Divisor (ভাজক) * Quotient + Remainder"),
                    formulas = listOf(
                        "Dividend Formula" to "Dividend (ভাজ্য) = (Divisor (ভাজক) × Quotient (ভাগফল)) + Remainder (ভাগশেষ)"
                    )
                )
            ),
            downloadUrl = "https://banglarshiksha.wb.gov.in/Frontend/e_textbook"
        ),
        Textbook(
            id = "pata_bahar_5",
            title = "Pata Bahar",
            banglaTitle = "পাতাবাহার",
            board = "WBBSE",
            className = "Class 5",
            subject = "Bengali Literature",
            chapters = listOf(
                Chapter(
                    id = "pb5_ch1",
                    title = "Galpoburor Golpo",
                    banglaTitle = "গল্পবুড়োর গল্প (সুনির্মল বসু)",
                    keyConcepts = listOf("Bengali folk tales", "Winter morning scenes", "Creative imagination in verse"),
                    quickSelfTest = emptyList()
                )
            ),
            downloadUrl = "https://banglarshiksha.wb.gov.in/Frontend/e_textbook"
        ),
        Textbook(
            id = "ganit_prakash_10",
            title = "Ganit Prakash",
            banglaTitle = "গণিত প্রকাশ",
            board = "WBBSE",
            className = "Class 10",
            subject = "Mathematics",
            chapters = listOf(
                Chapter(
                    id = "gp_ch1",
                    title = "Quadratic Equations in One Variable",
                    banglaTitle = "একচলবিশিষ্ট দ্বিঘাত সমীকরণ",
                    keyConcepts = listOf("Standard Form ax² + bx + c = 0", "Sridhar Acharya formula (শ্রীধর আচার্যের সূত্র)", "Nature of Roots (নিরূপক b²-4ac)"),
                    formulas = listOf(
                        "Quadratic Formula" to "x = (-b ± √(b² - 4ac)) / 2a",
                        "Discriminant" to "D = b² - 4ac (D > 0: Real & Distinct, D = 0: Real & Equal, D < 0: Imaginary)"
                    ),
                    quickSelfTest = listOf(
                        Question(
                            "gp_q1",
                            "What is the discriminant of 2x² - 5x + 3 = 0?",
                            listOf("1", "25", "2", "4"),
                            0,
                            "Use the discriminant formula: D = b² - 4ac. Here a = 2, b = -5, and c = 3.",
                            "D = (-5)² - 4(2)(3) = 25 - 24 = 1. Since D > 0, roots are real and distinct."
                        )
                    )
                )
            ),
            downloadUrl = "https://banglarshiksha.wb.gov.in/Frontend/e_textbook"
        ),
        Textbook(
            id = "bhauta_bijnan_10",
            title = "Bhauta Bijnan",
            banglaTitle = "ভৌত বিজ্ঞান",
            board = "WBBSE",
            className = "Class 10",
            subject = "Physical Science",
            chapters = listOf(
                Chapter(
                    id = "bb_ch1",
                    title = "Behavior of Gases",
                    banglaTitle = "গ্যাসের আচরণ",
                    keyConcepts = listOf("Boyle's Law (বয়েলের সূত্র)", "Charles's Law (চার্লসের সূত্র)", "Ideal Gas Equation (আদর্শ গ্যাস সমীকরণ)"),
                    formulas = listOf(
                        "Boyle's Law" to "P₁V₁ = P₂V₂ (at constant Temperature 'T')",
                        "Charles's Law" to "V₁/T₁ = V₂/T₂ (at constant Pressure 'P')",
                        "Ideal Gas Equation" to "PV = nRT (where R is the Universal Gas Constant)"
                    ),
                    quickSelfTest = listOf(
                        Question(
                            "bb_q1",
                            "If the volume of a gas is doubled at constant temperature, what happens to its pressure?",
                            listOf("Doubles", "Halves", "Stays the same", "Quadruples"),
                            1,
                            "Think about Boyle's Law: P * V is constant. If V becomes 2V, what must happen to P so that the product remains unchanged?",
                            "By Boyle's Law, pressure is inversely proportional to volume. Doubling volume halves the pressure."
                        )
                    )
                ),
                Chapter(
                    id = "bb_ch2",
                    title = "Light and Refraction",
                    banglaTitle = "আলো",
                    keyConcepts = listOf("Reflection (প্রতিফলন)", "Refraction & Snell's Law (প্রতিসরণ ও স্নেলের সূত্র)", "Convex & Concave Mirrors (উত্তল ও অবতল দর্পণ)"),
                    formulas = listOf(
                        "Refractive Index (प्रतिसरणांक)" to "μ = sin(i) / sin(r) = c/v",
                        "Mirror Formula" to "1/f = 1/v + 1/u",
                        "Power of Lens" to "P = 1 / f (in meters)"
                    )
                )
            ),
            downloadUrl = "https://banglarshiksha.wb.gov.in/Frontend/e_textbook"
        ),
        Textbook(
            id = "english_bliss_10",
            title = "English (Bliss)",
            banglaTitle = "ইংরেজি (ব্লিস)",
            board = "WBBSE",
            className = "Class 10",
            subject = "English",
            chapters = listOf(
                Chapter(
                    id = "eb_ch1",
                    title = "Father's Help",
                    banglaTitle = "ফাদার্স হেল্প (আর. কে. নারায়ণ)",
                    keyConcepts = listOf("Swami's hesitation to go to school", "Father's letter to headmaster", "Internal guilt and perception vs reality"),
                    quickSelfTest = emptyList()
                )
            ),
            downloadUrl = "https://banglarshiksha.wb.gov.in/Frontend/e_textbook"
        ),
        Textbook(
            id = "math_11",
            title = "Higher Secondary Calculus & Algebra",
            banglaTitle = "কলনবিদ্যা ও বীজগণিত",
            board = "WBCHSE",
            className = "Class 11",
            subject = "Mathematics",
            chapters = listOf(
                Chapter(
                    id = "cal_ch1",
                    title = "Limits and Continuity",
                    banglaTitle = "সীমা ও সন্ততা",
                    keyConcepts = listOf("Limit definition", "Standard Limits", "Left hand and Right hand Limits"),
                    formulas = listOf(
                        "Standard Limit 1" to "lim (x→0) [sin(x) / x] = 1",
                        "Standard Limit 2" to "lim (x→a) [(xⁿ - aⁿ) / (x - a)] = n * aⁿ⁻¹"
                    )
                )
            ),
            downloadUrl = "https://wbchse.wb.gov.in/approved-books/"
        ),
        Textbook(
            id = "english_mindscapes_11",
            title = "English (Mindscapes)",
            banglaTitle = "ইংরেজি (মাইন্ডস্কেপস)",
            board = "WBCHSE",
            className = "Class 11",
            subject = "English",
            chapters = listOf(
                Chapter(
                    id = "em11_ch1",
                    title = "Leela's Friend",
                    banglaTitle = "লীলাস ফ্রেন্ড (আর. কে. নারায়ণ)",
                    keyConcepts = listOf("Siddha and Leela's friendship", "Class divides and societal suspicion", "Childhood innocence"),
                    quickSelfTest = emptyList()
                )
            ),
            downloadUrl = "https://wbchse.wb.gov.in/books-for-language-subject/"
        ),
        Textbook(
            id = "arabic_wbbme_10",
            title = "Al-Balagh (Madrasah Studies)",
            banglaTitle = "আল-বালাগ (আরবি ও মাদ্রাসা শিক্ষা)",
            board = "WBBME",
            className = "Class 10",
            subject = "Islamic Studies & Arabic",
            chapters = listOf(
                Chapter(
                    id = "ab_ch1",
                    title = "Arabic Grammar & Composition",
                    banglaTitle = "আরবি ব্যাকরণ ও রচনা",
                    keyConcepts = listOf("Arabic verbs and patterns", "Sina'at (literary devices)", "Socratic translations"),
                    quickSelfTest = emptyList()
                )
            ),
            downloadUrl = "https://wbbme.org/text-books/"
        ),
        // Central Repository Shortcuts for Poor Students' references (Direct official government links)
        Textbook(
            id = "central_banglar_shiksha",
            title = "Central e-Textbook Portal (Class 1-12)",
            banglaTitle = "বাংলার শিক্ষা ই-বুক পোর্টাল",
            board = "All Boards",
            className = "Class 1-12",
            subject = "All Subjects",
            chapters = listOf(
                Chapter(
                    id = "bs_portal",
                    title = "Direct Official Download Link",
                    banglaTitle = "সরাসরি ডাউনলোড পোর্টাল",
                    keyConcepts = listOf("Contains official PDFs for Bengali, English, Hindi, Urdu mediums", "Free of cost under School Education Department", "Instant mobile access")
                )
            ),
            downloadUrl = "https://banglarshiksha.wb.gov.in/Frontend/e_textbook"
        ),
        Textbook(
            id = "wbchse_languages",
            title = "WBCHSE Language Subjects approved books",
            banglaTitle = "উচ্চমাধ্যমিক ভাষা সাহিত্যের অনুমোদিত বই",
            board = "WBCHSE",
            className = "Class 11-12",
            subject = "Languages",
            chapters = listOf(
                Chapter(
                    id = "wbchse_lang",
                    title = "Official Language Book Repository",
                    banglaTitle = "ভাষা সাহিত্যের অফিশিয়াল বইয়ের তালিকা",
                    keyConcepts = listOf("Sanskrit, Hindi, Santhali, Bengali secondary texts", "Maintained by WBCHSE Council")
                )
            ),
            downloadUrl = "https://wbchse.wb.gov.in/books-for-language-subject/"
        ),
        Textbook(
            id = "wbchse_approved_all",
            title = "WBCHSE List of Approved Textbooks",
            banglaTitle = "উচ্চমাধ্যমিকের সমস্ত অনুমোদিত পাঠ্যপুস্তক",
            board = "WBCHSE",
            className = "Class 11-12",
            subject = "All Subjects",
            chapters = listOf(
                Chapter(
                    id = "wbchse_app",
                    title = "Syllabus and Recommended Books List",
                    banglaTitle = "পাঠ্যক্রম ও অনুমোদিত বইয়ের তালিকা",
                    keyConcepts = listOf("Science, Arts, Commerce textbooks", "Official publishers and curriculum codes")
                )
            ),
            downloadUrl = "https://wbchse.wb.gov.in/approved-books/"
        ),
        Textbook(
            id = "wbbme_madrasah_portal",
            title = "WBBME Madrasah Education Textbooks",
            banglaTitle = "পশ্চিমবঙ্গ মাদ্রাসা শিক্ষা পরিষদ পাঠ্যপুস্তক",
            board = "WBBME",
            className = "Class 1-10",
            subject = "All Madrasah Subjects",
            chapters = listOf(
                Chapter(
                    id = "wbbme_ch",
                    title = "Official Madrasah Book Downloads",
                    banglaTitle = "মাদ্রাসা বইয়ের অফিশিয়াল পোর্টাল",
                    keyConcepts = listOf("Arabic, High Madrasah, Theology textbooks", "Direct PDFs maintained by WBBME Board")
                )
            ),
            downloadUrl = "https://wbbme.org/text-books/"
        ),
        Textbook(
            id = "wbxpress_backup",
            title = "WBXpress Consolidated Textbook Hub",
            banglaTitle = "WBXpress একত্রিত ই-বুক ম্যাট্রিক্স",
            board = "All Boards",
            className = "Class 1-12",
            subject = "All Subjects",
            chapters = listOf(
                Chapter(
                    id = "wbx_ch",
                    title = "Single-Page Direct Download Matrix",
                    banglaTitle = "একত্রিত পিডিএফ ডাউনলোড গ্রিড",
                    keyConcepts = listOf("Direct download mirrors mapped back to government servers", "Contains vocational (WBSCVE&T) textbooks", "Highly reliable fallback library")
                )
            ),
            downloadUrl = "https://wbxpress.com/e-text-books-wbbse-wbchse-wbscvet-2018/"
        )
    )

    val pyqs = listOf(
        PYQ(
            year = 2025,
            subject = "History (Madhyamik)",
            questions = listOf(
                "Discuss the role of visual art in the Indigo Revolt (নীল বিদ্রোহ) of Bengal." to 
                "Think about what newspaper cartoons, the play 'Nil Darpan' (নীলদর্পণ) by Dinabandhu Mitra, and native illustrations conveyed. How did they spark a public conscience?",
                
                "Analyze the development of science education in 19th century Bengal under the leadership of Mahendralal Sircar." to 
                "Consider the founding of the Indian Association for the Cultivation of Science (IACS) in 1876. What was the purpose of having a native scientific body instead of colonial ones?"
            )
        ),
        PYQ(
            year = 2024,
            subject = "Physical Science (Madhyamik)",
            questions = listOf(
                "Why do we see a reddish sky during sunrise and sunset? Explain using scattering." to 
                "Recall Rayleigh's Scattering Law: intensity is inversely proportional to wavelength to the fourth power. Red light has a longer wavelength. How far can it travel through a thick atmosphere compared to blue light?",
                
                "A certain volume of gas occupies 300 ml at 27°C. What will be its volume at 127°C at constant pressure?" to 
                "We must convert temperatures from Celsius to Kelvin (T = t + 273). Write down T1 and T2, and apply Charles's Law (V1 / T1 = V2 / T2). What values do you get?"
            )
        )
    )

    val diyExperiments = listOf(
        DIYExperiment(
            title = "Domestic Turmeric pH Indicator",
            objective = "Determine if household liquids are basic or acidic using zero-cost kitchen ingredients.",
            householdMaterials = listOf("Turmeric powder (হলুদ)", "Water", "Soap water", "Lemon juice (লেবুর রস)", "White paper sheet", "Cotton swab"),
            steps = listOf(
                "Mix one teaspoon of turmeric powder with a little water to make a thick paste.",
                "Spread the turmeric paste evenly on a piece of paper and let it dry completely (now you have turmeric indicator paper!).",
                "Dip a cotton swab in soap water and write or paint on the dried turmeric paper. Note the color change.",
                "Now, dip another swab in fresh lemon juice and apply it over the same area. Watch what happens."
            ),
            scientificPrinciple = "Turmeric contains Curcumin, which acts as a natural indicator. It remains bright yellow in acidic/neutral media (like lemon juice) but changes to a deep red-brown in basic solutions (like soap water). Applying acid over base neutralizes it, turning the paper yellow again!",
            socraticQuestion = "When you painted with soap water, it turned blood-red. Why do you think it reverted to yellow when lemon juice was added on top? What dynamic chemical reaction is happening there?"
        ),
        DIYExperiment(
            title = "Zero-Cost Lemon Battery",
            objective = "Construct a simple battery to understand electrical potential differences using safe objects.",
            householdMaterials = listOf("1 Juicy Lemon (লেবু)", "1 Copper Coin or thick copper wire (anode)", "1 Zinc-coated Galvanized iron nail (cathode)", "A small LED bulb or low-voltage multi-meter"),
            steps = listOf(
                "Roll the lemon on a table while pressing gently to squeeze the juice pockets inside.",
                "Make two parallel slits in the lemon skin about 2 cm apart.",
                "Insert the copper coin halfway into one slit, and the galvanized nail into the other. Make sure they do not touch each other inside.",
                "Connect the leads of your LED bulb (or voltmeter) to the coin and nail respectively."
            ),
            scientificPrinciple = "The citric acid in the lemon juice acts as an electrolyte. Zinc is more reactive than copper, so it loses electrons (oxidation) which flow through the wire to copper (reduction), producing a small electric current of about 0.9V.",
            socraticQuestion = "What would happen to the voltage if you chained three lemons in a series (coin-to-nail, coin-to-nail)? Let's reason out why series connection multiplies electrical potential!"
        )
    )

    val geogChallenges = listOf(
        GeogChallenge(
            region = "Sundarbans Delta (সুন্দরবন)",
            title = "Mangrove Adaptation & Tidal Dynamics",
            description = "Explore the hyper-localized saline and mud environment of the world's largest delta.",
            coordinates = "21.9497° N, 89.1833° E",
            socraticQuest = "Sundarbans trees cannot get oxygen from the waterlogged, salt-heavy soil. They grow special roots upwards! What are these roots called, and how does physical gas exchange happen through their pores?",
            activeExercise = "If high tide rises the coastal water by 3 meters and your mangrove pneumatophore root is 50 cm tall, what percentage of the root is fully submerged during peak hours? Let's formulate a safety threshold for the forest's respiratory survival!"
        ),
        GeogChallenge(
            region = "Darjeeling Himalayan Foothills (দার্জিলিং)",
            title = "Altitude, Pressure, & Toy Train Curve Mechanics",
            description = "Explore high-altitude geography and the physics of the legendary Himalayan Toy Train.",
            coordinates = "27.0410° N, 88.2627° E",
            socraticQuest = "As you climb from Siliguri plains to Darjeeling (altitude 2,042m), air pressure drops. Why does water boil at a lower temperature (93°C) here? How does this affect local tea brewing?",
            activeExercise = "The toy train passes through 'Batasia Loop'—a tight spiral curve designed to lower the steep gradient. If the train has a mass of 40 tonnes and goes around a radius of 30 meters, let's derive how centripetal force and mechanical friction prevent derailment!"
        ),
        GeogChallenge(
            region = "Hooghly River Basin (ভাগীরথী-হুগলী)",
            title = "River Distribution & Industrial Silt Vectors",
            description = "Analyze the distribution patterns of Bengal's industrial lifeline.",
            coordinates = "22.5726° N, 88.3639° E",
            socraticQuest = "Why does the Ganges branch into the Padma (flowing into Bangladesh) and the Bhagirathi-Hooghly (flowing through Kolkata)? What role does the Farakka Barrage play in managing the silt of our ports?",
            activeExercise = "Draft a Socratic action plan to clean a local river bank. How can we use natural filter beds (sand, charcoal, gravel) to filter toxic industrial runoff on a low budget?"
        )
    )
}
