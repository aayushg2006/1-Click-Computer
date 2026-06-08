import 'package:flutter/material.dart';
import '../services/api_service.dart';

class InvoiceGenerationScreen extends StatefulWidget {
  const InvoiceGenerationScreen({super.key});

  @override
  State<InvoiceGenerationScreen> createState() => _InvoiceGenerationScreenState();
}

class _InvoiceGenerationScreenState extends State<InvoiceGenerationScreen> {
  final _customerNameCtrl = TextEditingController();
  final _customerPhoneCtrl = TextEditingController();
  final _items = <Map<String, dynamic>>[];
  bool _isOffline = false;
  bool _isSyncing = false;

  void _addItem() {
    setState(() {
      _items.add({'desc': 'New Item', 'qty': 1, 'price': 1000});
    });
  }

  Future<void> _generateInvoice() async {
    if (_customerNameCtrl.text.isEmpty || _items.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Please enter name and items.')));
      return;
    }

    final subTotal = _items.fold<double>(0, (sum, item) => sum + (item['qty'] * item['price']));

    final offlineInvoice = {
      'invoiceNumber': 'INV-OFF-${DateTime.now().millisecondsSinceEpoch}',
      'customerName': _customerNameCtrl.text,
      'customerPhone': _customerPhoneCtrl.text,
      'subTotal': subTotal,
      'taxAmount': 0,
      'grandTotal': subTotal,
      'isPaid': false,
      'paymentMethod': 'CASH',
      'items': _items.map((i) => {
        'itemDescription': i['desc'],
        'quantity': i['qty'],
        'unitPrice': i['price'],
        'totalPrice': i['qty'] * i['price']
      }).toList()
    };

    if (_isOffline) {
      // Mock saving to SQLite locally
      showDialog(
        context: context,
        builder: (ctx) => AlertDialog(
          title: const Text('Offline Mode'),
          content: const Text('Network down! Invoice cached locally to SQLite. Will auto-sync to Cloud SQL when online.'),
          actions: [TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('OK'))],
        ),
      );
      return;
    }

    setState(() => _isSyncing = true);
    try {
      final res = await ApiService.postEndpoint('/admin/invoices/sync', {
        'invoices': [offlineInvoice]
      });
      
      if (!mounted) return;
      showDialog(
        context: context,
        builder: (ctx) => AlertDialog(
          title: const Text('Invoice Synced'),
          content: Text('Server says: ${res['message']}'),
          actions: [TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('OK'))],
        ),
      );
      
      setState(() {
        _items.clear();
        _customerNameCtrl.clear();
        _customerPhoneCtrl.clear();
      });
    } catch (e) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Sync failed: $e')));
    } finally {
      if (mounted) setState(() => _isSyncing = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('New Invoice'),
        actions: [
          IconButton(
            icon: Icon(_isOffline ? Icons.cloud_off : Icons.cloud_done, color: _isOffline ? Colors.red : Colors.green),
            onPressed: () => setState(() => _isOffline = !_isOffline),
            tooltip: 'Toggle Network Simulation',
          ),
        ],
      ),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            TextField(
              controller: _customerNameCtrl,
              decoration: const InputDecoration(labelText: 'Customer Name', border: OutlineInputBorder()),
            ),
            const SizedBox(height: 8),
            TextField(
              controller: _customerPhoneCtrl,
              decoration: const InputDecoration(labelText: 'Customer Phone', border: OutlineInputBorder()),
            ),
            const SizedBox(height: 16),
            Expanded(
              child: ListView.builder(
                itemCount: _items.length,
                itemBuilder: (ctx, i) => ListTile(
                  title: Text(_items[i]['desc']),
                  subtitle: Text('Qty: ${_items[i]['qty']} | ₹${_items[i]['price']}'),
                  trailing: IconButton(
                    icon: const Icon(Icons.delete, color: Colors.red),
                    onPressed: () => setState(() => _items.removeAt(i)),
                  ),
                ),
              ),
            ),
            Row(
              children: [
                Expanded(
                  child: ElevatedButton.icon(
                    onPressed: _addItem,
                    icon: const Icon(Icons.add),
                    label: const Text('Add Item'),
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: FilledButton.icon(
                    onPressed: _isSyncing ? null : _generateInvoice,
                    icon: const Icon(Icons.picture_as_pdf),
                    label: Text(_isSyncing ? 'Syncing...' : 'Generate & Sync'),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
