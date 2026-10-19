import axios from 'axios'

const client = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' },
})

client.interceptors.response.use(
  (res) => res,
  (err) => {
    const msg =
      err.response?.data?.message ||
      err.response?.data?.error ||
      (err.code === 'ECONNABORTED' ? 'Request timed out — backend may be starting up' : null) ||
      (err.message === 'Network Error' ? 'Cannot connect to backend. Is the Spring Boot server running on port 8080?' : null) ||
      err.message
    return Promise.reject(new Error(msg))
  }
)

export default client
