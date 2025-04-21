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

// components/Login.vue
<script>
import { authService } from '@/services/api';

export default {
  name: 'Login',
  data() {
    return {
      credentials: {
        email: '',
        password: ''
      },
      error: null,
      loading: false
    };
  },

  methods: {
    async handleLogin() {
      this.loading = true;
      this.error = null;

      try {
        const response = await authService.login(this.credentials);

        // Store the session information
        localStorage.setItem('sessionToken', response.data.sessionToken);
        localStorage.setItem('userId', response.data.userId);

        // Redirect to tasks page
        this.$router.push('/tasks');
      } catch (error) {
        console.error('Login error:', error);
        this.error = error.response?.data?.message || 'Login failed';
      } finally {
        this.loading = false;
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