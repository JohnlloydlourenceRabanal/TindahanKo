import React, { useState, useEffect } from 'react';
import { Navbar } from '../components/Navbar';
import { useAuth } from '../context/AuthContext';
import { userApi } from '../api/user';

export function Profile() {
  const { user, token, updateUser } = useAuth();

  // Profile Form State
  const [fullName, setFullName] = useState(user?.fullName || '');
  const [mobileNumber, setMobileNumber] = useState(user?.mobileNumber || '');
  const [profileData, setProfileData] = useState(null);
  const [loadingProfile, setLoadingProfile] = useState(true);
  const [profileSuccess, setProfileSuccess] = useState('');
  const [profileError, setProfileError] = useState('');
  const [savingProfile, setSavingProfile] = useState(false);

  // Password Form State
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [showCurrentPw, setShowCurrentPw] = useState(false);
  const [showNewPw, setShowNewPw] = useState(false);
  const [passwordSuccess, setPasswordSuccess] = useState('');
  const [passwordError, setPasswordError] = useState('');
  const [savingPassword, setSavingPassword] = useState(false);

  // Active Tab: 'profile' | 'password'
  const [activeTab, setActiveTab] = useState('profile');

  useEffect(() => {
    async function fetchProfile() {
      if (!token) return;
      try {
        const data = await userApi.getProfile(token);
        setProfileData(data);
        setFullName(data.fullName || '');
        setMobileNumber(data.mobileNumber || '');
      } catch (err) {
        setProfileError(err.message || 'Failed to load profile.');
      } finally {
        setLoadingProfile(false);
      }
    }
    fetchProfile();
  }, [token]);

  const handleProfileSubmit = async (e) => {
    e.preventDefault();
    setProfileSuccess('');
    setProfileError('');

    const cleanName = fullName.trim();
    const cleanMobile = mobileNumber.trim();

    if (!cleanName || cleanName.length < 2) {
      setProfileError('Full name must be at least 2 characters.');
      return;
    }

    if (!cleanMobile.match(/^(09|\+639)\d{9}$/)) {
      setProfileError('Please enter a valid 11-digit Philippine mobile number (e.g., 09171234567).');
      return;
    }

    setSavingProfile(true);
    try {
      const updated = await userApi.updateProfile(token, {
        fullName: cleanName,
        mobileNumber: cleanMobile,
      });
      setProfileData(updated);
      updateUser({ fullName: updated.fullName, mobileNumber: updated.mobileNumber });
      setProfileSuccess('Profile information updated successfully.');
    } catch (err) {
      setProfileError(err.message || 'Failed to update profile.');
    } finally {
      setSavingProfile(false);
    }
  };

  const handlePasswordSubmit = async (e) => {
    e.preventDefault();
    setPasswordSuccess('');
    setPasswordError('');

    if (!currentPassword) {
      setPasswordError('Please provide your current password.');
      return;
    }

    if (newPassword.length < 8) {
      setPasswordError('New password must be at least 8 characters long.');
      return;
    }

    if (newPassword !== confirmPassword) {
      setPasswordError('New password and confirmation do not match.');
      return;
    }

    if (newPassword === currentPassword) {
      setPasswordError('New password cannot be the same as your current password.');
      return;
    }

    setSavingPassword(true);
    try {
      const res = await userApi.changePassword(token, {
        currentPassword,
        newPassword,
        confirmPassword,
      });
      setPasswordSuccess(res.message || 'Password changed successfully.');
      setCurrentPassword('');
      setNewPassword('');
      setConfirmPassword('');
    } catch (err) {
      setPasswordError(err.message || 'Failed to change password.');
    } finally {
      setSavingPassword(false);
    }
  };

  const isOwner = (profileData?.role || user?.role) === 'STORE_OWNER';

  return (
    <div className="dashboard-layout">
      <Navbar />

      <main className="dashboard-content">
        <div className="dashboard-header">
          <div className="dashboard-header-brand">
            <img
              src="/logo.png"
              alt="TindahanKo"
              className="dashboard-header-logo"
              style={{ height: '36px', width: 'auto', maxHeight: '36px', objectFit: 'contain', display: 'block' }}
            />
            <div>
              <h1>User Profile & Settings</h1>
              <p className="dashboard-subtext">
                Manage your personal details, Philippine contact number, and account security
              </p>
            </div>
          </div>
        </div>

        {/* Profile Card Container */}
        <div className="profile-container">
          {/* User Overview Summary Card */}
          <div className="profile-summary-card">
            <div className="avatar-circle">
              {(profileData?.fullName || user?.fullName || 'U').charAt(0).toUpperCase()}
            </div>
            <div className="profile-summary-details">
              <h2>{profileData?.fullName || user?.fullName}</h2>
              <div className="profile-badges-row">
                <span className={`brand-badge ${isOwner ? 'badge-owner' : 'badge-customer'}`}>
                  {isOwner ? 'Store Owner' : 'Store Customer'}
                </span>
                <span className="profile-username-tag">@{profileData?.username || user?.username}</span>
              </div>
              <p className="profile-member-date">
                Member since: {profileData?.createdAt ? new Date(profileData.createdAt).toLocaleDateString() : 'Active Member'}
              </p>
            </div>
          </div>

          {/* Tab Navigation */}
          <div className="profile-tabs">
            <button
              className={`profile-tab-btn ${activeTab === 'profile' ? 'active' : ''}`}
              onClick={() => { setActiveTab('profile'); setProfileSuccess(''); setProfileError(''); }}
            >
              👤 Profile Information
            </button>
            <button
              className={`profile-tab-btn ${activeTab === 'password' ? 'active' : ''}`}
              onClick={() => { setActiveTab('password'); setPasswordSuccess(''); setPasswordError(''); }}
            >
              🔒 Change Password
            </button>
          </div>

          {/* TAB 1: Profile Information */}
          {activeTab === 'profile' && (
            <div className="section-card profile-card-body">
              <h3>Edit Profile Information</h3>
              <p className="card-description">
                Keep your store contact information current. Your registered mobile number is used for critical utang alerts and SMS reminders.
              </p>

              {profileSuccess && (
                <div className="alert alert-success" role="alert">
                  <span>✅ {profileSuccess}</span>
                </div>
              )}

              {profileError && (
                <div className="alert alert-error" role="alert">
                  <span>⚠️ {profileError}</span>
                </div>
              )}

              <form onSubmit={handleProfileSubmit} className="profile-form">
                <div className="form-group">
                  <label htmlFor="username">Username</label>
                  <input
                    id="username"
                    type="text"
                    value={profileData?.username || user?.username || ''}
                    disabled
                    className="input-disabled"
                    title="Username is permanent and cannot be modified."
                  />
                  <span className="field-hint">Usernames are immutable for security and record integrity.</span>
                </div>

                <div className="form-group">
                  <label htmlFor="role">Account Role</label>
                  <input
                    id="role"
                    type="text"
                    value={isOwner ? 'Store Owner (Admin Privileges)' : 'Store Customer (Credit Debtor)'}
                    disabled
                    className="input-disabled"
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="fullName">Full Name *</label>
                  <input
                    id="fullName"
                    type="text"
                    value={fullName}
                    onChange={(e) => setFullName(e.target.value)}
                    placeholder="Enter your complete legal name"
                    required
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="mobileNumber">Philippine Mobile Number *</label>
                  <input
                    id="mobileNumber"
                    type="tel"
                    value={mobileNumber}
                    onChange={(e) => setMobileNumber(e.target.value)}
                    placeholder="09171234567"
                    required
                  />
                  <span className="field-hint">Format: 09171234567 or +639171234567 (11 digits)</span>
                </div>

                <div className="profile-actions">
                  <button type="submit" className="btn btn-primary" disabled={savingProfile}>
                    {savingProfile ? 'Saving Changes...' : 'Save Profile Changes'}
                  </button>
                </div>
              </form>
            </div>
          )}

          {/* TAB 2: Change Password */}
          {activeTab === 'password' && (
            <div className="section-card profile-card-body">
              <h3>Change Password</h3>
              <p className="card-description">
                Ensure your account is protected with a secure password of at least 8 characters.
              </p>

              {passwordSuccess && (
                <div className="alert alert-success" role="alert">
                  <span>✅ {passwordSuccess}</span>
                </div>
              )}

              {passwordError && (
                <div className="alert alert-error" role="alert">
                  <span>⚠️ {passwordError}</span>
                </div>
              )}

              <form onSubmit={handlePasswordSubmit} className="profile-form">
                <div className="form-group">
                  <label htmlFor="currentPassword">Current Password *</label>
                  <div className="password-input-wrapper">
                    <input
                      id="currentPassword"
                      type={showCurrentPw ? 'text' : 'password'}
                      value={currentPassword}
                      onChange={(e) => setCurrentPassword(e.target.value)}
                      placeholder="Enter current password"
                      required
                    />
                    <button
                      type="button"
                      className="btn-toggle-pw"
                      onClick={() => setShowCurrentPw(!showCurrentPw)}
                      title={showCurrentPw ? 'Hide password' : 'Show password'}
                    >
                      {showCurrentPw ? '👁️' : '🙈'}
                    </button>
                  </div>
                </div>

                <div className="form-group">
                  <label htmlFor="newPassword">New Password *</label>
                  <div className="password-input-wrapper">
                    <input
                      id="newPassword"
                      type={showNewPw ? 'text' : 'password'}
                      value={newPassword}
                      onChange={(e) => setNewPassword(e.target.value)}
                      placeholder="Minimum 8 characters"
                      required
                      minLength={8}
                    />
                    <button
                      type="button"
                      className="btn-toggle-pw"
                      onClick={() => setShowNewPw(!showNewPw)}
                      title={showNewPw ? 'Hide password' : 'Show password'}
                    >
                      {showNewPw ? '👁️' : '🙈'}
                    </button>
                  </div>
                  <span className="field-hint">Must be at least 8 characters and differ from your current password.</span>
                </div>

                <div className="form-group">
                  <label htmlFor="confirmPassword">Confirm New Password *</label>
                  <input
                    id="confirmPassword"
                    type={showNewPw ? 'text' : 'password'}
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    placeholder="Re-enter new password"
                    required
                  />
                  {confirmPassword && newPassword !== confirmPassword && (
                    <span className="field-error">Passwords do not match</span>
                  )}
                </div>

                <div className="profile-actions">
                  <button
                    type="submit"
                    className="btn btn-primary"
                    disabled={savingPassword || (confirmPassword && newPassword !== confirmPassword)}
                  >
                    {savingPassword ? 'Updating Password...' : 'Update Password'}
                  </button>
                </div>
              </form>
            </div>
          )}
        </div>
      </main>
    </div>
  );
}
