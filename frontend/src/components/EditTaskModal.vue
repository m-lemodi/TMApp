<template>
  <div v-if="show" class="modal-overlay" @click.self="closeModal">
    <div class="modal">
      <h3>Edit Task</h3>
      <form @submit.prevent="submitTask">
        <div class="form-group">
          <label for="title">Title</label>
          <input 
            id="title"
            v-model="taskData.title"
            type="text"
            required
            placeholder="Enter task title"
            @input="validateTitle"
          >
          <span class="error-message" v-if="titleError">
            {{ titleError }}
          </span>
        </div>

        <div class="form-group">
          <label for="description">Description</label>
          <textarea
            id="description"
            v-model="taskData.description"
            rows="3"
            placeholder="Enter task description"
          ></textarea>
        </div>

        <div class="form-group">
          <label for="dueDate">Due Date</label>
          <input
            id="dueDate"
            v-model="taskData.dueDate"
            type="date"
            :min="today"
          >
        </div>

        <div class="modal-buttons">
          <button type="button" class="cancel-button" @click="closeModal">Cancel</button>
          <button type="submit" class="submit-button" :disabled="!!titleError">Save Changes</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
export default {
  name: 'EditTaskModal',
  props: {
    show: {
      type: Boolean,
      required: true
    },
    task: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      taskData: {
        title: '',
        description: '',
        dueDate: '',
      },
      titleError: ''
    }
  },
  computed: {
    today() {
      return new Date().toISOString().split('T')[0]
    }
  },
  watch: {
    show(newValue) {
      if (newValue && this.task) {
        this.taskData = {
          title: this.task.title,
          description: this.task.description || '',
          dueDate: this.task.dueDate || '',
        }
      }
    }
  },
  methods: {

    submitTask() {

      const validationPattern = /^[a-zA-Z0-9\s]+$/;

      if (!validationPattern.test(this.taskData.title)) {
        alert('Title cannot contain special characters');
      } else if (this.taskData.title.trim().length === 0) {
        alert('Title cannot be empty');
      } else {
        this.$emit('submit', {
          ...this.taskData,
          title: this.taskData.title.trim()
        });
        this.closeModal();
      }
    },
    closeModal() {
      this.$emit('close');
    }
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal {
  background-color: white;
  padding: 25px;
  border-radius: 8px;
  width: 400px;
  max-width: 90%;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
  color: #2c3e50;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.error-message {
  color: #dc3545;
  font-size: 0.875rem;
  margin-top: 5px;
  display: block;
}

.modal-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.cancel-button,
.submit-button {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
}

.cancel-button {
  background-color: #6c757d;
  color: white;
}

.submit-button {
  background-color: #42b983;
  color: white;
}

.cancel-button:hover {
  background-color: #5a6268;
}

.submit-button:hover {
  background-color: #3aa876;
}

.submit-button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}
</style>