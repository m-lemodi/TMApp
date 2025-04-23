<template>
  <div id="app">
    <nav-bar :key="$route.fullPath + navbarKey"></nav-bar>
    <main class="main-content">
      <router-view></router-view>
    </main>
  </div>
</template>

<script>
import NavBar from '@/components/NavBar.vue'

export default {
  name: 'App',
  components: {
    NavBar
  },
  data() {
    return {
      navbarKey: 0,
    }
  },
  watch: {
    // Watch auth status globally
    '$route'() {
      if (this.isLoggedIn) {
        this.navbarKey++; // Force navbar remount on route change when authenticated
      }
    }
  }

}
</script>

<style>
:root {
  --navbar-height: 64px; /* Define navbar height as a CSS variable */
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  padding-top: var(--navbar-height); /* Add padding equal to navbar height */
  flex: 1;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding-left: 20px;
  padding-right: 20px;
  box-sizing: border-box;
}

/* Reset default margins and padding */
body {
  margin: 0;
  padding: 0;
}
</style>