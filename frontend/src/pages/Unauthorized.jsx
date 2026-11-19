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
        <div style={{ fontSize: '48px', marginBottom: '16px' }}>🚫</div>
        <h2>403 - Access Denied</h2>
        <p className="auth-subtitle">
          Your account role (<code>{user?.role || 'GUEST'}</code>) does not have permission to access this page.
        </p>

        <div style={{ margin: '24px 0', padding: '16px', background: '#FEF2F2', border: '1px solid #FECACA', borderRadius: '8px', color: '#991B1B', fontSize: '14px' }}>
          Role-Based Access Control (RBAC) is enforced by both the React route guard and the Spring Security backend.
        </div>

        <button onClick={handleGoDashboard} className="btn btn-primary btn-block">
          Return to My Dashboard
        </button>
      </div>
    </div>
  );
}
