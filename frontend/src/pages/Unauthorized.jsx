import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export function Unauthorized() {
  const { user } = useAuth();
  const navigate = useNavigate();

  const handleGoDashboard = () => {
    if (user?.role === 'STORE_OWNER') {
      navigate('/owner/dashboard');
    } else if (user?.role === 'CUSTOMER') {
      navigate('/customer/dashboard');
    } else {
      navigate('/login');
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card" style={{ textAlign: 'center' }}>
        <img
          src="/logo.png"
          alt="TindahanKo Logo"
          className="auth-logo-img"
          style={{ maxWidth: '210px', width: '100%', height: 'auto', maxHeight: '54px', objectFit: 'contain', margin: '0 auto 16px', display: 'block' }}
        />
        <div style={{ fontSize: '36px', margin: '8px 0 12px' }}>🚫</div>
        <h2>403 - Access Denied</h2>
        <p className="auth-subtitle" style={{ marginBottom: '24px' }}>
          Your account role <code>{user?.role || 'GUEST'}</code> does not have permission to access this page.
        </p>

        <button onClick={handleGoDashboard} className="btn btn-primary btn-block">
          Return to My Dashboard
        </button>
      </div>
    </div>
  );
}
