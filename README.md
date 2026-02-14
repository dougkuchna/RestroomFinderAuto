# Restroom Finder for Android Auto

An Android app that helps you find the closest public restroom, with full Android Auto support for hands-free use while driving.

## Features

- **Location-based Search**: Finds restrooms near your current location
- **Android Auto Support**: Full integration with Android Auto for safe, hands-free use
- **Accessibility Info**: Shows wheelchair accessibility, unisex options, and changing tables
- **Navigation Integration**: One-tap navigation to any restroom via Google Maps
- **Offline-friendly**: Graceful handling of network issues

## Tech Stack

- **Language**: Kotlin
- **Min SDK**: 29 (Android 10)
- **Target SDK**: 34 (Android 14)
- **Architecture**: Repository pattern with coroutines
- **Networking**: Retrofit + OkHttp
- **Location**: Google Play Services Location
- **Android Auto**: Car App Library 1.4.0

## API

This app uses the [Refuge Restrooms API](https://www.refugerestrooms.org/api/docs/) - a free, open API for finding public restrooms.

## Building

1. Open the project in Android Studio
2. Sync Gradle files
3. Build and run on a device or emulator

## Testing Android Auto

1. Install the [Desktop Head Unit (DHU)](https://developer.android.com/training/cars/testing)
2. Connect your device via ADB
3. Run the DHU and test the app

## Project Structure

```
app/src/main/java/com/restroomfinder/auto/
├── MainActivity.kt              # Phone UI
├── RestroomFinderApplication.kt # Application class
├── auto/                        # Android Auto components
│   ├── RestroomCarAppService.kt
│   ├── RestroomListScreen.kt
│   └── RestroomDetailScreen.kt
├── data/
│   ├── model/Restroom.kt
│   ├── remote/RestroomApi.kt
│   ├── remote/RetrofitClient.kt
│   └── repository/RestroomRepository.kt
└── location/LocationProvider.kt
```

## License

MIT License
