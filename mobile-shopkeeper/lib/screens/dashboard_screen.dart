import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../services/api_service.dart';

final dashboardDataProvider = FutureProvider<Map<String, dynamic>>((ref) async {
  try {
    final tickets = await ApiService.getEndpoint('/admin/tickets') as List;
    final reservations = await ApiService.getEndpoint('/admin/reservations') as List;
    // We mock ledger and inventory counts for now, but these would hit their respective endpoints.
    
    return {
      'pendingTickets': tickets.length.toString(),
      'newLeads': reservations.length.toString(),
      'khataDue': '₹45k', 
      'lowStock': '12',
    };
  } catch (e) {
    return {
      'pendingTickets': 'ERR',
      'newLeads': 'ERR',
      'khataDue': 'ERR',
      'lowStock': 'ERR',
    };
  }
});

class DashboardScreen extends ConsumerWidget {
  const DashboardScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final dashboardAsync = ref.watch(dashboardDataProvider);

    return Scaffold(
      appBar: AppBar(
        title: const Text('Command Center Dashboard'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: () => ref.invalidate(dashboardDataProvider),
          )
        ],
      ),
      body: dashboardAsync.when(
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (err, stack) => Center(child: Text('Error: $err')),
        data: (data) => GridView.count(
          padding: const EdgeInsets.all(16),
          crossAxisCount: 2,
          crossAxisSpacing: 16,
          mainAxisSpacing: 16,
          children: [
            _DashboardCard(icon: Icons.pending_actions, title: 'Tickets', count: data['pendingTickets']),
            _DashboardCard(icon: Icons.notifications_active, title: 'Reservations', count: data['newLeads'], color: Colors.orange),
            _DashboardCard(icon: Icons.account_balance_wallet, title: 'Khata Due', count: data['khataDue'], color: Colors.red),
            _DashboardCard(icon: Icons.inventory, title: 'Low Stock', count: data['lowStock'], color: Colors.orange),
          ],
        ),
      ),
    );
  }
}

class _DashboardCard extends StatelessWidget {
  final IconData icon;
  final String title;
  final String count;
  final Color color;

  const _DashboardCard({
    required this.icon,
    required this.title,
    required this.count,
    this.color = Colors.blue,
  });

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 4,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(icon, size: 48, color: color),
            const SizedBox(height: 8),
            Text(title, style: const TextStyle(fontSize: 14, color: Colors.grey)),
            const SizedBox(height: 4),
            Text(count, style: const TextStyle(fontSize: 24, fontWeight: FontWeight.bold)),
          ],
        ),
      ),
    );
  }
}
