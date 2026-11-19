import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export function Register() {
  const [formData, setFormData] = useState({
    fullName: '',
    username: '',
    mobileNumber: '',
    password: '',
    confirmPassword: '',
    role: 'CUSTOMER', // Defaulted securely to CUSTOMER role
  });

  const [fieldErrors, setFieldErrors] = useState({});
  const [generalError, setGeneralError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const { register } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    // Clear field error as user types
    if (fieldErrors[name]) {
      setFieldErrors((prev) => ({ ...prev, [name]: null }));
    }
  };

  const validate = () => {
    const errors = {};
    if (!formData.fullName.trim()) {
      errors.fullName = 'Full name is required';
    }

    if (!formData.username.trim()) {
      errors.username = 'Username is required';
    } else if (formData.username.trim().length < 3) {
      errors.username = 'Username must be at least 3 characters';
    }

    const phonePattern = /^(09|\+639)\d{9}$/;
    if (!formData.mobileNumber.trim()) {
      errors.mobileNumber = 'Philippine mobile number is required';
    } else if (!phonePattern.test(formData.mobileNumber.trim())) {
      errors.mobileNumber = 'Must be a valid 11-digit Philippine number (e.g. 09171234567)';
    }

    if (!formData.password) {
      errors.password = 'Password is required';
    } else if (formData.password.length < 6) {
      errors.password = 'Password must be at least 6 characters long';
    }

    if (formData.password !== formData.confirmPassword) {
      errors.confirmPassword = 'Passwords do not match';
    }

    setFieldErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setGeneralError('');

    if (!validate()) {
      return;
    }

    setSubmitting(true);
    try {
      // Force role to CUSTOMER for public self-registration
      const registrationPayload = {
        ...formData,
        role: 'CUSTOMER',
      };
      const user = await register(registrationPayload);
      navigate('/customer/dashboard', { replace: true });
    } catch (err) {
      if (err.data?.fieldErrors) {
        setFieldErrors(err.data.fieldErrors);
      }
      setGeneralError(err.message || 'Registration failed. Please check your information.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card auth-card-wide">
        <div className="auth-header">
          <img src="/logo.png" alt="TindahanKo Logo" className="auth-logo-img" />
          <h2>Create Customer Account</h2>
          <p className="auth-subtitle">Register to monitor your utang balance, purchases, and payments</p>
        </div>

        {generalError && (
          <div className="alert alert-error" role="alert">
            <span className="alert-icon">⚠️</span>
            <span>{generalError}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="auth-form" noValidate>
          <div className="form-row">
            <div className="form-group">
              <label htmlFor="fullName">Full Name</label>
              <input
                id="fullName"
                name="fullName"
                type="text"
                value={formData.fullName}
                onChange={handleChange}
                placeholder="e.g. Juan Dela Cruz"
                disabled={submitting}
                className={fieldErrors.fullName ? 'input-error' : ''}
              />
              {fieldErrors.fullName && <span className="field-error">{fieldErrors.fullName}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="username">Username</label>
              <input
                id="username"
                name="username"
                type="text"
                value={formData.username}
                onChange={handleChange}
                placeholder="e.g. juandelacruz"
                disabled={submitting}
                className={fieldErrors.username ? 'input-error' : ''}
              />
              {fieldErrors.username && <span className="field-error">{fieldErrors.username}</span>}
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="mobileNumber">Philippine Mobile Number</label>
            <input
              id="mobileNumber"
              name="mobileNumber"
              type="tel"
              value={formData.mobileNumber}
              onChange={handleChange}
              placeholder="09XXXXXXXXX"
              disabled={submitting}
              className={fieldErrors.mobileNumber ? 'input-error' : ''}
            />
            {fieldErrors.mobileNumber && <span className="field-error">{fieldErrors.mobileNumber}</span>}
          </div>

          <div className="form-row">
            <div className="form-group">
              <label htmlFor="password">Password</label>
              <input
                id="password"
                name="password"
                type="password"
                value={formData.password}
                onChange={handleChange}
                placeholder="At least 6 characters"
                disabled={submitting}
                className={fieldErrors.password ? 'input-error' : ''}
              />
              {fieldErrors.password && <span className="field-error">{fieldErrors.password}</span>}
            </div>

            <div className="form-group">
              <label htmlFor="confirmPassword">Confirm Password</label>
              <input
                id="confirmPassword"
                name="confirmPassword"
                type="password"
                value={formData.confirmPassword}
                onChange={handleChange}
                placeholder="Re-enter password"
                disabled={submitting}
                className={fieldErrors.confirmPassword ? 'input-error' : ''}
              />
              {fieldErrors.confirmPassword && <span className="field-error">{fieldErrors.confirmPassword}</span>}
            </div>
          </div>

          <button type="submit" className="btn btn-primary btn-block" disabled={submitting}>
            {submitting ? 'Registering Account...' : 'Create Account'}
          </button>
        </form>

        <div className="auth-footer">
          <p>
            Already have an account?{' '}
            <Link to="/login" className="auth-link">
              Sign in here
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
