export const API_CONFIG = {
  // Use environment variable or fallback to localhost for development
  BFF_URL: import.meta.env.VITE_BFF_URL || 'http://localhost:8080', // Changed from 8081 to 8080
};

export const getApiUrl = (endpoint: string): string => {
  return `${API_CONFIG.BFF_URL}/api/bff${endpoint}`;
};