package com.example.apragamstudyjam

/**
 * Repository providing quiz questions categorized into Android, Programming, General Knowledge, and Science.
 */
object QuestionBank {

    private val questionsList = listOf(
        // --- Android Category ---
        Question(
            text = "Which programming language is Google's preferred language for Android app development?",
            options = listOf("Java", "Kotlin", "Python", "C++"),
            correctIndex = 1,
            category = "Android"
        ),
        Question(
            text = "What does APK stand for in the Android ecosystem?",
            options = listOf("Android Package Kit", "Android Project Kit", "Android Application Key", "Android Process Kernel"),
            correctIndex = 0,
            category = "Android"
        ),
        Question(
            text = "Which Android component handles background operations without a user interface?",
            options = listOf("Activity", "Service", "BroadcastReceiver", "ContentProvider"),
            correctIndex = 1,
            category = "Android"
        ),
        Question(
            text = "What is the official IDE recommended by Google for Android development?",
            options = listOf("Eclipse", "VS Code", "Android Studio", "IntelliJ IDEA"),
            correctIndex = 2,
            category = "Android"
        ),

        // --- Programming Category ---
        Question(
            text = "Which data structure operates on a First In, First Out (FIFO) principle?",
            options = listOf("Stack", "Queue", "Tree", "Graph"),
            correctIndex = 1,
            category = "Programming"
        ),
        Question(
            text = "In Kotlin, which keyword is used to declare a read-only (immutable) variable?",
            options = listOf("var", "const", "val", "let"),
            correctIndex = 2,
            category = "Programming"
        ),
        Question(
            text = "In object-oriented programming, what is the term for a class inheriting properties from another class?",
            options = listOf("Polymorphism", "Encapsulation", "Inheritance", "Abstraction"),
            correctIndex = 2,
            category = "Programming"
        ),
        Question(
            text = "Which sorting algorithm has an average time complexity of O(n log n)?",
            options = listOf("Bubble Sort", "Merge Sort", "Selection Sort", "Insertion Sort"),
            correctIndex = 1,
            category = "Programming"
        ),

        // --- General Knowledge Category ---
        Question(
            text = "Who is widely recognized as the father of modern computer science?",
            options = listOf("Steve Jobs", "Alan Turing", "Bill Gates", "Charles Babbage"),
            correctIndex = 1,
            category = "General Knowledge"
        ),
        Question(
            text = "Which company originally created Android before it was acquired by Google in 2005?",
            options = listOf("Nokia", "Android Inc.", "Sun Microsystems", "Motorola"),
            correctIndex = 1,
            category = "General Knowledge"
        ),
        Question(
            text = "What is the capital city of Japan?",
            options = listOf("Beijing", "Seoul", "Tokyo", "Bangkok"),
            correctIndex = 2,
            category = "General Knowledge"
        ),
        Question(
            text = "What does HTTP stand for in networking?",
            options = listOf("HyperText Transfer Protocol", "High Transfer Text Protocol", "HyperText Test Protocol", "Hyperlink Text Transport System"),
            correctIndex = 0,
            category = "General Knowledge"
        ),

        // --- Science Category ---
        Question(
            text = "What is the chemical symbol for Gold?",
            options = listOf("Ag", "Au", "Fe", "Hg"),
            correctIndex = 1,
            category = "Science"
        ),
        Question(
            text = "Which planet in our solar system is known as the Red Planet?",
            options = listOf("Venus", "Mars", "Jupiter", "Saturn"),
            correctIndex = 1,
            category = "Science"
        ),
        Question(
            text = "What is the speed of light in a vacuum approximately?",
            options = listOf("150,000 km/s", "300,000 km/s", "450,000 km/s", "600,000 km/s"),
            correctIndex = 1,
            category = "Science"
        ),
        Question(
            text = "Which organ in the human body produces insulin?",
            options = listOf("Liver", "Kidney", "Pancreas", "Heart"),
            correctIndex = 2,
            category = "Science"
        )
    )

    /**
     * Returns a shuffled list of all available quiz questions.
     */
    fun getShuffledQuestions(): List<Question> {
        return questionsList.shuffled()
    }

    /**
     * Returns questions filtered by category, or all questions if category is "All".
     */
    fun getQuestionsByCategory(category: String): List<Question> {
        return if (category == "All" || category.isBlank()) {
            getShuffledQuestions()
        } else {
            questionsList.filter { it.category.equals(category, ignoreCase = true) }.shuffled()
        }
    }
}