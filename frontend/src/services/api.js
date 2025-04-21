// services/api.js
import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080',
    headers: {
        'Content-Type': 'application/json'
    }
});

export const taskService = {
    getAllTasks(userId, sessionToken) {
        return api.get('/tasks', {
            headers: {
                'x-user-id': userId,
                'x-session-token': sessionToken
            }
        });
    },

    addTask(task, userId, sessionToken) {
        return api.post('/tasks', task, {
            headers: {
                'x-user-id': userId,
                'x-session-token': sessionToken
            }
        });
    },

    editTask(title, task, userId, sessionToken) {
        return api.put(`/tasks/${title}`, task, {
            headers: {
                'x-user-id': userId,
                'x-session-token': sessionToken
            }
        });
    },

    deleteTask(title, userId, sessionToken) {
        return api.delete(`/tasks/${title}`, {
            headers: {
                'x-user-id': userId,
                'x-session-token': sessionToken
            }
        });
    },

    completeTask(title, userId, sessionToken) {
        return api.put(`/tasks/${title}/complete`, null, {
            headers: {
                'x-user-id': userId,
                'x-session-token': sessionToken
            }
        });
    }
};

export const authService = {
    login(credentials) {
        return api.post('/users/login', null, {
            params: {
                email: credentials.email,
                password: credentials.password
            }
        });
    },

    register(user) {
        return api.post('/users/register', user);
    }
};

// Request interceptor
api.interceptors.request.use(config => {
    console.log(`Sending ${config.method.toUpperCase()} request to ${config.url}`);
    return config;
}, error => {
    console.error('Request error:', error);
    return Promise.reject(error);
});

// Response interceptor
api.interceptors.response.use(response => {
    console.log(`Received response from ${response.config.url} with status ${response.status}`);
    return response;
}, error => {
    console.error('Response error:', error);
    return Promise.reject(error);
});

export default api;