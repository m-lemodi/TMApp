<template>
  <div class="not-found">
    <h1>404</h1>
    <h2>Page Not Found</h2>
    <p>The page you're looking for doesn't exist or has been moved.</p>
    <router-link to="/" class="home-link">Go to Home</router-link>
  </div>
</template>

<script>
export default {
  name: 'NotFound'
};
</script>

<style scoped>
.not-found {
  text-align: center;
  padding: 40px;
  margin-top: 60px;
}

h1 {
  font-size: 72px;
  color: #42b983;
  margin: 0;
}

h2 {
  margin-top: 20px;
  color: #2c3e50;
}

p {
  color: #666;
  margin: 20px 0;
}

.home-link {
  display: inline-block;
  padding: 10px 20px;
  background-color: #42b983;
  color: white;
  text-decoration: none;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.home-link:hover {
  background-color: #3aa876;
}
</style>