'use client';

import { useState } from 'react';
import { api } from '@/lib/api';

export default function RepairPage() {
  const [ticketId, setTicketId] = useState('');
  
  // Booking Form State
  const [customerName, setCustomerName] = useState('');
  const [customerPhone, setCustomerPhone] = useState('');
  const [deviceDetails, setDeviceDetails] = useState('');
  const [address, setAddress] = useState('');
  const [pinCode, setPinCode] = useState('');
  const [isBooking, setIsBooking] = useState(false);

  const handleTrack = (e: React.FormEvent) => {
    e.preventDefault();
    if (ticketId) {
      window.location.href = `/track/${ticketId}`;
    }
  };

  const handleBooking = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsBooking(true);
    try {
      const response = await api.repairs.bookDoorstep({
        customerName,
        customerPhone,
        deviceDetails,
        address,
        pinCode
      });
      alert(`Booking confirmed! Your Tracking ID is ${response.trackingCode || 'sent via WhatsApp'}.`);
      setCustomerName('');
      setCustomerPhone('');
      setDeviceDetails('');
      setAddress('');
      setPinCode('');
    } catch (err) {
      console.error(err);
      alert('Failed to book repair. Please try again.');
    } finally {
      setIsBooking(false);
    }
  };

  return (
    <div className="container">
      <div style={{ textAlign: 'center', padding: '4rem 0' }}>
        <h1 style={{ fontSize: '2.5rem', marginBottom: '1rem' }}>Doorstep Repair Hub</h1>
        <p style={{ color: 'var(--text-secondary)', maxWidth: '600px', margin: '0 auto 2rem' }}>
          Book a home visit from our expert technicians or drop your device at our Nallasopara store.
        </p>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '2rem' }}>
        
        {/* Booking Form */}
        <div className="glass-panel">
          <h2 style={{ marginBottom: '1.5rem', color: 'var(--accent-hover)' }}>Book a Repair</h2>
          <form onSubmit={handleBooking} style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              <label style={{ fontSize: '0.9rem', color: 'var(--text-secondary)' }}>Your Name</label>
              <input type="text" required value={customerName} onChange={e => setCustomerName(e.target.value)} style={{ background: 'rgba(0,0,0,0.2)', border: '1px solid var(--border-color)', padding: '0.75rem', borderRadius: 'var(--border-radius)', color: 'white' }} />
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              <label style={{ fontSize: '0.9rem', color: 'var(--text-secondary)' }}>Phone Number</label>
              <input type="tel" required value={customerPhone} onChange={e => setCustomerPhone(e.target.value)} style={{ background: 'rgba(0,0,0,0.2)', border: '1px solid var(--border-color)', padding: '0.75rem', borderRadius: 'var(--border-radius)', color: 'white' }} />
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              <label style={{ fontSize: '0.9rem', color: 'var(--text-secondary)' }}>Device Issue</label>
              <textarea required rows={2} value={deviceDetails} onChange={e => setDeviceDetails(e.target.value)} style={{ background: 'rgba(0,0,0,0.2)', border: '1px solid var(--border-color)', padding: '0.75rem', borderRadius: 'var(--border-radius)', color: 'white', fontFamily: 'inherit' }} placeholder="E.g., Screen broken, won't turn on"></textarea>
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              <label style={{ fontSize: '0.9rem', color: 'var(--text-secondary)' }}>Address</label>
              <textarea required rows={2} value={address} onChange={e => setAddress(e.target.value)} style={{ background: 'rgba(0,0,0,0.2)', border: '1px solid var(--border-color)', padding: '0.75rem', borderRadius: 'var(--border-radius)', color: 'white', fontFamily: 'inherit' }}></textarea>
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              <label style={{ fontSize: '0.9rem', color: 'var(--text-secondary)' }}>Pin Code</label>
              <input type="text" required value={pinCode} onChange={e => setPinCode(e.target.value)} style={{ background: 'rgba(0,0,0,0.2)', border: '1px solid var(--border-color)', padding: '0.75rem', borderRadius: 'var(--border-radius)', color: 'white' }} />
            </div>
            <button type="submit" className="btn btn-primary" style={{ marginTop: '1rem' }} disabled={isBooking}>
              {isBooking ? 'Booking...' : 'Confirm Booking'}
            </button>
          </form>
        </div>

        {/* Tracker */}
        <div className="glass-panel" style={{ height: 'max-content' }}>
          <h2 style={{ marginBottom: '1.5rem' }}>Track Existing Repair</h2>
          <p style={{ color: 'var(--text-secondary)', marginBottom: '1.5rem' }}>
            Enter your unique secure Tracking ID to see live updates, photos, and cost estimates.
          </p>
          <form onSubmit={handleTrack} style={{ display: 'flex', gap: '0.5rem' }}>
            <input 
              type="text" 
              placeholder="e.g. 550e8400-e29b..." 
              required 
              value={ticketId}
              onChange={(e) => setTicketId(e.target.value)}
              style={{ flex: 1, background: 'rgba(0,0,0,0.2)', border: '1px solid var(--border-color)', padding: '0.75rem', borderRadius: 'var(--border-radius)', color: 'white' }}
            />
            <button type="submit" className="btn btn-secondary">Track</button>
          </form>
        </div>

      </div>
    </div>
  );
}
