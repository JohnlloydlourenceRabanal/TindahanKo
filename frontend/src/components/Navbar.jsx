import React from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const isOwner = user?.role === 'STORE_OWNER';
  const currentPath = location.pathname;

  return (
    <header className="navbar">
      <div className="navbar-container">
        <div className="navbar-brand">
          <Link to={isOwner ? '/owner/dashboard' : '/customer/dashboard'} className="brand-logo">
            <img
              src="/logo.png"
              alt="TindahanKo"
              className="navbar-logo-img"
              style={{ height: '32px', width: 'auto', maxHeight: '32px', objectFit: 'contain', display: 'block' }}
            />
          </Link>
          <span className="brand-badge">
            {isOwner ? 'Store Owner' : 'Customer'}
          </span>
        </div>

        <nav className="navbar-links">
          {isOwner ? (
            <>
              <Link
                to="/owner/dashboard"
                className={`nav-link ${currentPath === '/owner/dashboard' ? 'active' : ''}`}
              >
                Dashboard
              </Link>
              <Link
                to="/profile"
                className={`nav-link ${currentPath === '/profile' ? 'active' : ''}`}
              >
                Profile & Security
              </Link>
              <span className="nav-link disabled" title="Available in Increment 3">Inventory</span>
              <span className="nav-link disabled" title="Available in Increment 3">POS & Utang</span>
            </>
          ) : (
            <>
              <Link
                to="/customer/dashboard"
                className={`nav-link ${currentPath === '/customer/dashboard' ? 'active' : ''}`}
              >
                My Balance Portal
              </Link>
              <Link
                to="/profile"
                className={`nav-link ${currentPath === '/profile' ? 'active' : ''}`}
              >
                Profile & Security
              </Link>
              <span className="nav-link disabled" title="Available in Increment 3">Pay Online</span>
            </>
          )}
        </nav>

        <div className="navbar-user">
          <Link to="/profile" className="user-info user-info-link" title="Manage your profile & password">
            <span className="user-name">{user?.fullName || user?.username}</span>
            <span className="user-phone">{user?.mobileNumber}</span>
          </Link>
          <button onClick={handleLogout} className="btn-logout" title="Sign out of TindahanKo">
            Logout
          </button>
        </div>
      </div>
    </header>
  );
}
