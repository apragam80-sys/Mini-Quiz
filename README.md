# 🧠 Mini Quiz App (Kotlin XML)

An interactive, feature-packed Android Quiz application built with Kotlin, ViewBinding, and Material Design components.

---

## ✨ Features

- 🎨 **Dual Theme Support**:
  - **Neon Night (Dark Theme)**: Modern dark gradient background (`#0F0C29` → `#302B63` → `#24243E`) with glowing neon accents (`#00F5D4` cyan, `#F15BB5` pink, `#00E676` green, `#FF5252` red).
  - **Sunset (Light Theme)**: Vibrant gradient (`#FF512F` → `#DD2476`) with clean white cards and dark text.
  - Theme preference saved automatically in `SharedPreferences`.

- 🏷️ **Category Selection**:
  - Select questions by category: **Android**, **Programming**, **General Knowledge**, **Science**, or **All Topics**.

- 💡 **Quiz Lifelines**:
  - **50-50**: Hides 2 wrong options for the current question (usable once per quiz).
  - **Skip**: Skips current question without any penalty or streak reset (usable once per quiz).

- 🔥 **Streak Bonus System**:
  - Tracks consecutive correct answers and awards **+2 bonus points** on every 3 consecutive correct answers with an animated streak badge.

- 📋 **Detailed Review Screen**:
  - Review all questions, user responses, correct answers, and status indicators using a `RecyclerView`.

- ⚡ **Animations & Transitions**:
  - Animated splash screen with bounce and pulsing glow effects.
  - Smooth card entry transitions, countdown timer, and score popups.

---

## 🛠️ Built With

- **Language**: Kotlin
- **UI Framework**: Android XML ViewBinding, Material Components
- **Architecture**: Single-module Android Application
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: API 35

---

## 🚀 Getting Started

1. **Clone the repository**:
   ```bash
   git clone https://github.com/apragam80-sys/Mini-Quiz.git
   ```
2. **Open in Android Studio**:
   - Open Android Studio, select **Open** and choose the `Mini-Quiz` project folder.
3. **Build & Run**:
   - Sync Gradle and click **Run** (or press Shift+F10) to deploy to an emulator or physical device.

---

## 📜 License

Created for GDG JSS / Apragam Study Jam 2026.
