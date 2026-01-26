# Courier App

A Flutter mobile application for couriers in the multi-merchant delivery marketplace.

## Features

- **Authentication & Registration**: Secure login and courier registration with biometric support
- **Profile Management**: Manage personal information, vehicle details, and availability
- **Delivery Management**: Accept, track, and complete deliveries with DCC validation
- **Real-time Location Tracking**: GPS tracking and location services
- **Push Notifications**: Real-time updates for delivery assignments and status changes

## Architecture

The app follows Clean Architecture principles with:

- **Presentation Layer**: BLoC pattern for state management
- **Domain Layer**: Business logic and entities
- **Data Layer**: API clients, repositories, and data sources

### Project Structure

```
lib/
├── core/                          # Core functionality
│   ├── constants/                 # App constants
│   ├── di/                        # Dependency injection
│   ├── network/                   # API client
│   ├── storage/                   # Secure storage
│   ├── theme/                     # App theme
│   └── utils/                     # Utilities
├── features/                      # Feature modules
│   ├── auth/                      # Authentication
│   ├── deliveries/                # Delivery management
│   ├── home/                      # Dashboard
│   ├── location/                  # Location services
│   ├── notifications/             # Push notifications
│   └── profile/                   # Profile management
└── shared/                        # Shared components
    ├── models/                    # Shared models
    ├── services/                  # Shared services
    └── widgets/                   # Shared widgets
```

## Key Dependencies

- **flutter_bloc**: State management
- **get_it & injectable**: Dependency injection
- **dio & retrofit**: HTTP client
- **geolocator**: Location services
- **google_maps_flutter**: Maps integration
- **flutter_secure_storage**: Secure data storage
- **local_auth**: Biometric authentication
- **firebase_messaging**: Push notifications

## Getting Started

### Prerequisites

- Flutter SDK (3.7.2 or higher)
- Dart SDK
- Android Studio / Xcode for mobile development
- Firebase project for push notifications

### Installation

1. Clone the repository
2. Navigate to the courier_app directory
3. Install dependencies:
   ```bash
   flutter pub get
   ```

4. Generate code:
   ```bash
   flutter packages pub run build_runner build
   ```

5. Configure Firebase:
   - Add `google-services.json` (Android) and `GoogleService-Info.plist` (iOS)
   - Follow Firebase setup instructions

### Configuration

Update the API base URL in `lib/core/constants/app_constants.dart`:

```dart
static const String baseUrl = 'YOUR_API_BASE_URL';
```

### Running the App

```bash
flutter run
```

## Features Implementation

### Authentication
- Email/password login
- Courier registration with vehicle information
- Biometric authentication support
- Secure token storage

### Profile Management
- Personal information management
- Vehicle details and documentation
- Availability schedule configuration
- Earnings and statistics tracking

### Delivery Operations
- Real-time delivery assignments
- GPS navigation and route optimization
- Delivery status updates
- DCC (Delivery Confirmation Code) validation
- Proof of delivery capture

### Location Services
- Real-time GPS tracking
- Location permission management
- Background location updates
- Geofencing capabilities

## Security Features

- Secure storage for authentication tokens
- Biometric authentication
- API request authentication
- Location data encryption
- Audit logging for delivery operations

## Testing

Run tests with:

```bash
flutter test
```

## Build

### Android
```bash
flutter build apk --release
```

### iOS
```bash
flutter build ios --release
```

## Contributing

1. Follow the established architecture patterns
2. Write tests for new features
3. Update documentation
4. Follow Flutter/Dart style guidelines

## License

This project is part of the multi-merchant delivery marketplace platform.