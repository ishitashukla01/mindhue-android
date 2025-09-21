# 🌈 MindHue – AI-Powered Mood Journal

[![Android Studio](https://img.shields.io/badge/IDE-Android%20Studio-3DDC84?logo=android&logoColor=white)]()
[![Language](https://img.shields.io/badge/Language-Java-orange)]()
[![Min SDK](https://img.shields.io/badge/minSdk-21-blue)]()
[![License: MIT](https://img.shields.io/badge/License-MIT-lightgrey.svg)](LICENSE)

**MindHue** is an Android app to log your moods, jot quick notes, and discover patterns with **offline sentiment analysis**. It turns daily feelings into a colorful timeline with charts, streaks, and insights.

---

## ✨ Features

- 📝 **Daily mood entries** — mood + note + optional activity tag
- 🎨 **Weekly palette** — color blocks for each day at a glance
- 📊 **Stats screen** — mood distribution + **sentiment distribution** (MPAndroidChart)
- 🤖 **AI-style sentiment** — Positive / Neutral / Negative (fast, offline)
- 🔥 **Streaks & badges** — daily logging streak + positive streak
- 📅 **Calendar view** — jump to any day; see highlights
- 🧭 **Activity insights** — “top activity by mood” summary
- 🧩 (Optional) **Triggers** — common words in positive vs negative notes
- 🔐 **Local-only storage** — data kept on device (SharedPreferences)

---

## 🎥 Demo
<video src="screenshots/demo.webm" controls width="300"></video>

---

## 🛠 Tech Stack

- **Android:** Java + XML, Material 3 UI
- **Charts:** [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)
- **Calendar:** MaterialCalendarView
- **Storage:** SharedPreferences (custom `PrefStore`)
- **Min SDK:** 21

---

