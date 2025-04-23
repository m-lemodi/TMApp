<template>
  <div class="register-container">
    <h2>Register</h2>
    <form @submit.prevent="handleRegister" class="register-form">
      <div class="form-group">
        <label for="email">Email:</label>
        <input 
          type="email" 
          id="email" 
          v-model="user.email" 
          required
        >
      </div>
      <div class="form-group">
        <label for="username">Username:</label>
        <input 
          type="text" 
          id="username" 
          v-model="user.username" 
          required
        >
      </div>
      <div class="form-group">
        <label for="password">Password:</label>
        <input 
          type="password" 
          id="password" 
          v-model="user.password" 
          required
        >
      </div>
      <div class="form-group">
        <label for="password">Confirm Password:</label>
        <input
            type="password"
            id="confirmPassword"
            v-model="user.confirmPassword"
            required
        >
      </div>
      <div class="error" v-if="error">{{ error }}</div>
      <button type="submit" :disabled="isLoading">
        {{ isLoading ? 'Registering...' : 'Register' }}
      </button>
    </form>
    <p class="login-link">
      Already have an account? 
      <router-link to="/login">Login here</router-link>
    </p>
  </div>
</template>

<script>
import { authService } from '../services/api';

export default {
  name: 'Register',
  data() {
    return {
      user: {
        email: '',
        username: '',
        password: '',
        confirmPassword: ''
      },
      error: null,
      isLoading: false
    };
  },
  methods: {
    async handleRegister() {
      this.error = null;
      this.isLoading = true;
      
      try {
        await authService.register(this.user);
        this.$router.push('/login');
      } catch (error) {
        this.error = error.response?.data?.message || 'Registration failed';
      } finally {
        this.isLoading = false;
      }
    }
  }
};
</script>

<style scoped>
.register-container {
  max-width: 400px;
  margin: 40px auto;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.register-form {
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

.login-link {
  margin-top: 20px;
  text-align: center;
}

.login-link a {
  color: #42b983;
  text-decoration: none;
}
</style>