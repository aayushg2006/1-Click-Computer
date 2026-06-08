'use client';

import { useState, useEffect } from 'react';
import styles from './catalog.module.css';
import { api } from '@/lib/api';

export default function CatalogPage() {
  const [accessories, setAccessories] = useState<any[]>([]);
  const [selectedItem, setSelectedItem] = useState<any>(null);
  const [isBooking, setIsBooking] = useState(false);
  const [loading, setLoading] = useState(true);

  // Form State
  const [customerName, setCustomerName] = useState('');
  const [customerPhone, setCustomerPhone] = useState('');
  const [pickupTimeslot, setPickupTimeslot] = useState('');

  useEffect(() => {
    api.catalog.getProducts()
      .then(data => {
        setAccessories(data);
        setLoading(false);
      })
      .catch(err => {
        console.error('Failed to fetch catalog', err);
        setLoading(false);
      });
  }, []);

  const handleReserve = (item: any) => {
    setSelectedItem(item);
    setIsBooking(true);
  };

  const submitReservation = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await api.reservations.create({
        productId: selectedItem.id,
        customerName: customerName,
        customerPhone: customerPhone,
        expectedPickupTimeslot: pickupTimeslot
      });
      alert(`Successfully reserved ${selectedItem.name} for Store Pickup at Nallasopara branch!`);
      setIsBooking(false);
      setSelectedItem(null);
      setCustomerName('');
      setCustomerPhone('');
      setPickupTimeslot('');
    } catch (err) {
      alert('Failed to create reservation. Please try again.');
    }
  };

  if (loading) {
    return <div className="container" style={{textAlign: 'center', padding: '50px'}}>Loading products...</div>;
  }

  return (
    <div className="container">
      <div className={styles.catalogHeader}>
        <h1 className={styles.title}>Smart Accessories Catalog</h1>
        <p className={styles.subtitle}>Premium gear for everyday computing and gaming. Strict "Store Pickup" only policy applies to avoid shipping logistics.</p>
      </div>

      <div className={styles.grid}>
        {accessories.map((item) => (
          <div key={item.id} className="card">
            <img src={item.primaryImageUrl || 'https://via.placeholder.com/200'} alt={item.name} className={styles.productImage} />
            <div className={styles.productInfo}>
              <span className={styles.category}>{item.categoryName}</span>
              <h3>{item.name}</h3>
              <div className={styles.priceRow}>
                <span className={styles.price}>₹{item.price}</span>
                {item.inStock ? (
                  <button className="btn btn-primary" onClick={() => handleReserve(item)}>
                    Reserve for Pickup
                  </button>
                ) : (
                  <span className={styles.outOfStock}>Out of Stock</span>
                )}
              </div>
            </div>
          </div>
        ))}
      </div>

      {isBooking && selectedItem && (
        <div className={styles.modalOverlay}>
          <div className="glass-panel">
            <h2>Reserve {selectedItem.name}</h2>
            <p>Select a timeslot to visit our Nallasopara store.</p>
            <form onSubmit={submitReservation} className={styles.form}>
              <div className={styles.formGroup}>
                <label>Your Name</label>
                <input type="text" required className={styles.input} value={customerName} onChange={e => setCustomerName(e.target.value)} />
              </div>
              <div className={styles.formGroup}>
                <label>Mobile Number</label>
                <input type="tel" required className={styles.input} value={customerPhone} onChange={e => setCustomerPhone(e.target.value)} />
              </div>
              <div className={styles.formGroup}>
                <label>Pickup Date & Time</label>
                <input type="datetime-local" required className={styles.input} value={pickupTimeslot} onChange={e => setPickupTimeslot(e.target.value)} />
              </div>
              <div className={styles.modalActions}>
                <button type="button" className="btn btn-secondary" onClick={() => setIsBooking(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary">Confirm Reservation</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
