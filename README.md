# IoT BLE Android App

This is an IoT Android application built with Kotlin, Jetpack Compose, and MVVM architecture.
It is designed to connect and communicate with Arduino Nano 33 BLE over Bluetooth Low Energy (BLE).

📱 Features

🔗 Connects to Arduino Nano 33 BLE device.

📡 Scans and lists available BLE devices.

📥 Reads and writes data via BLE characteristics.

🎨 Built entirely with Jetpack Compose UI.

⚡ Uses MVVM architecture with Kotlin Coroutines for async operations.

🛠️ Modular and scalable project structure.

🛠️ Tech Stack

Kotlin 1.9.0

Jetpack Compose 3.5.0

MVVM + Coroutines + Flow

Bluetooth Low Energy (BLE) APIs

AndroidX + Material3

⚙️ Architecture

The app follows MVVM (Model-View-ViewModel) with a clean separation of concerns:

UI Layer → Jetpack Compose screens.

ViewModel Layer → Manages UI state & BLE interactions.

Repository Layer → Handles Bluetooth scanning, connecting, reading & writing.

🔧 Requirements

Android Studio Giraffe or newer

Android API 26+

A device with Bluetooth Low Energy (BLE) support

Arduino Nano 33 BLE with a BLE-enabled sketch uploaded

🚀 Getting Started

Clone this repo:

git clone https://github.com/your-username/IoT-BLE-App.git


Open in Android Studio.

Sync Gradle & run on a real device (emulators do not support BLE).

Turn on Bluetooth & connect to your Arduino Nano 33 BLE.

📷 Screenshots (optional)

(Add screenshots or GIFs of scanning and connecting screens if possible)

🤝 Contributing

Contributions are welcome! Feel free to fork this repo, raise issues, or open PRs.

📜 License

This project is released under the MIT License
.
