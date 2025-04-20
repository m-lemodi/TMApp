import { createApp } from 'vue';
import { createRouter, createWebHistory } from 'vue-router';
import App from './App.vue';
import TaskList from './components/TaskList.vue';

/* eslint-disable */

// Route guard to check authentication
const requireAuth = (to, from, next) => {
    const sessionToken = localStorage.getItem('sessionToken');
    if (!sessionToken) {
        next('/login');
    } else {
        next();
    }
};

// Router configuration
const router = createRouter({
    history: createWebHistory(),
    routes: [
        {
            path: '/',
            redirect: '/tasks'
        },
        {
            path: '/tasks',
            component: TaskList,
            beforeEnter: requireAuth
        },
        {
            path: '/login',
            component: () => import('./components/Login.vue')
        },
        {
            path: '/register',
            component: () => import('./components/Register.vue')
        },
        {
            // Catch all route for 404
            path: '/:pathMatch(.*)*',
            component: () => import('./components/NotFound.vue')
        }
    ]
});

// Create and configure Vue application
const app = createApp(App);

// Global error handler
app.config.errorHandler = (error, vm, info) => {
    console.error('Global error:', error);
    if (error.response && error.response.status === 401) {
        localStorage.removeItem('sessionToken');
        localStorage.removeItem('username');
        router.push('/login');
    }
};

// Add router to the app
app.use(router);

// Mount the app
app.mount('#app');