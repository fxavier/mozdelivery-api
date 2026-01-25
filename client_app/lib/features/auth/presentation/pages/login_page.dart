import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:go_router/go_router.dart';

import '../bloc/auth_bloc.dart';
import '../widgets/auth_form.dart';
import '../../../../core/utils/app_router.dart';
import '../../../../core/di/injection.dart';

class LoginPage extends StatelessWidget {
  const LoginPage({super.key});

  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (context) => getIt<AuthBloc>(),
      child: const LoginView(),
    );
  }
}

class LoginView extends StatelessWidget {
  const LoginView({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Login'),
        backgroundColor: Colors.transparent,
        elevation: 0,
      ),
      body: BlocListener<AuthBloc, AuthState>(
        listener: (context, state) {
          if (state is Authenticated) {
            context.go(AppRouter.home);
          } else if (state is GuestMode) {
            context.go(AppRouter.home);
          } else if (state is AuthError) {
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(
                content: Text(state.message),
                backgroundColor: Colors.red,
              ),
            );
          }
        },
        child: SafeArea(
          child: Padding(
            padding: const EdgeInsets.all(24.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                const Spacer(),
                // Logo or App Name
                const Text(
                  'MozDelivery',
                  style: TextStyle(
                    fontSize: 32,
                    fontWeight: FontWeight.bold,
                  ),
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: 8),
                const Text(
                  'Welcome back!',
                  style: TextStyle(
                    fontSize: 16,
                    color: Colors.grey,
                  ),
                  textAlign: TextAlign.center,
                ),
                const SizedBox(height: 48),
                
                // Login Form
                BlocBuilder<AuthBloc, AuthState>(
                  builder: (context, state) {
                    return AuthForm(
                      isLogin: true,
                      isLoading: state is AuthLoading,
                      onSubmit: (data) {
                        context.read<AuthBloc>().add(
                          LoginRequested(
                            email: data['email']!,
                            password: data['password']!,
                          ),
                        );
                      },
                    );
                  },
                ),
                
                const SizedBox(height: 24),
                
                // Guest Mode Button
                OutlinedButton(
                  onPressed: () {
                    context.read<AuthBloc>().add(StartGuestMode());
                  },
                  child: const Text('Continue as Guest'),
                ),
                
                const SizedBox(height: 16),
                
                // Register Link
                TextButton(
                  onPressed: () {
                    context.go(AppRouter.register);
                  },
                  child: const Text("Don't have an account? Register"),
                ),
                
                const Spacer(),
              ],
            ),
          ),
        ),
      ),
    );
  }
}