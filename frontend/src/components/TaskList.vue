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

// components/TaskList.vue
<script>
import { taskService } from '@/services/api';

export default {
  name: 'TaskList',
  data() {
    return {
      tasks: [],
      newTask: {
        title: '',
        description: '',
        dueDate: new Date().toISOString().split('T')[0] // Today's date as default
      },
      error: null
    };
  },

  created() {
    this.loadTasks();
  },

  methods: {
    async loadTasks() {
      try {
        const userId = localStorage.getItem('userId');
        const sessionToken = localStorage.getItem('sessionToken');

        if (!userId || !sessionToken) {
          this.$router.push('/login');
          return;
        }

        const response = await taskService.getAllTasks(userId, sessionToken);
        this.tasks = response.data;
      } catch (error) {
        console.error('Error loading tasks:', error);
        this.error = error.response?.data || 'Error loading tasks';

        if (error.response?.status === 401) {
          this.$router.push('/login');
        }
      }
    },

    async addTask() {
      try {
        const userId = localStorage.getItem('userId');
        const sessionToken = localStorage.getItem('sessionToken');

        await taskService.addTask(this.newTask, userId, sessionToken);

        // Reset form
        this.newTask = {
          title: '',
          description: '',
          dueDate: new Date().toISOString().split('T')[0]
        };

        await this.loadTasks();
      } catch (error) {
        console.error('Error adding task:', error);
        this.error = error.response?.data || 'Error adding task';
      }
    },

    async completeTask(title) {
      try {
        const userId = localStorage.getItem('userId');
        const sessionToken = localStorage.getItem('sessionToken');

        await taskService.completeTask(title, userId, sessionToken);
        await this.loadTasks();
      } catch (error) {
        console.error('Error completing task:', error);
        this.error = error.response?.data || 'Error completing task';
      }
    },

    async deleteTask(title) {
      try {
        const userId = localStorage.getItem('userId');
        const sessionToken = localStorage.getItem('sessionToken');

        await taskService.deleteTask(title, userId, sessionToken);
        await this.loadTasks();
      } catch (error) {
        console.error('Error deleting task:', error);
        this.error = error.response?.data || 'Error deleting task';
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