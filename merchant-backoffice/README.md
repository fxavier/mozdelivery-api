# Merchant Backoffice - Angular 20 Application

This is the merchant backoffice application for the multi-merchant delivery marketplace, built with Angular 20, TailwindCSS, and NgRx.

## Features

- **Angular 20** with standalone components and zoneless change detection
- **TailwindCSS** for styling with custom component classes
- **NgRx** for state management with proper separation of concerns
- **Authentication** with JWT tokens and role-based access control
- **Merchant Authentication** with proper guards and interceptors
- **Responsive Design** with mobile-first approach
- **Server-Side Rendering (SSR)** support

## Architecture

The application follows Angular best practices with:

- **Hexagonal Architecture** principles
- **Feature-based module organization**
- **Separate files** for components (`.ts`, `.html`, `.css`)
- **NgRx state management** with actions, reducers, effects, and selectors
- **HTTP interceptors** for authentication and tenant context
- **Route guards** for role-based access control

## Project Structure

```
src/app/
├── core/                          # Core services and guards
│   ├── auth/                      # Authentication services
│   ├── guards/                    # Route guards
│   ├── interceptors/              # HTTP interceptors
│   └── store/                     # Global state management
├── shared/                        # Shared components and utilities
│   ├── components/                # Reusable UI components
│   ├── models/                    # TypeScript interfaces and types
├── features/                      # Feature modules
│   ├── auth/                      # Authentication components
│   └── dashboard/                 # Dashboard components
└── layout/                        # Application layout components
```

## Getting Started

### Prerequisites

- Node.js 18+ 
- npm 9+
- Angular CLI 20+

### Installation

1. Install dependencies:
```bash
npm install
```

2. Start the development server:
```bash
npm start
```

3. Build for production:
```bash
npm run build
```

### Available Scripts

- `npm start` - Start development server
- `npm run build` - Build for production
- `npm run watch` - Build in watch mode
- `npm test` - Run unit tests
- `npm run serve:ssr:merchant-backoffice` - Serve SSR build

## Authentication

The application supports:

- **JWT-based authentication** with refresh tokens
- **Role-based access control** (Admin, Merchant, Courier, Client)
- **Multi-tenant support** with merchant isolation
- **OAuth2/OIDC integration** ready

## State Management

Uses NgRx with:

- **Actions** for user interactions and API calls
- **Reducers** for state transitions
- **Effects** for side effects and API calls
- **Selectors** for derived state

## Styling

Built with TailwindCSS featuring:

- **Custom component classes** (`.btn-primary`, `.card`, `.form-input`)
- **Responsive design** utilities
- **Custom color palette** for branding
- **Utility-first approach** with component abstractions

## Development

### Adding New Features

1. Create feature module in `src/app/features/`
2. Add components with separate `.ts`, `.html`, `.css` files
3. Create NgRx store files (actions, reducer, effects, selectors)
4. Add routes and guards as needed
5. Update shared models and services

### Code Style

- Use **standalone components**
- Separate concerns with distinct files
- Follow **Angular style guide**
- Use **TypeScript strict mode**
- Implement **proper error handling**

## Deployment

The application is built for production with:

- **Server-Side Rendering** for better SEO and performance
- **Code splitting** for optimal loading
- **Tree shaking** for smaller bundle sizes
- **Production optimizations** enabled

## API Integration

Configured for integration with:

- **Backend API** at `http://localhost:8080/api` (development)
- **Authentication endpoints** for login/logout/refresh
- **Merchant-specific APIs** with tenant context
- **Role-based endpoint access**

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Contributing

1. Follow the established architecture patterns
2. Write tests for new features
3. Use proper TypeScript types
4. Follow the component separation pattern
5. Update documentation as needed