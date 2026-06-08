import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../services/api_service.dart';

final ticketsProvider = FutureProvider<List<dynamic>>((ref) async {
  return await ApiService.getEndpoint('/admin/tickets') as List;
});

class FieldCRMScreen extends ConsumerWidget {
  const FieldCRMScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final ticketsAsync = ref.watch(ticketsProvider);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Field Technician Tasks'),
        actions: [
          IconButton(icon: const Icon(Icons.refresh), onPressed: () => ref.refresh(ticketsProvider.future)),
        ],
      ),
      body: ticketsAsync.when(
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (err, stack) => Center(child: Text('Error: $err')),
        data: (tickets) {
          if (tickets.isEmpty) return const Center(child: Text('No assigned tickets.'));
          return ListView.builder(
            itemCount: tickets.length,
            itemBuilder: (context, index) {
              final ticket = tickets[index];
              return Card(
                margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                child: Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Text(ticket['trackingCode'] ?? 'Unknown', style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 18)),
                          Chip(
                            label: Text(ticket['status'] ?? 'NEW'),
                            backgroundColor: Colors.blue.withValues(alpha: 0.2),
                          ),
                        ],
                      ),
                      const SizedBox(height: 8),
                      Row(
                        children: [
                          const Icon(Icons.location_on, size: 16, color: Colors.grey),
                          const SizedBox(width: 4),
                          Expanded(child: Text(ticket['address'] ?? 'No address provided')),
                        ],
                      ),
                      const SizedBox(height: 8),
                      Text('Device: ${ticket['deviceDetails'] ?? 'Unknown'}'),
                      const SizedBox(height: 16),
                      Row(
                        children: [
                          Expanded(
                            child: OutlinedButton.icon(
                              onPressed: () {},
                              icon: const Icon(Icons.camera_alt),
                              label: const Text('Photos'),
                            ),
                          ),
                          const SizedBox(width: 16),
                          Expanded(
                            child: FilledButton(
                              onPressed: () async {
                                final trackingCode = ticket['trackingCode'];
                                if (trackingCode != null) {
                                  try {
                                    await ApiService.postEndpoint('/admin/tickets/$trackingCode/updates', {
                                      'statusTitle': 'IN_PROGRESS',
                                      'statusDescription': 'Technician is working on it.',
                                      'visibleToCustomer': true
                                    });
                                    if (!context.mounted) return;
                                    ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Status Updated to IN_PROGRESS')));
                                    ref.invalidate(ticketsProvider);
                                  } catch (e) {
                                    if (!context.mounted) return;
                                    ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Failed to update: $e')));
                                  }
                                }
                              },
                              child: const Text('Mark IN PROGRESS'),
                            ),
                          ),
                        ],
                      )
                    ],
                  ),
                ),
              );
            },
          );
        },
      ),
    );
  }
}
