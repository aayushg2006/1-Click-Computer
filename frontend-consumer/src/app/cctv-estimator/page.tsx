'use client';

import { useState } from 'react';
import { api } from '@/lib/api';

export default function CctvEstimatorPage() {
  const [cameras, setCameras] = useState(4);
  const [days, setDays] = useState(15);
  
  const [customerName, setCustomerName] = useState('');
  const [phone, setPhone] = useState('');
  const [address, setAddress] = useState('');
  const [pinCode, setPinCode] = useState('');
  
  const [serverEstimate, setServerEstimate] = useState<number | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Simple mock calculation for instant feedback before submission
  const getEstimate = () => {
    let base = cameras * 2500; // Camera + Wiring avg
    let storage = days > 15 ? 4000 : 2000; // Hard drive cost bump
    let dvr = cameras > 4 ? 6000 : 3500;
    return base + storage + dvr;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    try {
      const response = await api.cctvEstimator.estimate({
        cameraCount: cameras,
        backupDays: days,
        customerName,
        customerPhone: phone,
        address,
        pinCode
      });
      
      setServerEstimate(response.estimatedAmount);
      alert(`Estimate saved (Ref: ${response.referenceId})! ${response.leadMessage || 'Our Command Center will contact you to schedule an on-site survey.'}`);
      setCustomerName('');
      setPhone('');
      setAddress('');
      setPinCode('');
    } catch (err) {
      console.error(err);
      alert('Failed to submit estimation request. Please try again later.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="container">
      <div style={{ textAlign: 'center', padding: '4rem 0 2rem' }}>
        <h1 style={{ fontSize: '2.5rem', marginBottom: '1rem' }}>CCTV Custom Estimator</h1>
        <p style={{ color: 'var(--text-secondary)', maxWidth: '600px', margin: '0 auto' }}>
          Instantly calculate approximate costs for your security setup and schedule an on-site physical survey.
        </p>
      </div>

      <div style={{ maxWidth: '600px', margin: '0 auto 4rem' }} className="glass-panel">
        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
          
          <div>
            <label style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '1rem', fontWeight: 'bold' }}>
              <span>Number of Cameras</span>
              <span style={{ color: 'var(--accent-hover)' }}>{cameras} Cameras</span>
            </label>
            <input 
              type="range" 
              min="1" max="16" 
              value={cameras} 
              onChange={e => setCameras(parseInt(e.target.value))}
              style={{ width: '100%', cursor: 'pointer' }}
            />
          </div>

          <div>
            <label style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '1rem', fontWeight: 'bold' }}>
              <span>Recording Backup Required</span>
              <span style={{ color: 'var(--accent-hover)' }}>{days} Days</span>
            </label>
            <input 
              type="range" 
              min="7" max="60" step="7"
              value={days} 
              onChange={e => setDays(parseInt(e.target.value))}
              style={{ width: '100%', cursor: 'pointer' }}
            />
          </div>

          <div style={{ padding: '1.5rem', background: 'rgba(255,255,255,0.05)', borderRadius: 'var(--border-radius)', textAlign: 'center' }}>
            <div style={{ fontSize: '0.9rem', color: 'var(--text-secondary)', textTransform: 'uppercase', letterSpacing: '1px', marginBottom: '0.5rem' }}>Estimated Cost</div>
            <div style={{ fontSize: '3rem', fontWeight: 'bold', color: 'var(--success)' }}>
              ₹{serverEstimate ? serverEstimate.toLocaleString() : getEstimate().toLocaleString()}
            </div>
            <div style={{ fontSize: '0.8rem', color: 'var(--text-secondary)', marginTop: '0.5rem' }}>*Final price may vary after physical survey.</div>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <h3 style={{ borderBottom: '1px solid var(--border-color)', paddingBottom: '0.5rem', marginBottom: '0.5rem' }}>Contact Details for Survey</h3>
            
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              <label style={{ fontSize: '0.9rem', color: 'var(--text-secondary)' }}>Name</label>
              <input type="text" required value={customerName} onChange={e => setCustomerName(e.target.value)} style={{ background: 'rgba(0,0,0,0.2)', border: '1px solid var(--border-color)', padding: '0.75rem', borderRadius: 'var(--border-radius)', color: 'white' }} />
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              <label style={{ fontSize: '0.9rem', color: 'var(--text-secondary)' }}>Mobile Number</label>
              <input type="tel" required value={phone} onChange={e => setPhone(e.target.value)} style={{ background: 'rgba(0,0,0,0.2)', border: '1px solid var(--border-color)', padding: '0.75rem', borderRadius: 'var(--border-radius)', color: 'white' }} />
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              <label style={{ fontSize: '0.9rem', color: 'var(--text-secondary)' }}>Address</label>
              <textarea required rows={2} value={address} onChange={e => setAddress(e.target.value)} style={{ background: 'rgba(0,0,0,0.2)', border: '1px solid var(--border-color)', padding: '0.75rem', borderRadius: 'var(--border-radius)', color: 'white', fontFamily: 'inherit' }}></textarea>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              <label style={{ fontSize: '0.9rem', color: 'var(--text-secondary)' }}>Pin Code</label>
              <input type="text" required value={pinCode} onChange={e => setPinCode(e.target.value)} style={{ background: 'rgba(0,0,0,0.2)', border: '1px solid var(--border-color)', padding: '0.75rem', borderRadius: 'var(--border-radius)', color: 'white' }} />
            </div>
          </div>

          <button type="submit" disabled={isSubmitting} className="btn btn-primary" style={{ padding: '1rem', fontSize: '1.1rem' }}>
            {isSubmitting ? 'Scheduling...' : 'Schedule On-Site Survey'}
          </button>
        </form>
      </div>
    </div>
  );
}
