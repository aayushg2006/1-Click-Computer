'use client';

import { useState } from 'react';
import styles from './builder.module.css';
import { api } from '@/lib/api';

const parts = {
  cpu: [
    { id: '11111111-1111-1111-1111-111111111111', name: 'Intel Core i5-13400F', socket: 'LGA1700', price: 18500 },
    { id: '22222222-2222-2222-2222-222222222222', name: 'AMD Ryzen 5 7600', socket: 'AM5', price: 19000 },
  ],
  motherboard: [
    { id: '33333333-3333-3333-3333-333333333333', name: 'MSI PRO B760M-A WIFI', socket: 'LGA1700', price: 14500 },
    { id: '44444444-4444-4444-4444-444444444444', name: 'Gigabyte B650M DS3H', socket: 'AM5', price: 15500 },
  ],
  ram: [
    { id: '55555555-5555-5555-5555-555555555555', name: 'Corsair Vengeance 16GB DDR5', price: 5500 },
    { id: '66666666-6666-6666-6666-666666666666', name: 'Crucial 32GB (2x16GB) DDR5', price: 9500 },
  ],
  gpu: [
    { id: '77777777-7777-7777-7777-777777777777', name: 'NVIDIA RTX 4060 8GB', price: 29000 },
    { id: '88888888-8888-8888-8888-888888888888', name: 'AMD Radeon RX 7600 8GB', price: 26500 },
  ]
};

export default function PcBuilderPage() {
  const [selectedParts, setSelectedParts] = useState<any>({ cpu: null, motherboard: null, ram: null, gpu: null });
  const [pincode, setPincode] = useState('');
  const [customerName, setCustomerName] = useState('');
  const [customerPhone, setCustomerPhone] = useState('');
  const [deliveryType, setDeliveryType] = useState<string | null>(null);
  const [isFinalizing, setIsFinalizing] = useState(false);

  const handleSelect = (category: string, part: any) => {
    setSelectedParts({ ...selectedParts, [category]: part });
  };

  const calculateTotal = () => {
    return Object.values(selectedParts).reduce((sum: number, part: any) => sum + (part ? part.price : 0), 0);
  };

  const checkPincode = () => {
    const validPincodes = ['401203', '401209', '401208']; // Nallasopara/Vasai/Virar examples
    if (validPincodes.includes(pincode)) {
      setDeliveryType('Premium Doorstep Delivery & Physical Setup');
    } else {
      setDeliveryType('Self-Pickup from Nallasopara Store Only');
    }
  };

  const getCompatibilityWarning = () => {
    if (selectedParts.cpu && selectedParts.motherboard) {
      if (selectedParts.cpu.socket !== selectedParts.motherboard.socket) {
        return `Incompatible! CPU is ${selectedParts.cpu.socket} but Motherboard is ${selectedParts.motherboard.socket}.`;
      }
    }
    return null;
  };

  const finalizeQuote = async (e: React.FormEvent) => {
    e.preventDefault();
    if (warning || total === 0 || !customerName || !customerPhone || !pincode) {
      alert("Please fill all details and ensure no compatibility issues.");
      return;
    }

    setIsFinalizing(true);
    const selectedProductIds = Object.values(selectedParts).map((p: any) => p?.id).filter(id => id);

    try {
      const response = await api.pcBuilder.getQuote({
        customerName,
        customerPhone,
        selectedProductIds,
        pinCode: pincode
      });

      if (response.whatsappMessage) {
        window.open(`https://wa.me/918421876082?text=${encodeURIComponent(response.whatsappMessage)}`, '_blank');
      } else {
        alert("Quote generated, but WhatsApp link missing from server response.");
      }
    } catch (err) {
      console.error(err);
      alert('Failed to generate quote via API. Make sure the backend is running.');
    } finally {
      setIsFinalizing(false);
    }
  };

  const warning = getCompatibilityWarning();
  const total = calculateTotal();

  return (
    <div className="container">
      <div className={styles.header}>
        <h1 className={styles.title}>Localized Custom PC Builder</h1>
        <p className={styles.subtitle}>Select your components. Our live compatibility matrix ensures everything fits perfectly.</p>
      </div>

      <div className={styles.layout}>
        <div className={styles.partsSelection}>
          {Object.entries(parts).map(([category, options]) => (
            <div key={category} className="glass-panel" style={{ marginBottom: '1.5rem' }}>
              <h2 className={styles.categoryTitle}>{category.toUpperCase()}</h2>
              <div className={styles.grid}>
                {options.map(opt => (
                  <div 
                    key={opt.id} 
                    className={`${styles.partCard} ${selectedParts[category]?.id === opt.id ? styles.selected : ''}`}
                    onClick={() => handleSelect(category, opt)}
                  >
                    <h4>{opt.name}</h4>
                    <span className={styles.price}>₹{opt.price}</span>
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>

        <div className={styles.summarySidebar}>
          <div className="glass-panel">
            <h2>Build Summary</h2>
            
            {warning && <div className={styles.warning}>{warning}</div>}

            <ul className={styles.summaryList}>
              <li><span>CPU:</span> {selectedParts.cpu?.name || '---'}</li>
              <li><span>Mobo:</span> {selectedParts.motherboard?.name || '---'}</li>
              <li><span>RAM:</span> {selectedParts.ram?.name || '---'}</li>
              <li><span>GPU:</span> {selectedParts.gpu?.name || '---'}</li>
            </ul>

            <div className={styles.totalRow}>
              <span>Total Estimate:</span>
              <span>₹{total}</span>
            </div>

            <hr className={styles.divider} />

            <form onSubmit={finalizeQuote}>
              <div className={styles.deliverySection}>
                <label>Your Name</label>
                <input 
                  type="text" 
                  value={customerName} 
                  onChange={(e) => setCustomerName(e.target.value)} 
                  className={styles.input} 
                  required
                /><br/>

                <label>Phone Number</label>
                <input 
                  type="tel" 
                  value={customerPhone} 
                  onChange={(e) => setCustomerPhone(e.target.value)} 
                  className={styles.input} 
                  required
                /><br/>

                <label>Delivery Pincode</label>
                <div style={{ display: 'flex', gap: '0.5rem', marginTop: '0.5rem' }}>
                  <input 
                    type="text" 
                    value={pincode} 
                    onChange={(e) => setPincode(e.target.value)} 
                    className={styles.input} 
                    placeholder="e.g. 401209"
                    required
                  />
                  <button type="button" className="btn btn-secondary" onClick={checkPincode}>Verify</button>
                </div>
                {deliveryType && (
                  <div className={`${styles.deliveryType} ${deliveryType.includes('Doorstep') ? styles.success : styles.neutral}`}>
                    {deliveryType}
                  </div>
                )}
              </div>

              <button 
                type="submit" 
                disabled={isFinalizing || !!warning || total === 0}
                className={`btn btn-primary ${styles.whatsappBtn} ${warning || total === 0 ? styles.disabled : ''}`}
                style={{ width: '100%', marginTop: '1rem' }}
              >
                {isFinalizing ? 'Finalizing...' : 'Finalize on WhatsApp'}
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
}
