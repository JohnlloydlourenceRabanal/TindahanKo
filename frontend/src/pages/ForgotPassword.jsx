import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authApi } from '../api/auth';

export function ForgotPassword() {
  const navigate = useNavigate();

  // Step 1: Request Token | Step 2: Reset Password | Step 3: Success
  const [step, setStep] = useState(1);

  // Form Fields
  const [usernameOrMobile, setUsernameOrMobile] = useState('');
  const [token, setToken] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  // UI State
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [successInfo, setSuccessInfo] = useState(null);

  const handleRequestToken = async (e) => {
    e.preventDefault();
    setError('');

    if (!usernameOrMobile.trim()) {
      setError('Please enter your registered username or Philippine mobile number.');
      return;
    }

    setLoading(true);
    try {
      const res = await authApi.forgotPassword(usernameOrMobile.trim());
      setSuccessInfo(res);
      setToken(res.resetToken || '');
      setStep(2);
    } catch (err) {
      setError(err.message || 'Failed to request password reset code.');
    } finally {
      setLoading(false);
    }
  };

  const handleResetPassword = async (e) => {
    e.preventDefault();
    setError('');

    if (!token.trim()) {
      setError('Please provide the reset verification code.');
      return;
    }

    if (newPassword.length < 8) {
      setError('New password must be at least 8 characters long.');
      return;
    }

    if (newPassword !== confirmPassword) {
      setError('New password and confirmation do not match.');
      return;
    }

    setLoading(true);
    try {
      await authApi.resetPassword({
        token: token.trim(),
        newPassword,
        confirmPassword,
      });
      setStep(3);
    } catch (err) {
      setError(err.message || 'Failed to reset password.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <div className="auth-header">
          <img
            src="/logo.png"
            alt="TindahanKo Logo"
            className="auth-logo-img"
            style={{ maxWidth: '210px', width: '100%', height: 'auto', maxHeight: '54px', objectFit: 'contain', margin: '0 auto 16px', display: 'block' }}
          />
          <h2>{step === 3 ? 'Password Reset Complete' : 'Reset Your Password'}</h2>
          <p className="auth-subtitle">
            {step === 1 && 'Enter your username or mobile number to receive a secure recovery code'}
            {step === 2 && 'Enter the verification code and choose your new secure password'}
            {step === 3 && 'Your account credentials have been securely updated with BCrypt hashing'}
          </p>
        </div>

        {error && (
          <div className="alert alert-error" role="alert">
            <span className="alert-icon">⚠️</span>
            <span>{error}</span>
          </div>
        )}

        {/* STEP 1: Request Code */}
        {step === 1 && (
          <form onSubmit={handleRequestToken} className="auth-form">
            <div className="form-group">
              <label htmlFor="usernameOrMobile">Username or Registered Mobile Number</label>
              <input
                id="usernameOrMobile"
                type="text"
                value={usernameOrMobile}
                onChange={(e) => setUsernameOrMobile(e.target.value)}
                placeholder="e.g. admin or 09171234567"
                required
                autoFocus
              />
              <span className="field-hint">Works with your TindahanKo username or Philippine phone number.</span>
            </div>

            <button type="submit" className="btn btn-primary btn-block" disabled={loading}>
              {loading ? 'Verifying Account...' : 'Generate Reset Code'}
            </button>

            <div className="auth-footer">
              Remembered your password?{' '}
              <Link to="/login" className="auth-link">
                Back to Sign In
              </Link>
            </div>
          </form>
        )}

        {/* STEP 2: Enter Token & Set Password */}
        {step === 2 && (
          <form onSubmit={handleResetPassword} className="auth-form">
            {successInfo?.resetToken && (
              <div className="token-banner">
                <span className="token-label">Verification Code Issued:</span>
                <span className="token-code">{successInfo.resetToken}</span>
                <span className="token-expiry">Valid for {successInfo.expiresInMinutes || 15} minutes • Single-use</span>
              </div>
            )}

            <div className="form-group">
              <label htmlFor="token">Verification Code *</label>
              <input
                id="token"
                type="text"
                value={token}
                onChange={(e) => setToken(e.target.value)}
                placeholder="e.g. TK-XXXXXXXX"
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="newPassword">New Password *</label>
              <div className="password-input-wrapper">
                <input
                  id="newPassword"
                  type={showPassword ? 'text' : 'password'}
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                  placeholder="Minimum 8 characters"
                  required
                  minLength={8}
                />
                <button
                  type="button"
                  className="btn-toggle-pw"
                  onClick={() => setShowPassword(!showPassword)}
                  title={showPassword ? 'Hide password' : 'Show password'}
                >
                  {showPassword ? '👁️' : '🙈'}
                </button>
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="confirmPassword">Confirm New Password *</label>
              <input
                id="confirmPassword"
                type={showPassword ? 'text' : 'password'}
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                placeholder="Re-enter new password"
                required
              />
              {confirmPassword && newPassword !== confirmPassword && (
                <span className="field-error">Passwords do not match</span>
              )}
            </div>

            <button
              type="submit"
              className="btn btn-primary btn-block"
              disabled={loading || (confirmPassword && newPassword !== confirmPassword)}
            >
              {loading ? 'Resetting Password...' : 'Confirm & Save Password'}
            </button>

            <div className="auth-footer">
              <button
                type="button"
                className="btn-link"
                onClick={() => { setStep(1); setError(''); }}
              >
                ← Request a different code
              </button>
            </div>
          </form>
        )}

        {/* STEP 3: Success Screen */}
        {step === 3 && (
          <div className="reset-success-box">
            <div className="success-icon-badge">✅</div>
            <h3>Password Reset Successfully!</h3>
            <p>
              Your new password is now active and hashed with BCrypt. You can proceed to sign in with your updated credentials.
            </p>
            <button
              onClick={() => navigate('/login')}
              className="btn btn-primary btn-block"
              style={{ marginTop: '20px' }}
            >
              Sign In Now
            </button>
          </div>
        )}
      </div>
    </div>
  );
}
