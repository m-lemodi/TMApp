<template>
  <div class="app">
    <nav class="navbar">
      <div class="nav-brand">Task Management App</div>
      <div class="nav-links">
        <router-link to="/">Home</router-link>
        <router-link to="/tasks">Tasks</router-link>
        <router-link to="/login">Login</router-link>
      </div>
    </nav>
    <main>
      <router-view></router-view>
    </main>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
.app {
  font-family: Arial, sans-serif;
}

.navbar {
  background-color: #333;
  padding: 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.nav-brand {
  color: white;
  font-size: 1.5rem;
  font-weight: bold;
}

.nav-links {
  display: flex;
  gap: 1rem;
}

.nav-links a {
  color: white;
  text-decoration: none;
}

.nav-links a:hover {
  color: #ddd;
}

main {
  padding: 2rem;
}
</style>
