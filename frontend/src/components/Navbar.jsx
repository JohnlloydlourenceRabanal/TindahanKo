import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const isOwner = user?.role === 'STORE_OWNER';

  return (
    <header className="navbar">
      <div className="navbar-container">
        <div className="navbar-brand">
          <Link to={isOwner ? '/owner/dashboard' : '/customer/dashboard'} className="brand-logo">
            🏪 TindahanKo
          </Link>
          <span className="brand-badge">
            {isOwner ? 'Store Owner' : 'Customer'}
          </span>
        </div>

        <nav className="navbar-links">
          {isOwner ? (
            <>
              <Link to="/owner/dashboard" className="nav-link active">Dashboard</Link>
              <span className="nav-link disabled" title="Available in Increment 2">Inventory</span>
              <span className="nav-link disabled" title="Available in Increment 2">POS & Utang</span>
            </>
          ) : (
            <>
              <Link to="/customer/dashboard" className="nav-link active">My Balance Portal</Link>
              <span className="nav-link disabled" title="Available in Increment 3">Pay Online</span>
            </>
          )}
        </nav>

        <div className="navbar-user">
          <div className="user-info">
            <span className="user-name">{user?.fullName || user?.username}</span>
            <span className="user-phone">{user?.mobileNumber}</span>
          </div>
          <button onClick={handleLogout} className="btn-logout" title="Sign out of TindahanKo">
            Logout
          </button>
        </div>
      </div>
    </header>
  );
}
