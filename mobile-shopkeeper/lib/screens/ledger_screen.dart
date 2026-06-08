import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../services/api_service.dart';

final ledgerProvider = FutureProvider<List<dynamic>>((ref) async {
  return await ApiService.getEndpoint('/admin/ledger/overdue') as List;
});

class LedgerScreen extends ConsumerWidget {
  const LedgerScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final ledgerAsync = ref.watch(ledgerProvider);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Digital Khata / Ledger'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: () => ref.refresh(ledgerProvider.future),
          )
        ],
      ),
      body: ledgerAsync.when(
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (err, stack) => Center(child: Text('Error: $err')),
        data: (clients) => ListView.builder(
          itemCount: clients.length,
          itemBuilder: (context, index) {
            final client = clients[index];
            final due = client['currentBalance'] ?? 0;
            final isOverdue = due > 0;

            return Card(
              margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
              child: ListTile(
                leading: CircleAvatar(
                  backgroundColor: isOverdue ? Colors.red.shade100 : Colors.green.shade100,
                  child: Icon(Icons.person, color: isOverdue ? Colors.red : Colors.green),
                ),
                title: Text(client['customerName'] ?? 'Unknown', style: const TextStyle(fontWeight: FontWeight.bold)),
                subtitle: Text(isOverdue ? 'Overdue Payment' : 'Payment on Track'),
                trailing: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  crossAxisAlignment: CrossAxisAlignment.end,
                  children: [
                    Text('₹$due', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: isOverdue ? Colors.red : Colors.green)),
                    const SizedBox(height: 4),
                    if (isOverdue)
                      InkWell(
                        onTap: () async {
                          try {
                            await ApiService.postEndpoint('/admin/whatsapp/customers/${client['customerId']}/reminder', {});
                            if (!context.mounted) return;
                            ScaffoldMessenger.of(context).showSnackBar(
                              const SnackBar(content: Text('WhatsApp Reminder with UPI link sent!')),
                            );
                          } catch (e) {
                            if (!context.mounted) return;
                            ScaffoldMessenger.of(context).showSnackBar(
                              SnackBar(content: Text('Failed to send nudge: $e')),
                            );
                          }
                        },
                        child: const Row(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            Icon(Icons.message, size: 14, color: Colors.blue),
                            SizedBox(width: 4),
                            Text('Nudge', style: TextStyle(color: Colors.blue, fontSize: 12)),
                          ],
                        ),
                      )
                  ],
                ),
              ),
            );
          },
        ),
      ),
      floatingActionButton: FloatingActionButton.extended(
        onPressed: () {},
        icon: const Icon(Icons.add),
        label: const Text('Add Entry'),
      ),
    );
  }
}
