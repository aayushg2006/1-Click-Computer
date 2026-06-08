'use client';

import { use, useState, useEffect } from 'react';
import { api } from '@/lib/api';

export default function TicketTrackerPage({ params }: { params: Promise<{ ticketId: string }> }) {
  const unwrappedParams = use(params);
  const ticketId = unwrappedParams.ticketId;
  
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [phoneLast4, setPhoneLast4] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(true);

  const [ticketData, setTicketData] = useState<any>(null);

  useEffect(() => {
    // Fetch public tracking data
    api.tickets.getStatus(ticketId)
      .then(data => {
        setTicketData({
          id: ticketId,
          status: data.status || 'UNKNOWN',
          device: data.deviceDetails || 'Unknown Device',
          date: data.createdAt ? new Date(data.createdAt).toLocaleDateString() : 'N/A',
        });
      })
      .catch(err => {
        console.error("Public ticket fetch failed", err);
        setError('Ticket not found or invalid.');
      })
      .finally(() => setIsLoading(false));
  }, [ticketId]);

  const handleVerify = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    try {
      const verifyRes = await api.customerAccess.verify({
        trackingCode: ticketId,
        mobileNumber: phoneLast4
      });

      if (verifyRes.accessToken) {
        setIsAuthenticated(true);
        // Fetch secure details
        const secureData = await api.customerAccess.getTicketDetails(ticketId, verifyRes.accessToken, phoneLast4);
        
        setTicketData((prev: any) => ({
          ...prev,
          address: secureData.address,
          logs: secureData.updates?.map((u: any) => ({
            time: new Date(u.updatedAt).toLocaleString(),
            event: u.updateNotes
          })) || [],
          photos: secureData.mediaUrls || [],
          estimate: secureData.estimateAmount || 0,
        }));
      }
    } catch (err) {
      setError('Verification failed. Please check the digits and try again.');
    }
  };

  if (isLoading) return <div className="container" style={{ padding: '4rem 0', textAlign: 'center' }}>Loading...</div>;
  if (!ticketData && error) return <div className="container" style={{ padding: '4rem 0', textAlign: 'center', color: 'red' }}>{error}</div>;

  return (
    <div className="container">
      <div style={{ padding: '4rem 0', textAlign: 'center' }}>
        <h1 style={{ fontSize: '2rem', marginBottom: '0.5rem' }}>Repair Tracking</h1>
        <p style={{ color: 'var(--text-secondary)' }}>Ticket: <span style={{ fontFamily: 'monospace', color: 'var(--text-primary)' }}>{ticketId}</span></p>
      </div>

      <div style={{ maxWidth: '800px', margin: '0 auto', display: 'flex', flexDirection: 'column', gap: '2rem' }}>
        
        {/* Public Status Strip */}
        <div className="glass-panel" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '1.5rem' }}>
          <div>
            <h3 style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', textTransform: 'uppercase' }}>Current Status</h3>
            <div style={{ fontSize: '1.5rem', fontWeight: 'bold', color: 'var(--accent-hover)' }}>{ticketData.status}</div>
          </div>
          <div style={{ textAlign: 'right' }}>
            <h3 style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', textTransform: 'uppercase' }}>Device</h3>
            <div style={{ fontWeight: '500' }}>{ticketData.device}</div>
          </div>
        </div>

        {!isAuthenticated ? (
          <div className="glass-panel" style={{ textAlign: 'center', padding: '3rem 2rem' }}>
            <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>🔒</div>
            <h2>Unlock Detailed View</h2>
            <p style={{ color: 'var(--text-secondary)', marginBottom: '1.5rem', maxWidth: '400px', margin: '0 auto 1.5rem' }}>
              To view repair logs, internal photos, and cost estimates, please enter the last 4 digits of your registered mobile number.
            </p>
            <form onSubmit={handleVerify} style={{ display: 'flex', flexDirection: 'column', gap: '1rem', maxWidth: '300px', margin: '0 auto' }}>
              <input 
                type="text" 
                maxLength={4} 
                required 
                placeholder="Last 4 digits"
                value={phoneLast4}
                onChange={e => setPhoneLast4(e.target.value)}
                style={{ background: 'rgba(0,0,0,0.2)', border: '1px solid var(--border-color)', padding: '0.75rem', borderRadius: 'var(--border-radius)', color: 'white', textAlign: 'center', fontSize: '1.25rem', letterSpacing: '4px' }}
              />
              {error && <div style={{ color: 'var(--danger)', fontSize: '0.85rem' }}>{error}</div>}
              <button type="submit" className="btn btn-primary">Verify Identity</button>
            </form>
          </div>
        ) : (
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '2rem' }}>
            <div className="glass-panel">
              <h2 style={{ marginBottom: '1rem' }}>Repair Logs</h2>
              <ul style={{ listStyle: 'none', padding: 0 }}>
                {ticketData.logs?.length === 0 && <li style={{color: 'var(--text-secondary)'}}>No logs yet.</li>}
                {ticketData.logs?.map((log: any, i: number) => (
                  <li key={i} style={{ marginBottom: '1rem', paddingBottom: '1rem', borderBottom: '1px solid var(--border-color)' }}>
                    <div style={{ fontSize: '0.8rem', color: 'var(--text-secondary)', marginBottom: '0.25rem' }}>{log.time}</div>
                    <div>{log.event}</div>
                  </li>
                ))}
              </ul>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '2rem' }}>
              <div className="glass-panel">
                <h2 style={{ marginBottom: '1rem' }}>Estimate</h2>
                <div style={{ fontSize: '2rem', fontWeight: 'bold', color: 'var(--success)', marginBottom: '1rem' }}>
                  ₹{ticketData.estimate}
                </div>
                <button className="btn btn-primary" style={{ width: '100%' }} onClick={() => alert('Quote Approved! Technician will proceed.')}>Approve Quote</button>
              </div>

              <div className="glass-panel">
                <h2 style={{ marginBottom: '1rem' }}>Hardware Condition Photos</h2>
                {ticketData.photos?.length === 0 && <p style={{color: 'var(--text-secondary)'}}>No photos uploaded yet.</p>}
                <div style={{ display: 'flex', gap: '1rem', flexWrap: 'wrap' }}>
                  {ticketData.photos?.map((p: string, i: number) => (
                    <img key={i} src={p} alt={`Condition ${i}`} style={{ width: '100px', height: '100px', objectFit: 'cover', borderRadius: '8px' }} />
                  ))}
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
