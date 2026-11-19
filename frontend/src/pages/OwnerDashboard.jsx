import React, { useState, useEffect } from 'react';
import { Navbar } from '../components/Navbar';
import { useAuth } from '../context/AuthContext';
import { authApi } from '../api/auth';

export function OwnerDashboard() {
  const { user, token } = useAuth();
  const [dashboardData, setDashboardData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    async function fetchDashboard() {
      try {
        const data = await authApi.getOwnerDashboard(token);
        setDashboardData(data);
      } catch (err) {
        setError(err.message || 'Failed to load Store Owner Dashboard from backend.');
      } finally {
        setLoading(false);
      }
    }
    fetchDashboard();
  }, [token]);

  return (
    <div className="dashboard-layout">
      <Navbar />

      <main className="dashboard-content">
        <div className="dashboard-header">
          <div>
            <span className="badge badge-owner">STORE OWNER DASHBOARD</span>
            <h1>Sari-Sari Store Management Hub</h1>
            <p className="dashboard-subtext">
              Welcome back, <strong>{user?.fullName}</strong> ({user?.username}) • Mobile: {user?.mobileNumber}
            </p>
          </div>
          <div className="status-pill status-online">
            <span className="dot"></span>
            <span>Authenticated via Supabase PostgreSQL</span>
          </div>
        </div>

        {error && (
          <div className="alert alert-error" role="alert">
            <span>⚠️ {error}</span>
          </div>
        )}

        {loading ? (
          <div className="loading-card">
            <p>Loading real-time store metrics from Spring Boot backend...</p>
          </div>
        ) : (
          <>
            <div className="stats-grid">
              <div className="stat-card">
                <div className="stat-icon">📦</div>
                <div className="stat-info">
                  <span className="stat-label">Product Inventory</span>
                  <span className="stat-value">Active Catalog</span>
                  <span className="stat-meta">Ready for Increment 2 POS</span>
                </div>
              </div>

              <div className="stat-card">
                <div className="stat-icon">⚠️</div>
                <div className="stat-info">
                  <span className="stat-label">Low Stock Alerts</span>
                  <span className="stat-value">0 Items</span>
                  <span className="stat-meta">Automated threshold warnings</span>
                </div>
              </div>

              <div className="stat-card">
                <div className="stat-icon">📋</div>
                <div className="stat-info">
                  <span className="stat-label">Total Utang Receivables</span>
                  <span className="stat-value">PHP 0.00</span>
                  <span className="stat-meta">Digital customer ledger</span>
                </div>
              </div>

              <div className="stat-card">
                <div className="stat-icon">👥</div>
                <div className="stat-info">
                  <span className="stat-label">Registered Debtors</span>
                  <span className="stat-value">1 Active</span>
                  <span className="stat-meta">Credit limits monitored</span>
                </div>
              </div>
            </div>

            <div className="section-card">
              <h3>Increment 1: Core Authentication & RBAC Verification</h3>
              <p className="card-description">
                You are securely authenticated as a <strong>Sari-Sari Store Owner</strong>. Spring Security and PostgreSQL enforce role-based access control, preventing customers or unauthenticated users from querying this endpoint.
              </p>

              <div className="info-table-wrapper">
                <table className="info-table">
                  <tbody>
                    <tr>
                      <td><strong>Account Role:</strong></td>
                      <td><code>{dashboardData?.role || user?.role}</code></td>
                    </tr>
                    <tr>
                      <td><strong>Account Holder:</strong></td>
                      <td>{dashboardData?.ownerName || user?.fullName}</td>
                    </tr>
                    <tr>
                      <td><strong>Registered Mobile:</strong></td>
                      <td>{dashboardData?.mobileNumber || user?.mobileNumber}</td>
                    </tr>
                    <tr>
                      <td><strong>Backend Status:</strong></td>
                      <td>{dashboardData?.message || 'Spring Boot REST API connected'}</td>
                    </tr>
                    <tr>
                      <td><strong>Server Verification Time:</strong></td>
                      <td>{dashboardData?.serverTime ? new Date(dashboardData.serverTime).toLocaleString() : 'Live'}</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>

            <div className="section-card">
              <h3>Upcoming Store Operations (Increments 2 & 3)</h3>
              <div className="action-buttons-grid">
                <button className="action-btn disabled" title="Scheduled for Increment 2">
                  <span className="action-btn-icon">🛒</span>
                  <span>Point of Sale (POS) Cash Sale</span>
                </button>
                <button className="action-btn disabled" title="Scheduled for Increment 2">
                  <span className="action-btn-icon">📝</span>
                  <span>Record Utang (Credit) Charge</span>
                </button>
                <button className="action-btn disabled" title="Scheduled for Increment 3">
                  <span className="action-btn-icon">📱</span>
                  <span>Dispatch Semaphore SMS Reminders</span>
                </button>
                <button className="action-btn disabled" title="Scheduled for Increment 3">
                  <span className="action-btn-icon">💳</span>
                  <span>PayMongo Digital Reconciliation</span>
                </button>
              </div>
            </div>
          </>
        )}
      </main>
    </div>
  );
}
