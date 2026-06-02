class ApiService {
  const ApiService._();

  // Populated via `flutter run --dart-define-from-file=.env`.
  static const String baseUrl = String.fromEnvironment(
    'API_BASE_URL',
    defaultValue: 'http://localhost:8080',
  );
}
