# Hack News Android App

A modern Android application for reading Hacker News, built with Kotlin and following modern Android development practices.

## 🚀 Features

- Browse Hacker News stories (Top, New, Show, Ask, Jobs)
- Read comments and engage in discussions
- Save favorite posts for offline reading
- Clean, intuitive user interface with Material Design principles
- Dark mode support
- Cyberpunk UI theme available (cyberpunk-hacker-ui branch)

## 🛠️ Tech Stack

- **Language**: Kotlin
- **Architecture**: MVVM (Model-View-ViewModel)
- **Build System**: Gradle with Kotlin DSL
- **UI**: Jetpack Compose or XML layouts
- **Networking**: OkHttp/Retrofit for API calls
- **Data Management**: Room database for offline caching
- **Dependency Injection**: Dagger/Hilt

## 📋 Prerequisites

- Android Studio (latest version recommended)
- Android SDK 21+ (targeting API 34)
- Kotlin 1.8+
- Gradle 8.0+

## 📦 Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/Clearzero22/Hack-news-android-app.git
   ```

2. Open the project in Android Studio

3. Sync the project with Gradle files

4. Build and run the application

## ⚙️ Configuration

If there are any API keys or configuration files required, make sure to set them up in your `local.properties` or a dedicated config file.

## 📁 Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/          # Kotlin source files
│   │   ├── res/           # Resources (layouts, drawables, etc.)
│   │   └── AndroidManifest.xml
│   ├── test/              # Unit tests
│   └── androidTest/       # Instrumentation tests
```

## 🌟 Branches

- `main` - Primary development branch
- `master` - Alternative branch
- `cyberpunk-hacker-ui` - Special UI theme branch with cyberpunk styling

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Make your changes
4. Commit your changes (`git commit -m 'Add some amazing feature'`)
5. Push to the branch (`git push origin feature/amazing-feature`)
6. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🎯 Roadmap

- [ ] Implement user authentication
- [ ] Add notification support
- [ ] Improve offline reading capabilities
- [ ] Add more theme options

## 💬 Support

If you have any questions, feel free to open an issue in the repository.

---

Happy Hacking! 🚀