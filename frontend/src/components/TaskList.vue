<template>
  <div class="task-list">
    <h2 class="title">My Tasks</h2>

    <!-- Add Task Button -->
    <div class="add-task-button">
      <button @click="showAddTaskModal = true">Add New Task</button>
    </div>

    <!--  Search bar  -->
    <div class="search-bar">
      <input
          type="text"
          v-model="searchQuery"
          placeholder="Search tasks..."
          @input="handleSearch"
      >
    </div>


    <!-- Filter form -->
    <div class="filters">
      <button
          :class="{ active: currentFilter === 'all' }"
          @click="currentFilter = 'all'"
      >
        All Tasks
      </button>
      <button
          :class="{ active: currentFilter === 'pending' }"
          @click="currentFilter = 'pending'"
      >
        Pending
      </button>
      <button
          :class="{ active: currentFilter === 'completed' }"
          @click="currentFilter = 'completed'"
      >
        Completed
      </button>
    </div>

    <!--   Tasks counter   -->
    <div class="tasks-summary">
      <div class="task-counter">
        <span class="task-count">{{ filteredTasks.length }}</span>
        <span class="task-count-text">tasks shown</span>
        <span class="task-count-divider">•</span>
        <span class="completed-count">{{ completedCount }}</span>
        <span class="completed-count-text">completed</span>
      </div>
    </div>


    <!-- Tasks List -->
    <div class="tasks">
      <div v-for="task in filteredTasks" :key="task.id" class="task-item">
        <div class="task-content">
          <label class="checkbox-container">
            <input
                type="checkbox"
                :checked="task.status === 'COMPLETED'"
                @change="toggleTaskStatus(task)"
            >
            <span class="checkmark"></span>
          </label>
          <div class="task-details">
            <h3>{{ task.title }}</h3>
            <p>{{ task.description }}</p>
            <p class="due-date" v-if="task.dueDate">Due: {{ formatDate(task.dueDate) }}</p>
          </div>
        </div>
        <div class="task-actions">
          <button class="edit-button" @click="editTask(task)">Edit</button>
          <button class="delete-button" @click="deleteTask(task)">Delete</button>
        </div>
      </div>
      <div v-if="filteredTasks.length === 0" class="no-tasks">
        No tasks found
      </div>
    </div>

    <AddTaskModal
        :show="showAddTaskModal"
        @close="showAddTaskModal = false"
        @submit="addTask"
    />
    <EditTaskModal
        :show="showEditTaskModal"
        :task="selectedTask"
        @close="showEditTaskModal = false"
        @submit="handleEditTask"
    />


  </div>
</template>

// components/TaskList.vue
<script>
import AddTaskModal from './AddTaskModal.vue'
import EditTaskModal from './EditTaskModal.vue'

import { taskService } from '@/services/api';

export default {
  name: 'TaskList',
  components: { AddTaskModal, EditTaskModal},
  data() {
    return {
      tasks: [],
      showAddTaskModal: false,
      showEditTaskModal: false,
      selectedTask: null,
      currentFilter: 'all',
      error: null,
      searchQuery: '',
      searchTimeout: null,
    };
  },
  computed: {
    filteredTasks() {
      switch (this.currentFilter) {
        case 'completed':
          return this.tasks.filter(task => task.status === 'COMPLETED')
        case 'pending':
          return this.tasks.filter(task => task.status === 'PENDING')
        default:
          return this.tasks
      }
    },
    completedCount() {
      return this.tasks.filter(task => task.status === 'COMPLETED').length
    }
  },

  created() {
    this.loadTasks();
  },

  methods: {
    formatDate(date) {
      return new Date(date).toLocaleDateString('fr-FR', )
    },

    async toggleTaskStatus(task) {
      try {
        const userId = localStorage.getItem('userId')
        const sessionToken = localStorage.getItem('sessionToken')

        await taskService.changeTaskStatus(task.title, userId, sessionToken)

        // Update the local task status
        task.status = task.status === 'COMPLETED' ? 'PENDING' : 'COMPLETED'

        // Push a notification to the user
        if (!("Notification" in window)) {
          alert('Please enable notifications to mark tasks as completed.')
          return;
        }
        if (Notification.permission === "granted") {
          const notification = new Notification("Task Completed", {
            body: `Task "${task.title}" is ${task.status.toLowerCase()}.`,
          });
          notification.onclick = () => {
            window.focus();
          };
        } else if (Notification.permission !== 'denied') {
          Notification.requestPermission();
        }
        
      } catch (error) {
        console.error('Error toggling task status:', error)
        alert('Failed to update task status. Please try again.')
      }

    },
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

    async addTask(taskData) {
      try {
        const userId = localStorage.getItem('userId');
        const sessionToken = localStorage.getItem('sessionToken');

        const task = {
          title: taskData.title,
          description: taskData.description,
          dueDate: taskData.dueDate,
          status: 'PENDING'
        }
        const response = await taskService.addTask(task, userId, sessionToken);

        this.tasks.push(response.data);
        this.showAddTaskModal = false;


        await this.loadTasks();

        if (Notification.permission === "granted") {
          new Notification("Task Added", {
            body: `New task "${taskData.title}" has been added successfully.`
          });
        } else if (Notification.permission !== "denied") {
          Notification.requestPermission().then(permission => {
            if (permission === "granted") {
              new Notification("Task Added", {
                body: `New task "${taskData.title}" has been added successfully.`
              });
            }
          });
        }

      } catch (error) {
        console.error('Error adding task:', error);
        this.error = error.response?.data || 'Error adding task';

      }
    },

    editTask(task) {
      this.selectedTask = task;
      this.showEditTaskModal = true;
    },
    async handleEditTask(taskData) {
      try {
        const userId = localStorage.getItem('userId');
        const sessionToken = localStorage.getItem('sessionToken');

        await taskService.editTask(
            this.selectedTask.title,
            taskData,
            userId,
            sessionToken
        );

        await this.loadTasks();
        this.showEditTaskModal = false;
        this.selectedTask = null;
      } catch (error) {
        console.error('Error editing task:', error);
        this.error = error.response?.data || 'Error editing task';
      }
    },



  async deleteTask(task) {
      if (confirm('Are you sure you want to delete this task?')) {

        try {
          const userId = localStorage.getItem('userId');
          const sessionToken = localStorage.getItem('sessionToken');

          await taskService.deleteTask(task.title, userId, sessionToken);
          await this.loadTasks();
        } catch (error) {
          console.error('Error deleting task:', error);
          this.error = error.response?.data || 'Error deleting task';
        }
      }
    },
    handleSearch() {
      // Debounce the search to avoid too many API calls
      if (this.searchTimeout) {
        clearTimeout(this.searchTimeout);
      }
      this.searchTimeout = setTimeout(() => {
        this.performSearch();
      }, 300);
    },

    async performSearch() {
      if (!this.searchQuery.trim()) {
        await this.loadTasks();
        return;
      }

      try {
        const userId = localStorage.getItem('userId');
        const sessionToken = localStorage.getItem('sessionToken');

        if (!userId || !sessionToken) {
          this.$router.push('/login');
          return;
        }

        const response = await taskService.searchTasks(
            this.searchQuery,
            userId,
            sessionToken
        );
        this.tasks = response.data;
      } catch (error) {
        console.error('Error searching tasks:', error);
        this.error = error.response?.data || 'Error searching tasks';
      }
    },

  }
};
</script>

