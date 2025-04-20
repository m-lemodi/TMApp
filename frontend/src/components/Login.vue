<template>
  <div class="login-container">
    <h2>Login</h2>
    <form @submit.prevent="handleLogin" class="login-form">
      <div class="form-group">
        <label for="email">Email:</label>
        <input 
          type="email" 
          id="email" 
          v-model="credentials.email" 
          required
        >
      </div>
      <div class="form-group">
        <label for="password">Password:</label>
        <input 
          type="password" 
          id="password" 
          v-model="credentials.password" 
          required
        >
      </div>
      <div class="error" v-if="error">{{ error }}</div>
      <button type="submit" :disabled="isLoading">
        {{ isLoading ? 'Logging in...' : 'Login' }}
      </button>
    </form>
    <p class="register-link">
      Don't have an account? 
      <router-link to="/register">Register here</router-link>
    </p>
  </div>
</template>

<script>
import { authService } from '../services/api';

export default {
  name: 'Login',
  data(email, password) {
    return {
      credentials: {
        email: email,
        password: password
      },
      error: null,
      isLoading: false
    };
  },
  methods: {
    async handleLogin() {
      this.error = null;
      this.isLoading = true;
      
      try {
        const response = await authService.login(this.credentials);
        localStorage.setItem('sessionToken', response.data.sessionToken);
        localStorage.setItem('username', response.data.username);
        this.$router.push('/tasks');
      } catch (error) {
        this.error = error.response?.data?.message || 'Login failed';
      } finally {
        this.isLoading = false;
      }
    }
  }
};
</script>

<style scoped>
.login-container {
  max-width: 400px;
  margin: 40px auto;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

input {
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

button {
  padding: 10px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:disabled {
  background-color: #a8d5c2;
  cursor: not-allowed;
}

.error {
  color: #dc3545;
  font-size: 14px;
}

.register-link {
  margin-top: 20px;
  text-align: center;
}

.register-link a {
  color: #42b983;
  text-decoration: none;
}
</style>