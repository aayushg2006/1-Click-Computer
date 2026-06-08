import 'dart:io';

class AppConstants {
  // Use 10.0.2.2 for Android Emulator to access host localhost.
  // Use localhost for iOS simulator or web.
  static String get baseUrl {
    if (Platform.isAndroid) {
      return 'http://10.0.2.2:8080/api/v1';
    }
    return 'http://localhost:8080/api/v1';
  }
}
