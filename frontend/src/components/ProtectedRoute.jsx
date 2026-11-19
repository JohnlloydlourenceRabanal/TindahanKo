import React, { useEffect } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export function ProtectedRoute({ children, allowedRoles }) {
  const { user, isAuthenticated, loading, logout } = useAuth();
  const location = useLocation();
  const isRoleDenied = allowedRoles && user && !allowedRoles.includes(user.role);

  useEffect(() => {
    if (isRoleDenied) {
      logout();
    }
  }, [isRoleDenied, logout]);

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', fontFamily: 'sans-serif' }}>
        <p>Loading...</p>
      </div>
    );
  }

  if (!isAuthenticated || isRoleDenied) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  return children;
}
