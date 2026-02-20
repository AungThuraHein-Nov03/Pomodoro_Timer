# CSX4109 Term Project — Pomodoro Timer

A productivity-focused Pomodoro Timer Android application built with Kotlin. Features customizable work/break presets, a dedicated break screen, and a full Statistics dashboard with charts and task history.

## Features

- **Preset-Based Timer**
  - Work presets: 15m, 25m, 45m, 90m
  - Break presets: 5m, 15m
  - Automatic long break (15 min) after every 4 work sessions

- **Progress Tracking**
  - Linear progress bar with real-time countdown (MM:SS)
  - Daily session counter
  - Status text showing current work/break configuration

- **Task Logging**
  - "What are you working on?" input field
  - Task name auto-saved via SharedPreferences
  - Each completed session is recorded to a local SQLite database

- **Dedicated Break Screen**
  - Full-screen break timer with countdown
  - Automatic return to work mode when break ends
  - Manual "Back to Work" button

- **Statistics Dashboard**
  - Total focus time and session count
  - Best streak and current streak (consecutive days)
  - Average daily focus time (last 7 days)
  - Bar chart showing daily productivity over the last 7 days
  - Completed tasks table (most recent 8) with date, time, and task name

- **Motivational Quotes**
  - 25 curated productivity quotes displayed during work sessions and breaks
  - No-repeat logic ensures you don't see the same quote twice in a row
  - Quotes replace the status text while the timer is running

- **Safety & Polish**
  - Confirmation dialog before resetting an active timer (prevents accidental loss)
  - Preset buttons are disabled while the timer is running (prevents accidental changes)
  - Portrait orientation locked on all screens
  - Forced light theme to prevent dark-mode rendering issues

- **Notifications**
  - Sound notification when a session completes
  - Toast messages for session transitions

## Technical Details

| Detail           | Value                                  |
|------------------|----------------------------------------|
| Language         | Kotlin                                 |
| Min SDK          | 26 (Android 8.0 Oreo)                  |
| Target SDK       | 36                                     |
| Application ID   | `com.aungthurahein.myapplicationpmt`   |
| Build System     | Gradle with Kotlin DSL                 |
| View System      | ViewBinding                            |
| Database         | SQLite (via SQLiteOpenHelper)          |
| Chart            | Custom BarChartView (Canvas-drawn)     |
| Theme            | Material3 DayNight NoActionBar         |

## Prerequisites

1. **Android Studio** (latest version recommended)
2. **JDK 11** or higher (included with Android Studio)
3. **Android SDK Platform API 36** and Build-Tools

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/AungThuraHein-Nov03/Pomodoro_Timer
cd MyApplicationPMT
```

### Open in Android Studio

1. Launch Android Studio
2. **File** → **Open** → select the `MyApplicationPMT` folder
3. Wait for Gradle sync to complete

### Run the App

**Option A — Android Studio (recommended):**
1. Select a device/emulator from the toolbar dropdown
2. Click the **Run** button or press `Shift + F10`

**Option B — Command line:**
```bash
# Windows
.\gradlew.bat installDebug

