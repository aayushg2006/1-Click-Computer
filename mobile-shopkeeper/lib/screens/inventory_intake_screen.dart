import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import '../services/api_service.dart';

class InventoryIntakeScreen extends StatefulWidget {
  const InventoryIntakeScreen({super.key});

  @override
  State<InventoryIntakeScreen> createState() => _InventoryIntakeScreenState();
}

class _InventoryIntakeScreenState extends State<InventoryIntakeScreen> {
  bool _isProcessing = false;
  bool _isCommitting = false;
  List<Map<String, dynamic>> _extractedItems = [];

  Future<void> _scanInvoice() async {
    final picker = ImagePicker();
    final file = await picker.pickImage(source: ImageSource.camera);
    
    if (file != null) {
      setState(() => _isProcessing = true);
      // Simulate OCR processing via backend or local engine
      await Future.delayed(const Duration(seconds: 2));
      if (!mounted) return;
      setState(() {
        _isProcessing = false;
        _extractedItems = [
          {'name': 'Crucial 8GB DDR4 RAM', 'qty': 10, 'cost': 1200, 'margin': 30},
          {'name': 'Logitech B100 Mouse', 'qty': 25, 'cost': 250, 'margin': 40},
        ];
      });
    }
  }

  Future<void> _commitInventory() async {
    setState(() => _isCommitting = true);
    try {
      for (final item in _extractedItems) {
        final sellPrice = item['cost'] + (item['cost'] * (item['margin'] / 100));
        await ApiService.postEndpoint('/admin/inventory/add', {
          'name': item['name'],
          'brand': 'Unknown',
          'categoryId': '11111111-1111-1111-1111-111111111111', // Dummy category ID
          'buyingPrice': item['cost'],
          'sellingPrice': sellPrice,
          'initialStock': item['qty'],
          'isAvailableForPickup': true
        });
      }
      if (!mounted) return;
      setState(() => _extractedItems = []);
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Inventory Synced to Server!')));
    } catch (e) {
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Failed to commit: $e')));
    } finally {
      if (mounted) setState(() => _isCommitting = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Smart Inventory Intake (OCR)')),
      body: _isProcessing 
        ? const Center(child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              CircularProgressIndicator(),
              SizedBox(height: 16),
              Text('AI Extracting Invoice Data...')
            ],
          ))
        : _extractedItems.isEmpty
          ? Center(
              child: ElevatedButton.icon(
                onPressed: _scanInvoice,
                icon: const Icon(Icons.camera_alt),
                label: const Text('Scan Supplier Bill'),
              ),
            )
          : Column(
              children: [
                const Padding(
                  padding: EdgeInsets.all(16.0),
                  child: Text('Please verify extracted items and adjust margins before committing.', textAlign: TextAlign.center,),
                ),
                Expanded(
                  child: ListView.builder(
                    itemCount: _extractedItems.length,
                    itemBuilder: (ctx, i) {
                      final item = _extractedItems[i];
                      return Card(
                        margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                        child: Padding(
                          padding: const EdgeInsets.all(12.0),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(item['name'], style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                              const SizedBox(height: 8),
                              Row(
                                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                children: [
                                  Text('Qty: ${item['qty']}'),
                                  Text('Wholesale: ₹${item['cost']}'),
                                ],
                              ),
                              const SizedBox(height: 8),
                              Row(
                                children: [
                                  const Text('Markup Margin %: '),
                                  SizedBox(
                                    width: 60,
                                    child: TextField(
                                      keyboardType: TextInputType.number,
                                      decoration: const InputDecoration(isDense: true, contentPadding: EdgeInsets.all(8)),
                                      controller: TextEditingController(text: item['margin'].toString()),
                                      onChanged: (val) {
                                        item['margin'] = int.tryParse(val) ?? 0;
                                      },
                                    ),
                                  )
                                ],
                              )
                            ],
                          ),
                        ),
                      );
                    },
                  ),
                ),
                Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: SizedBox(
                    width: double.infinity,
                    child: FilledButton(
                      onPressed: _isCommitting ? null : _commitInventory,
                      child: Text(_isCommitting ? 'Syncing...' : 'Commit to Inventory'),
                    ),
                  ),
                )
              ],
            ),
    );
  }
}
