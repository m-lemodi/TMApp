<!-- src/components/NavBar.vue -->
<template>
  <nav class="navbar">
    <div class="navbar-container">
      <div class="navbar-brand">
        <router-link to="/" class="brand-link">Task Manager</router-link>
      </div>

      <div class="navbar-menu" v-if="isLoggedIn">
        <p class="username">Welcome <strong>{{ username }}</strong></p>
        <button @click="confirmLogout" class="nav-link logout-btn">Logout</button>
      </div>
      <div class="navbar-menu" v-else>
        <router-link to="/login" class="nav-link">Login</router-link>
        <router-link to="/register" class="nav-link register-btn">Register</router-link>

      </div>
    </div>
  </nav>
</template>

<script>
export default {
  name: 'NavBar',



  mounted() {
    const username = localStorage.getItem('username')
    const sessionToken = localStorage.getItem('sessionToken')
    this.username = username

    if (sessionToken && username) {
      this.isLoggedIn = true
    }

  },
  data () {
    return {
      isLoggedIn: false,
      username: '',
      navKey: 0,
    }
  },
  watch: {
    isLoggedIn(newValue) {
      if (newValue) {
        this.username = localStorage.getItem('username')
        this.navKey++
      }
    }
    },
  methods: {

    confirmLogout() {
      if (confirm('Are you sure you want to logout?')) {
        this.handleLogout()
      }
    },
    handleLogout() {
      this.isLoggedIn = false
      this.username = ''
      localStorage.removeItem('sessionToken')
      localStorage.removeItem('username')
      // Redirect to login page
      this.$router.push('/login')
    },

  }
}
</script>

<style scoped>
.navbar {
  background-color: #2c3e50;
  width: 100%;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1000;
  height: var(--navbar-height); /* Use the CSS variable */
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.navbar-container {
  height: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.navbar-brand {
  font-size: 1.5rem;
  font-weight: bold;
}

.brand-link {
  color: #ffffff;
  text-decoration: none;
  transition: color 0.3s ease;
}

.brand-link:hover {
  color: #3498db;
}

.navbar-menu {
  display: flex;
  gap: 1.5rem;
  align-items: center;
}

.nav-link {
  color: #ffffff;
  text-decoration: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.nav-link:hover {
  background-color: #34495e;
  color: #3498db;
}

.logout-btn {
  background: none;
  border: 1px solid #ffffff;
  cursor: pointer;
  font-size: 1rem;
}

.logout-btn:hover {
  background-color: #e74c3c;
  border-color: #e74c3c;
  color: #ffffff;
}

/* Responsive design */
@media (max-width: 768px) {
  :root {
    --navbar-height: 96px; /* Increase height for mobile layout */
  }
  
  .navbar-container {
    padding: 10px 20px;
  }

  .navbar-container {
    padding: 1rem;
    flex-direction: column;
    gap: 1rem;
  }

  .navbar-menu {
    width: 100%;
    justify-content: center;
    flex-wrap: wrap;
  }

  .nav-link {
    padding: 0.5rem;
  }
}

.username {
  color: #ffffff;
  font-size: 1rem;
}
</style>