# macOS / Linux
chmod +x gradlew
./gradlew installDebug
```

### Set Up a Physical Device

1. **Settings** → **About Phone** → tap **Build Number** 7 times to enable Developer Options
2. **Settings** → **Developer Options** → enable **USB Debugging**
3. Connect via USB and accept the debugging prompt

## How to Use the App

### Screen 1: Main Timer (MainActivity)

This is the home screen you see when you open the app.

**Layout (top to bottom):**
- **"POMODORO TIMER"** title at the top
- **Session counter** pill showing how many sessions you've done today (e.g. "2 sessions today")
- **Timer display** — large MM:SS countdown
- **Progress bar** — thin horizontal bar showing elapsed progress
- **Status / Quote area** — shows current configuration (e.g. "Work: 25m • Break: 5m") when idle, or a random motivational quote while the timer is running
- **Task input** — card with "What are you working on?" placeholder; type your current task here
- **Preset buttons** — two rows:
  - **STUDY TIME**: 15m, 25m, 45m, 90m (tap to change work duration)
  - **BREAK TIME**: 5m, 15m (tap to change break duration)
- **Control buttons** — bottom row with three buttons:
  - **Reset** (left) — resets timer and session count (shows a confirmation dialog if a session is active)
  - **Play/Pause** (center, large) — starts or pauses the countdown
  - **Statistics** (right) — opens the Statistics screen

**How a session works:**
1. Type what you're working on in the task input (Optional but highly recommended)
2. Tap a study time preset if you want something other than 25 minutes
3. Tap a break time preset if you want something other than 5 minutes
4. Press **Play** to start the countdown
5. A motivational quote appears in place of the status text while running
   - Preset buttons become disabled to prevent accidental changes
6. When the timer hits 00:00:
   - A sound plays
   - The session is saved to the database (with your task name, date, and duration)
   - The daily session counter increments
   - The app automatically opens the Break Screen
7. After every 4 completed work sessions, you get a long break (15 minutes) instead of the short break

**Controls during a session:**
- **Pause**: pauses the countdown; press Play to resume from where you left off
- **Reset**: shows a confirmation dialog if the timer is active; cancels the current session entirely, resets counter, and returns to "Ready for work!" state

### Screen 2: Break Screen (BreakActivity)

This screen appears automatically after a work session finishes.

**Layout:**
- Green background with "Well done! Take a well earned break." message
- A motivational quote displayed below the break message
- Break countdown timer (MM:SS format) showing the remaining break time
- **"Back to Work"** button at the bottom

**How it works:**
- The timer counts down automatically
- When it reaches 00:00, the screen closes and you're returned to the Main Timer in work mode
- You can press **"Back to Work"** at any time to skip the rest of the break and go back early
- The Main Timer automatically resets to work mode when you return

### Screen 3: Statistics (StatisticsActivity)

Access this screen by tapping the **chart icon** (bottom-right) on the Main Timer screen.

**Layout (top to bottom):**
- **Back arrow** (top-left) to return to the Main Timer
- **Summary cards** — three cards in a row:
  - **Total Focus**: total hours and minutes spent across all sessions
  - **Sessions**: total number of completed work sessions
  - **Best Streak**: longest run of consecutive days with at least one session
- **Two smaller cards** below:
  - **Avg Daily**: average focus time per active day (last 7 days)
  - **Current Streak**: how many consecutive days (up to today) you've completed at least one session
- **"LAST 7 DAYS" bar chart**: custom-drawn bar chart showing minutes of focus per day for the last week
- **"COMPLETED TASKS" table**: shows the 8 most recent completed sessions in a scrollable table with columns:
  - **Date** — formatted as "MMM dd" (e.g. "Feb 16")
  - **Time** — formatted as "hh:mm a" (e.g. "02:30 PM")
  - **Task** — the task name you entered (or "Focus Session" if left blank)

**Navigation:** Press the back arrow or the device back button to return to the Main Timer.

## Data Storage

All session data is stored **locally** on the device using SQLite.

- **Database file**: `pomodoro_sessions.db`
- **Location**: private app storage (`/data/data/com.aungthurahein.myapplicationpmt/databases/`)
- **No server or internet connection required**
- Data persists across app restarts but is deleted if the app is uninstalled
- Timer settings (task name, daily session count) are stored in SharedPreferences (`pomodoro_prefs`)

## Project Structure

```
MyApplicationPMT/
├── app/
│   ├── src/main/
│   │   ├── java/com/aungthurahein/myapplicationpmt/
│   │   │   ├── MainActivity.kt            # Main timer screen
│   │   │   ├── BreakActivity.kt           # Break countdown screen
│   │   │   ├── StatisticsActivity.kt      # Statistics dashboard
│   │   │   ├── SessionDatabaseHelper.kt   # SQLite database helper
│   │   │   ├── SessionRecord.kt           # Data class for session entries
│   │   │   ├── BarChartView.kt            # Custom bar chart (Canvas)
│   │   │   ├── MotivationalQuotes.kt      # 25 curated quotes with no-repeat logic
│   │   │   └── Constants.kt              # App-wide constants & defaults
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   │   ├── activity_main.xml      # Main timer layout
│   │   │   │   ├── activity_break.xml     # Break screen layout
│   │   │   │   └── activity_statistics.xml # Statistics layout
│   │   │   ├── drawable/                  # Buttons, icons, shapes
│   │   │   ├── anim/                      # Fade animations
│   │   │   ├── values/                    # Colors, strings, themes
│   │   │   └── mipmap-*/                 # App launcher icons
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## Dependencies

- **AndroidX Core KTX** — Kotlin extensions for Android
- **AndroidX AppCompat** — backward-compatible Activity/Fragment support
- **Material Components** — Material3 UI widgets and theming
- **AndroidX Activity** — Activity lifecycle support
- **ConstraintLayout** — flexible layout system
- **JUnit / Espresso** — testing frameworks

## Troubleshooting

| Problem                  | Solution                                                             |
|--------------------------|----------------------------------------------------------------------|
| Gradle sync fails        | **File** → **Invalidate Caches / Restart**, or delete `.gradle/`    |
| Device not detected      | Enable USB Debugging, try a different cable, reboot both devices     |
| Build errors             | **Build** → **Clean Project**, then **Rebuild Project**             |
| App crashes              | Check **Logcat** for error messages; verify device meets minSdk 26  |

## License

Created for educational purposes as part of CSX4109 Android App Development at Assumption University.

## Author

Developed by Aung Thura Hein
