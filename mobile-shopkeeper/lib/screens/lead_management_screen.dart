import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../services/api_service.dart';

final leadsProvider = FutureProvider<List<dynamic>>((ref) async {
  return await ApiService.getEndpoint('/admin/reservations') as List;
});

class LeadManagementScreen extends ConsumerWidget {
  const LeadManagementScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final leadsAsync = ref.watch(leadsProvider);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Lead Management Queue'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: () => ref.refresh(leadsProvider.future),
          )
        ],
      ),
      body: leadsAsync.when(
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (err, stack) => Center(child: Text('Error: $err')),
        data: (leads) {
          if (leads.isEmpty) {
            return const Center(child: Text('No active leads right now.'));
          }
          return ListView.builder(
            itemCount: leads.length,
            itemBuilder: (context, index) {
              final lead = leads[index];
              return Card(
                margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                child: ExpansionTile(
                  leading: const CircleAvatar(child: Icon(Icons.person_search)),
                  title: Text('${lead['reservationType']} - ${lead['customerName']}', style: const TextStyle(fontWeight: FontWeight.bold)),
                  subtitle: Text(lead['reservationStatus']),
                  children: [
                    Padding(
                      padding: const EdgeInsets.all(16.0),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text('Details: ${lead['productName'] ?? lead['reservationNotes'] ?? 'N/A'}'),
                          const SizedBox(height: 16),
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                            children: [
                              OutlinedButton.icon(
                                onPressed: () {},
                                icon: const Icon(Icons.edit_document),
                                label: const Text('Edit Quote'),
                              ),
                              FilledButton.icon(
                                onPressed: () async {
                                  final phone = lead['customerPhone'];
                                  if (phone != null) {
                                    // Simulated WhatsApp launch since url_launcher plugin isn't fully configured
                                    debugPrint('Launching WhatsApp to $phone');
                                    ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Simulated opening WhatsApp for $phone')));
                                  }
                                },
                                icon: const Icon(Icons.chat),
                                label: const Text('WhatsApp Chat'),
                                style: FilledButton.styleFrom(backgroundColor: Colors.green.shade600),
                              ),
                            ],
                          )
                        ],
                      ),
                    ),
                  ],
                ),
              );
            },
          );
        },
      ),
    );
  }
}
