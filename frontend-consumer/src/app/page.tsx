import styles from './page.module.css';

export default function Home() {
  return (
    <div className={styles.homeContainer}>
      {/* Hero Section */}
      <section className={styles.hero}>
        <div className="container">
          <div className={styles.heroContent}>
            <h1 className={styles.title}>
              Nallasopara's Premier <br />
              <span className={styles.gradientText}>Computer Solutions</span>
            </h1>
            <p className={styles.subtitle}>
              From custom gaming rigs to doorstep laptop repairs, we provide fast, reliable, and transparent tech services in Vasai-Virar.
            </p>
            <div className={styles.ctaGroup}>
              <a href="/pc-builder" className="btn btn-primary">Build Your Custom PC</a>
              <a href="/repair" className="btn btn-secondary">Book Doorstep Repair</a>
            </div>
          </div>
        </div>
      </section>

      {/* Services Section */}
      <section className={styles.servicesSection}>
        <div className="container">
          <h2 className={styles.sectionTitle}>Our Services</h2>
          <div className={styles.grid}>
            <div className="card">
              <h3>Custom PC Builds</h3>
              <p>Use our live compatibility engine to design your dream PC. Free doorstep setup in Vasai-Virar.</p>
              <a href="/pc-builder" className={styles.cardLink}>Start Building &rarr;</a>
            </div>
            <div className="card">
              <h3>Doorstep Repairs</h3>
              <p>Book a technician to visit your home. Track your repair status live without logging in.</p>
              <a href="/repair" className={styles.cardLink}>Book Now &rarr;</a>
            </div>
            <div className="card">
              <h3>Smart Accessories</h3>
              <p>Reserve premium accessories online and pick them up instantly from our Nallasopara store.</p>
              <a href="/catalog" className={styles.cardLink}>Browse Catalog &rarr;</a>
            </div>
            <div className="card">
              <h3>CCTV Solutions</h3>
              <p>Get an instant cost estimate for your security needs and schedule an on-site survey.</p>
              <a href="/cctv-estimator" className={styles.cardLink}>Get Estimate &rarr;</a>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
