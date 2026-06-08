'use client';

import { useState } from 'react';
import styles from './consultation.module.css';

export default function LaptopConsultationPage() {
  const [step, setStep] = useState(1);
  const [formData, setFormData] = useState({
    useCase: '',
    budget: '',
    name: '',
    phone: '',
  });

  const handleNext = () => setStep(step + 1);
  const handleBack = () => setStep(step - 1);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    // Would POST to LeadManagement endpoint
    alert('High-intent lead registered! We will call you back shortly with a custom quotation.');
    setStep(4); // Success step
  };

  return (
    <div className="container">
      <div className={styles.header}>
        <h1 className={styles.title}>Laptop Consultation Engine</h1>
        <p className={styles.subtitle}>Answer a few quick questions and we'll build a custom quotation perfect for your needs.</p>
      </div>

      <div className={styles.wizardContainer}>
        <div className="glass-panel">
          {step === 1 && (
            <div className={styles.step}>
              <h2>Step 1: Primary Use Case</h2>
              <div className={styles.optionsGrid}>
                {['Coding & B.Tech', 'AAA Gaming', 'Basic Office / Accounts', 'Video Editing / 3D Design'].map(opt => (
                  <button 
                    key={opt}
                    className={`btn ${formData.useCase === opt ? 'btn-primary' : 'btn-secondary'} ${styles.optionBtn}`}
                    onClick={() => setFormData({...formData, useCase: opt})}
                  >
                    {opt}
                  </button>
                ))}
              </div>
              <div className={styles.actions}>
                <button className="btn btn-primary" onClick={handleNext} disabled={!formData.useCase}>Next Step</button>
              </div>
            </div>
          )}

          {step === 2 && (
            <div className={styles.step}>
              <h2>Step 2: Target Budget</h2>
              <div className={styles.optionsGrid}>
                {['Under ₹30,000', '₹30,000 - ₹50,000', '₹50,000 - ₹80,000', 'Above ₹80,000'].map(opt => (
                  <button 
                    key={opt}
                    className={`btn ${formData.budget === opt ? 'btn-primary' : 'btn-secondary'} ${styles.optionBtn}`}
                    onClick={() => setFormData({...formData, budget: opt})}
                  >
                    {opt}
                  </button>
                ))}
              </div>
              <div className={styles.actions}>
                <button className="btn btn-secondary" onClick={handleBack}>Back</button>
                <button className="btn btn-primary" onClick={handleNext} disabled={!formData.budget}>Next Step</button>
              </div>
            </div>
          )}

          {step === 3 && (
            <div className={styles.step}>
              <h2>Step 3: Contact Details</h2>
              <p>We'll send your custom quotation directly via WhatsApp or call you to discuss.</p>
              <form onSubmit={handleSubmit} className={styles.form}>
                <div className={styles.formGroup}>
                  <label>Full Name</label>
                  <input type="text" required className={styles.input} onChange={e => setFormData({...formData, name: e.target.value})} />
                </div>
                <div className={styles.formGroup}>
                  <label>Mobile Number (WhatsApp)</label>
                  <input type="tel" required className={styles.input} onChange={e => setFormData({...formData, phone: e.target.value})} />
                </div>
                <div className={styles.actions}>
                  <button type="button" className="btn btn-secondary" onClick={handleBack}>Back</button>
                  <button type="submit" className="btn btn-primary">Get My Quote</button>
                </div>
              </form>
            </div>
          )}

          {step === 4 && (
            <div className={styles.stepSuccess}>
              <div className={styles.successIcon}>✓</div>
              <h2>Request Received!</h2>
              <p>Your details have been securely transmitted to our shop floor in Nallasopara. You'll hear from us within 15 minutes.</p>
              <button className="btn btn-secondary mt-4" onClick={() => window.location.href='/'}>Return Home</button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
