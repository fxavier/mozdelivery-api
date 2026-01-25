# MozDelivery Client App

A Flutter mobile application for the multi-merchant delivery marketplace targeting the Mozambique market.

## Features

- **Authentication & Guest Mode**: Support for user registration, login, and guest checkout
- **BLoC Architecture**: Clean architecture with state management using flutter_bloc
- **Navigation**: Declarative routing with go_router
- **Secure Storage**: Encrypted storage for authentication tokens and sensitive data
- **API Integration**: RESTful API client with error handling and retry mechanisms
- **Multi-language Support**: Ready for internationalization
- **Material Design 3**: Modern UI following Material Design guidelines

## Architecture

The app follows Clean Architecture principles with the following structure:

```
lib/
├── core/                     # Core functionality
│   ├── constants/           # App constants
│   ├── di/                  # Dependency injection
│   ├── errors/              # Error handling
│   ├── network/             # API client and networking
│   ├── storage/             # Secure storage services
│   ├── theme/               # App theming
│   └── utils/               # Utilities and routing
├── features/                # Feature modules
│   ├── auth/                # Authentication
│   ├── home/                # Home screen
│   ├── merchants/           # Merchant browsing
│   ├── orders/              # Order management
│   └── profile/             # User profile
└── shared/                  # Shared components
    ├── models/              # Data models
    ├── services/            # Shared services
    └── widgets/             # Reusable widgets
```

## Getting Started

### Prerequisites

- Flutter SDK (3.7.2 or higher)
- Dart SDK
- Android Studio / VS Code
- Android SDK (for Android development)
- Xcode (for iOS development, macOS only)

### Installation

1. Clone the repository
2. Navigate to the client_app directory
3. Install dependencies:
   ```bash
   flutter pub get
   ```
4. Generate code:
   ```bash
   dart run build_runner build
   ```
5. Run the app:
   ```bash
   flutter run
   ```

## Configuration

### API Configuration

Update the base URL in `lib/core/constants/app_constants.dart`:

```dart
static const String baseUrl = 'http://your-api-url.com/api';
```

### Firebase Configuration (Optional)

If using Firebase for push notifications:

1. Add your `google-services.json` (Android) and `GoogleService-Info.plist` (iOS)
2. Configure Firebase in your project

## State Management

The app uses BLoC (Business Logic Component) pattern for state management:

- **Bloc**: Handles business logic and state transitions
- **Events**: User actions and external events
- **States**: UI states that the widgets react to

Example usage:
```dart
BlocBuilder<AuthBloc, AuthState>(
  builder: (context, state) {
    if (state is AuthLoading) {
      return CircularProgressIndicator();
    }
    // Handle other states...
  },
)
```

## Navigation

The app uses go_router for declarative navigation:

```dart
context.go('/merchants');
context.push('/orders/123');
```

## Authentication

The app supports multiple authentication modes:

- **Registered Users**: Full account with login/logout
- **Guest Mode**: Anonymous browsing and ordering
- **Account Conversion**: Convert guest accounts to registered accounts

## API Integration

The app communicates with the backend through a RESTful API:

- **Public Endpoints**: No authentication required (browsing, guest checkout)
- **Authenticated Endpoints**: Require JWT tokens
- **Error Handling**: Comprehensive error handling with user-friendly messages
- **Token Refresh**: Automatic token refresh on expiration

## Security

- **Secure Storage**: Sensitive data encrypted using flutter_secure_storage
- **Token Management**: Automatic token refresh and secure storage
- **API Security**: Request/response interceptors for authentication
- **Input Validation**: Client-side validation for all forms

## Testing

Run tests with:
```bash
flutter test
```

## Building

### Debug Build
```bash
flutter build apk --debug
flutter build ios --debug
```

### Release Build
```bash
flutter build apk --release
flutter build ios --release
```

## Contributing

1. Follow the established architecture patterns
2. Use BLoC for state management
3. Write tests for new features
4. Follow Dart/Flutter style guidelines
5. Update documentation as needed

## Dependencies

Key dependencies used in this project:

- **flutter_bloc**: State management
- **go_router**: Navigation
- **dio**: HTTP client
- **flutter_secure_storage**: Secure storage
- **get_it**: Dependency injection
- **injectable**: Code generation for DI
- **equatable**: Value equality
- **cached_network_image**: Image caching
- **geolocator**: Location services
- **permission_handler**: Permissions
- **firebase_messaging**: Push notifications

## License

This project is part of the MozDelivery platform.