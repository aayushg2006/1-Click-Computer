import 'dart:convert';
import 'package:http/http.dart' as http;

class ApiService {
  const ApiService._();

  static const String baseUrl = String.fromEnvironment(
    'API_BASE_URL',
    defaultValue: 'http://10.0.2.2:8080/api/v1',
  );

  static Future<dynamic> getEndpoint(String endpoint) async {
    final response = await http.get(Uri.parse('$baseUrl$endpoint'));
    if (response.statusCode >= 200 && response.statusCode < 300) {
      if (response.statusCode == 204 || response.body.isEmpty) return null;
      return jsonDecode(response.body);
    }
    throw Exception('Failed to GET data: ${response.statusCode}');
  }

  static Future<dynamic> postEndpoint(String endpoint, Map<String, dynamic> body) async {
    final response = await http.post(
      Uri.parse('$baseUrl$endpoint'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(body),
    );
    if (response.statusCode >= 200 && response.statusCode < 300) {
      if (response.statusCode == 204 || response.body.isEmpty) return null;
      return jsonDecode(response.body);
    }
    throw Exception('Failed to POST data: ${response.statusCode}');
  }
}
