<template>
  <div class="task-list">
    <h2>My Tasks</h2>

    <!-- Add Task Form -->
    <form @submit.prevent="addTask" class="add-task-form">
      <input v-model="newTask.title" placeholder="Task title" required>
      <input v-model="newTask.description" placeholder="Task description">
      <button type="submit">Add Task</button>
    </form>

    <!-- Tasks List -->
    <div class="tasks">
      <div v-for="task in tasks" :key="task.id" class="task-item">
        <div class="task-content">
          <h3>{{ task.title }}</h3>
          <p>{{ task.description }}</p>
          <p>Status: {{ task.status }}</p>
        </div>
        <div class="task-actions">
          <button @click="completeTask(task.id)"
                  :disabled="task.status === 'COMPLETED'">
            Complete
          </button>
          <button @click="deleteTask(task.id)">Delete</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { taskService } from '@/services/api';

export default {
  name: 'TaskList',
  data() {
    return {
      tasks: [],
      newTask: {
        title: '',
        description: ''
      }
    };
  },
  created() {
    this.loadTasks();
  },
  methods: {
    async loadTasks() {
      try {
        const username = localStorage.getItem('username');
        const sessionToken = localStorage.getItem('sessionToken');
        const response = await taskService.getAllTasks(username, sessionToken);
        this.tasks = response.data;
      } catch (error) {
        this.error = error.response?.data?.message || 'Error loading tasks';
      }
    },
    async addTask() {
      try {
        const username = localStorage.getItem('username');
        const sessionToken = localStorage.getItem('sessionToken');
        await taskService.addTask(this.newTask, username, sessionToken);
        this.newTask = { title: '', description: '' };
        await this.loadTasks();
      } catch (error) {
        this.error = error.response?.data?.message || 'Error adding task';
      }
    },
    async completeTask(taskId) {
      try {
        const username = localStorage.getItem('username');
        const sessionToken = localStorage.getItem('sessionToken');
        await taskService.completeTask(taskId, username, sessionToken);
        await this.loadTasks();
      } catch (error) {
        this.error = error.response?.data?.message || 'Error completing task';
      }
    },
    async deleteTask(taskId) {
      try {
        const username = localStorage.getItem('username');
        const sessionToken = localStorage.getItem('sessionToken');
        await taskService.deleteTask(taskId, username, sessionToken);
        await this.loadTasks();
      } catch (error) {
        this.error = error.response?.data?.message || 'Error deleting task';
      }
    }
  }
};
</script>

<style scoped>
.task-list {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.add-task-form {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.task-item {
  border: 1px solid #ddd;
  margin-bottom: 10px;
  padding: 15px;
  border-radius: 4px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.task-actions {
  display: flex;
  gap: 10px;
}

button {
  padding: 5px 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  background-color: #4CAF50;
  color: white;
}

button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

input {
  padding: 5px;
  border: 1px solid #ddd;
  border-radius: 4px;
}
</style>