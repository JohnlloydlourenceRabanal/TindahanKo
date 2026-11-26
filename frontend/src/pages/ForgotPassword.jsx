import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authApi } from '../api/auth';

export function ForgotPassword() {
  const navigate = useNavigate();

  // Step 1: Identity Verification | Step 2: Enter Code & Reset Password | Step 3: Success
  const [step, setStep] = useState(1);

  // Identity Verification Form Fields
  const [username, setUsername] = useState('');
  const [mobileNumber, setMobileNumber] = useState('');
  const [fullName, setFullName] = useState('');

  // Password Reset Form Fields
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

    if (!username.trim() || !mobileNumber.trim() || !fullName.trim()) {
      setError('Please provide your Username, Registered Mobile Number, and Full Name for identity verification.');
      return;
    }

    const cleanMobile = mobileNumber.trim().replace(/[\s\-()]/g, '');
    const mobileRegex = /^(09|\+639)\d{9}$/;
    if (!mobileRegex.test(cleanMobile)) {
      setError('Mobile number must be a valid Philippine mobile number (e.g., 09171234567 or +639171234567).');
      return;
    }

    setLoading(true);
    try {
      const res = await authApi.forgotPassword({
        username: username.trim(),
        mobileNumber: cleanMobile,
        fullName: fullName.trim(),
      });
      setSuccessInfo(res);
      setToken(''); // Never prefill token; must be provided from received SMS/console
      setStep(2);
    } catch (err) {
      setError(err.message || 'Identity verification failed. Please verify your account details.');
    } finally {
      setLoading(false);
    }
  };

  const handleResetPassword = async (e) => {
    e.preventDefault();
    setError('');

    if (!token.trim()) {
      setError('Please enter the verification code sent to your registered mobile number.');
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
        token: token.trim().toUpperCase(),
        newPassword,
        confirmPassword,
      });
      setStep(3);
    } catch (err) {
      setError(err.message || 'Failed to reset password. Please check your verification code.');
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
            {step === 1 && 'Verify your account credentials to receive a secure recovery code via SMS'}
            {step === 2 && 'Enter the verification code dispatched to your phone to set a new password'}
            {step === 3 && 'Your account credentials have been securely updated with BCrypt hashing'}
          </p>
        </div>

        {error && (
          <div className="alert alert-error" role="alert">
            <span className="alert-icon">⚠️</span>
            <span>{error}</span>
          </div>
        )}

        {/* STEP 1: Secure Multi-Factor Identity Verification */}
        {step === 1 && (
          <form onSubmit={handleRequestToken} className="auth-form">
            <div className="form-group">
              <label htmlFor="username">Account Username *</label>
              <input
                id="username"
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="e.g. admin or customer_ana"
                required
                autoFocus
              />
            </div>

            <div className="form-group">
              <label htmlFor="mobileNumber">Registered Philippine Mobile Number *</label>
              <input
                id="mobileNumber"
                type="tel"
                value={mobileNumber}
                onChange={(e) => setMobileNumber(e.target.value)}
                placeholder="09171234567 or +639171234567"
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="fullName">Registered Full Name *</label>
              <input
                id="fullName"
                type="text"
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
                placeholder="e.g. John Lloyd Lourence Rabanal"
                required
              />
              <span className="field-hint">
                Multi-factor identity verification ensures only legitimate account owners can request password recovery.
              </span>
            </div>

            <button type="submit" className="btn btn-primary btn-block" disabled={loading}>
              {loading ? 'Verifying Identity...' : 'Verify Identity & Send Code'}
            </button>

            <div className="auth-footer">
              Remembered your password?{' '}
              <Link to="/login" className="auth-link">
                Back to Sign In
              </Link>
            </div>
          </form>
        )}

        {/* STEP 2: Enter Token & Set New Password */}
        {step === 2 && (
          <form onSubmit={handleResetPassword} className="auth-form">
            <div className="alert alert-success" style={{ marginBottom: '20px', alignItems: 'flex-start' }}>
              <span className="alert-icon">📱</span>
              <div>
                <strong>Identity Verified!</strong>
                <div style={{ marginTop: '2px', fontSize: '0.9rem' }}>
                  {successInfo?.liveSmsSent ? (
                    <>A real SMS verification code was sent via <strong>Semaphore API</strong> to <strong>{successInfo?.maskedMobile}</strong>. Please check your text messages.</>
                  ) : (
                    <>A verification code has been dispatched via SMS to <strong>{successInfo?.maskedMobile || 'your mobile number'}</strong>.</>
                  )}
                </div>
                <div style={{ fontSize: '0.8rem', marginTop: '4px', opacity: 0.9 }}>
                  Code is single-use and expires in {successInfo?.expiresInMinutes || 15} minutes.
                </div>
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="token">Verification Code *</label>
              <input
                id="token"
                type="text"
                value={token}
                onChange={(e) => setToken(e.target.value)}
                placeholder="Enter 8-character code (e.g. TK-XXXXXXXX)"
                required
                autoFocus
              />
              <span className="field-hint">Enter the code received on your registered mobile device.</span>
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

            <div style={{ fontSize: '0.78rem', color: '#475569', background: '#f1f5f9', border: '1px dashed #94a3b8', borderRadius: '6px', padding: '8px 12px', margin: '14px 0' }}>
              📡 <strong>Semaphore SMS Gateway:</strong> Configured for Philippine mobile networks. When <code>SEMAPHORE_API_KEY</code> is set in <code>backend/.env</code>, live SMS is dispatched to your cell phone. Otherwise, outgoing SMS is logged in the Spring Boot server console.
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
                onClick={() => { setStep(1); setError(''); setToken(''); }}
              >
                ← Back to Identity Verification
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
