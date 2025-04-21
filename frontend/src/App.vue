<template>
  <nav>
    <router-link to="/">Home</router-link> |
    <router-link to="/tasks">Tasks</router-link> |
    <template v-if="!isLoggedIn">
      <router-link to="/login">Login</router-link> |
      <router-link to="/register">Register</router-link>
    </template>
    <template v-else>
      <span class="username">Welcome, {{ username }}!</span> |
      <a href="#" @click.prevent="handleLogout">Logout</a>
    </template>
  </nav>
  <router-view @login-success="handleLoginSuccess"/>
</template>

<script>
export default {
  name: 'App',
  data() {
    return {
      isLoggedIn: false,
      username: ''
    }
  },
  created() {
    // Check if user is already logged in when app starts
    const sessionToken = localStorage.getItem('sessionToken')
    const username = localStorage.getItem('username')
    if (sessionToken && username) {
      this.isLoggedIn = true
      this.username = username
    }
  },
  methods: {
    handleLoginSuccess(response) {
      this.isLoggedIn = true
      this.username = response.username
      localStorage.setItem('username', response.username)
      localStorage.setItem('sessionToken', response.sessionToken)
    },
    handleLogout() {
      this.isLoggedIn = false
      this.username = ''
      localStorage.removeItem('sessionToken')
      localStorage.removeItem('username')
      // Redirect to login page
      this.$router.push('/login')
    }
  }
}
</script>

<style>
nav {
  padding: 30px;
}

nav a {
  font-weight: bold;
  color: #2c3e50;
  text-decoration: none;
}

nav a.router-link-exact-active {
  color: #42b983;
}

.username {
  font-weight: bold;
  color: #42b983;
}
</style>