<style scoped>

.task-content {
  display: flex;
  align-items: center;
  flex: 1;
}

.task-details {
  margin-left: 15px;
}

.task-details h3 {
  margin: 0 0 5px 0;
  color: #2c3e50;
}

.task-details p {
  margin: 0;
  color: #666;
}

.due-date {
  font-size: 0.9em;
  color: #666;
  margin-top: 5px;
}

/* Checkbox styling */
.checkbox-container {
  display: block;
  position: relative;
  padding-left: 35px;
  cursor: pointer;
  user-select: none;
}

.checkbox-container input {
  position: absolute;
  opacity: 0;
  cursor: pointer;
  height: 0;
  width: 0;
}

.checkmark {
  position: absolute;
  top: 0;
  left: 0;
  transform: translate(0%, -50%);
  height: 25px;
  width: 25px;
  background-color: #eee;
  border: 2px solid #ddd;
  border-radius: 4px;
}

.checkbox-container:hover input ~ .checkmark {
  background-color: #ccc;
}

.checkbox-container input:checked ~ .checkmark {
  background-color: #42b983;
  border-color: #42b983;
}

.checkmark:after {
  content: "";
  position: absolute;
  display: none;
}

.checkbox-container input:checked ~ .checkmark:after {
  display: block;
}

.checkbox-container .checkmark:after {
  left: 9px;
  top: 5px;
  width: 5px;
  height: 10px;
  border: solid white;
  border-width: 0 2px 2px 0;
  transform: rotate(45deg);
}

/* Delete button styling */
.delete-button {
  background-color: #dc3545;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.2s;
}

.delete-button:hover {
  background-color: #c82333;
}

.title {
  text-align: center;
  margin: 20px 0;
  font-size: 24px;
  font-weight: bold;
  color: #42b983;
}
.task-list {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}
.filters {
  margin: 20px 0;
  display: flex;
  gap: 10px;
  justify-content: center;
}

.filters button {
  padding: 8px 16px;
  border: 1px solid #42b983;
  background-color: white;
  color: #42b983;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.filters button.active {
  background-color: #42b983;
  color: white;
}


.add-task-button {
  margin-bottom: 20px;
  display: flex;
  justify-content: center;
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
  background-color: #42b983;
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

.search-bar {
  margin: 20px 0;
  display: flex;
  justify-content: center;
}

.search-bar input {
  width: 100%;
  max-width: 400px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
  transition: border-color 0.3s ease;
}

.search-bar input:focus {
  outline: none;
  border-color: #42b983;
  box-shadow: 0 0 5px rgba(66, 185, 131, 0.2);
}

.tasks-summary {
  margin: 20px 0;
  display: flex;
  justify-content: center;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
}

.task-counter {
  background-color: #f8f9fa;
  padding: 8px 16px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  gap: 6px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.task-count {
  font-weight: 600;
  color: #42b983;
  font-size: 1.1em;
}

.completed-count {
  font-weight: 600;
  color: #42b983;
  font-size: 1.1em;
}

.task-count-text,
.completed-count-text {
  color: #6c757d;
  font-size: 0.9em;
}

.task-count-divider {
  color: #dee2e6;
  margin: 0 4px;
}

</style>