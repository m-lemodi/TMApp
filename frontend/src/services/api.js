import axios from 'axios';

/* eslint-disable */

const api = axios.create({
    baseURL: 'http://localhost:8080', // Spring Boot default port
    withCredentials: true
});

export const taskService = {
    getAllTasks(username, sessionToken) {
        return api.get(`/api/tasks/${username}`, {
            headers: {
                'Session-Token': sessionToken
            }
        });
    },

    addTask(task, username, sessionToken) {
        return api.post(`/api/tasks/${username}`, task, {
            headers: {
                'Session-Token': sessionToken
            }
        });
    },

    editTask(taskId, task, username, sessionToken) {
        return api.put(`/api/tasks/${username}/${taskId}`, task, {
            headers: {
                'Session-Token': sessionToken
            }
        });
    },

    deleteTask(taskId, username, sessionToken) {
        return api.delete(`/api/tasks/${username}/${taskId}`, {
            headers: {
                'Session-Token': sessionToken
            }
        });
    },

    completeTask(taskId, username, sessionToken) {
        return api.put(`/api/tasks/${username}/${taskId}/complete`, null, {
            headers: {
                'Session-Token': sessionToken
            }
        });
    }
};

export const authService = {
    login(credentials) {
        return api.post('/users/login', credentials);
    },

    register(user) {
        return api.post('/api/auth/register', user);
    }
};