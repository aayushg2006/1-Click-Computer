import 'dart:convert';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:http/http.dart' as http;
import 'api_service.dart';

const String _tokenKey = 'jwt_token';

class AuthState {
  final bool isLoading;
  final bool isInitializing;
  final bool isAuthenticated;
  final String? token;

  const AuthState({
    this.isLoading = false,
    this.isInitializing = false,
    this.isAuthenticated = false,
    this.token,
  });

  AuthState copyWith({
    bool? isLoading,
    bool? isInitializing,
    bool? isAuthenticated,
    String? token,
  }) {
    return AuthState(
      isLoading: isLoading ?? this.isLoading,
      isInitializing: isInitializing ?? this.isInitializing,
      isAuthenticated: isAuthenticated ?? this.isAuthenticated,
      token: token ?? this.token,
    );
  }
}

class AuthNotifier extends Notifier<AuthState> {
  @override
  AuthState build() {
    _init();
    return const AuthState(isInitializing: true);
  }

  Future<void> _init() async {
    final prefs = await SharedPreferences.getInstance();
    final token = prefs.getString(_tokenKey);
    
    if (token != null && token.isNotEmpty) {
      state = state.copyWith(isInitializing: false, isAuthenticated: true, token: token);
    } else {
      state = state.copyWith(isInitializing: false, isAuthenticated: false, token: null);
    }
  }

  Future<void> login(String username, String password) async {
    state = state.copyWith(isLoading: true);
    try {
      final response = await http.post(
        Uri.parse('${ApiService.baseUrl}/auth/login'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'username': username,
          'password': password,
        }),
      );

      if (response.statusCode >= 200 && response.statusCode < 300) {
        final data = jsonDecode(response.body);
        final token = data['token'];
        
        if (token != null) {
          final prefs = await SharedPreferences.getInstance();
          await prefs.setString(_tokenKey, token);
          state = state.copyWith(isLoading: false, isAuthenticated: true, token: token);
          return;
        }
      }
      
      throw Exception('Login failed. Please check your credentials.');
    } catch (e) {
      state = state.copyWith(isLoading: false);
      rethrow;
    }
  }

  Future<void> logout() async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove(_tokenKey);
    state = state.copyWith(isAuthenticated: false, token: null);
  }
}

final authProvider = NotifierProvider<AuthNotifier, AuthState>(() {
  return AuthNotifier();
});
