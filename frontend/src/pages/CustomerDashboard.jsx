import React, { useState, useEffect } from 'react';
import { Navbar } from '../components/Navbar';
import { useAuth } from '../context/AuthContext';
import { authApi } from '../api/auth';

export function CustomerDashboard() {
  const { user, token } = useAuth();
  const [dashboardData, setDashboardData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    async function fetchDashboard() {
      try {
        const data = await authApi.getCustomerDashboard(token);
        setDashboardData(data);
      } catch (err) {
        setError(err.message || 'Failed to load Customer Balance Portal from backend.');
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
            <h1>My Store Utang & Credit Overview</h1>
            <p className="dashboard-subtext">
              Welcome, <strong>{user?.fullName}</strong> ({user?.username}) • Registered Mobile: {user?.mobileNumber}
            </p>
          </div>
        </div>

        {error && (
          <div className="alert alert-error" role="alert">
            <span>⚠️ {error}</span>
          </div>
        )}

        {loading ? (
          <div className="loading-card">
            <p>Loading your balance record from Spring Boot backend...</p>
          </div>
        ) : (
          <>
            <div className="stats-grid">
              <div className="stat-card stat-highlight">
                <div className="stat-icon">💳</div>
                <div className="stat-info">
                  <span className="stat-label">Outstanding Utang Balance</span>
                  <span className="stat-value text-accent">{dashboardData?.accountOverview?.currentUtangBalance || 'PHP 0.00'}</span>
                </div>
              </div>

              <div className="stat-card">
                <div className="stat-icon">🛡️</div>
                <div className="stat-info">
                  <span className="stat-label">Assigned Credit Limit</span>
                  <span className="stat-value">{dashboardData?.accountOverview?.creditLimit || 'PHP 2,000.00'}</span>
                </div>
              </div>

              <div className="stat-card">
                <div className="stat-icon">📅</div>
                <div className="stat-info">
                  <span className="stat-label">Payment Due Date</span>
                  <span className="stat-value">{dashboardData?.accountOverview?.dueDate || 'No Active Due Date'}</span>
                </div>
              </div>

              <div className="stat-card">
                <div className="stat-icon">📱</div>
                <div className="stat-info">
                  <span className="stat-label">SMS Alert Channel</span>
                  <span className="stat-value">Active</span>
                </div>
              </div>
            </div>

            <div className="section-card">
              <h3>Account & Profile Details</h3>

              <div className="info-table-wrapper">
                <table className="info-table">
                  <tbody>
                    <tr>
                      <td><strong>Assigned Role:</strong></td>
                      <td><code>{dashboardData?.role || user?.role}</code></td>
                    </tr>
                    <tr>
                      <td><strong>Borrower Name:</strong></td>
                      <td>{dashboardData?.customerName || user?.fullName}</td>
                    </tr>
                    <tr>
                      <td><strong>Registered Phone:</strong></td>
                      <td>{dashboardData?.mobileNumber || user?.mobileNumber}</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>

            <div className="section-card">
              <h3>Online Utang Settlement (Increment 3 Feature)</h3>
              <p className="card-description">
                Customers will be able to settle their utang balance cashless using Philippine e-wallets via PayMongo.
              </p>
              <div className="action-buttons-grid">
                <button className="action-btn disabled" title="Integrated in Increment 3 via PayMongo">
                  <span className="action-btn-icon">💙</span>
                  <span>Pay via GCash (Upcoming)</span>
                </button>
                <button className="action-btn disabled" title="Integrated in Increment 3 via PayMongo">
                  <span className="action-btn-icon">💚</span>
                  <span>Pay via Maya (Upcoming)</span>
                </button>
              </div>
            </div>
          </>
        )}
      </main>
    </div>
  );
}
