import React, { createContext, useContext, useState, useEffect } from 'react';
import { authApi } from '../api/auth';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const savedUser = localStorage.getItem('tindahanko_user');
    return savedUser ? JSON.parse(savedUser) : null;
  });
  const [token, setToken] = useState(() => localStorage.getItem('tindahanko_token'));
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Verify stored token on app boot
    async function verifyToken() {
      if (token) {
        try {
          const userData = await authApi.getCurrentUser(token);
          setUser(userData);
          localStorage.setItem('tindahanko_user', JSON.stringify(userData));
        } catch (err) {
          console.warn('Session expired or invalid token:', err);
          logout();
        }
      }
      setLoading(false);
    }
    verifyToken();
  }, [token]);

  const login = async (username, password) => {
    const data = await authApi.login({ username, password });
    setToken(data.token);
    const userInfo = {
      userId: data.userId,
      username: data.username,
      fullName: data.fullName,
      mobileNumber: data.mobileNumber,
      role: data.role,
    };
    setUser(userInfo);
    localStorage.setItem('tindahanko_token', data.token);
    localStorage.setItem('tindahanko_user', JSON.stringify(userInfo));
    return userInfo;
  };

  const register = async (formData) => {
    const data = await authApi.register(formData);
    setToken(data.token);
    const userInfo = {
      userId: data.userId,
      username: data.username,
      fullName: data.fullName,
      mobileNumber: data.mobileNumber,
      role: data.role,
    };
    setUser(userInfo);
    localStorage.setItem('tindahanko_token', data.token);
    localStorage.setItem('tindahanko_user', JSON.stringify(userInfo));
    return userInfo;
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('tindahanko_token');
    localStorage.removeItem('tindahanko_user');
  };

  return (
    <AuthContext.Provider value={{ user, token, loading, login, register, logout, isAuthenticated: !!token }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
