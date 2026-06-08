import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";

const inter = Inter({ subsets: ["latin"] });

export const metadata: Metadata = {
  title: "1 Click Computer | Premium PC & Repair",
  description: "Nallasopara's leading computer and laptop repair, custom PC builder, and accessories store.",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body className={inter.className}>
        <div className="layout-wrapper">
          <header className="main-header">
            <div className="container header-container">
              <div className="logo">1 Click Computer</div>
              <nav className="main-nav">
                <a href="/">Home</a>
                <a href="/catalog">Accessories</a>
                <a href="/pc-builder">Custom PCs</a>
                <a href="/repair">Repair</a>
              </nav>
            </div>
          </header>
          <main className="main-content">
            {children}
          </main>
          <footer className="main-footer">
            <div className="container">
              <p>&copy; 2026 1 Click Computer, Nallasopara. All rights reserved.</p>
            </div>
          </footer>
        </div>
      </body>
    </html>
  );
}
