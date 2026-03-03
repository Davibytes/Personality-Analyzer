let currentTasks = [];

document.addEventListener('DOMContentLoaded', () => {
    if (!authToken) {
        window.location.href = 'login.html';
        return;
    }

    loadTasks();

    const createTaskBtn = document.getElementById('createTaskBtn');
    const taskModal = document.getElementById('taskModal');
    const closeModal = document.getElementById('closeModal');
    const taskForm = document.getElementById('taskForm');

    if (createTaskBtn) {
        createTaskBtn.addEventListener('click', () => {
            taskModal.style.display = 'block';
        });
    }

    if (closeModal) {
        closeModal.addEventListener('click', () => {
            taskModal.style.display = 'none';
        });
    }

    window.addEventListener('click', (e) => {
        if (e.target == taskModal) {
            taskModal.style.display = 'none';
        }
    });

    if (taskForm) {
        taskForm.addEventListener('submit', handleCreateTask);
    }
});

async function loadTasks() {
    try {
        const response = await apiRequest('/tasks', 'GET');
        currentTasks = response.data;

        const taskListEl = document.getElementById('taskList');
        if (taskListEl) {
            taskListEl.innerHTML = currentTasks.length > 0 ? currentTasks.map(task => `
                <div class="task-item">
                    <div class="task-title">${task.title}</div>
                    <p>${task.description || 'No description'}</p>
                    <div class="task-meta">
                        <span>📅 ${task.deadline}</span>
                        <span>⏱️ ${task.timeRemaining}</span>
                        <span>📊 DUP: ${task.dup.toFixed(1)}%</span>
                        <span>${task.status}</span>
                    </div>
                    <div class="task-actions">
                        ${task.status === 'pending' ? `<button class="btn btn-primary" onclick="startTask('${task.taskId}')">Start</button>` : ''}
                        ${task.status === 'in_progress' ? `<button class="btn btn-primary" onclick="completeTask('${task.taskId}')">Complete</button>` : ''}
                        <button class="btn btn-secondary" onclick="deleteTask('${task.taskId}')">Delete</button>
                    </div>
                </div>
            `).join('') : '<p>No tasks yet. Create one to get started!</p>';
        }
    } catch (error) {
        console.error('Failed to load tasks:', error);
    }
}

async function handleCreateTask(e) {
    e.preventDefault();

    const title = document.getElementById('taskTitle').value;
    const description = document.getElementById('taskDescription').value;
    const deadlineInput = document.getElementById('taskDeadline').value;

    if (!title || !deadlineInput) {
        showAlert('Please fill in all required fields', 'error');
        return;
    }

    try {
        const deadline = new Date(deadlineInput).getTime();
        await apiRequest('/tasks', 'POST', {
            title,
            description,
            deadline,
        });

        showAlert('Task created successfully!', 'success');
        document.getElementById('taskModal').style.display = 'none';
        document.getElementById('taskForm').reset();
        loadTasks();
    } catch (error) {
        console.error('Task creation failed:', error);
    }
}

async function startTask(taskId) {
    try {
        await apiRequest(`/tasks/${taskId}/start`, 'PUT');
        showAlert('Task started!', 'success');
        loadTasks();
    } catch (error) {
        console.error('Failed to start task:', error);
    }
}

async function completeTask(taskId) {
    try {
        await apiRequest(`/tasks/${taskId}/complete`, 'PUT');
        showAlert('Task completed!', 'success');
        loadTasks();
    } catch (error) {
        console.error('Failed to complete task:', error);
    }
}

async function deleteTask(taskId) {
    if (confirm('Are you sure you want to delete this task?')) {
        try {
            await apiRequest(`/tasks/${taskId}`, 'DELETE');
            showAlert('Task deleted!', 'success');
            loadTasks();
        } catch (error) {
            console.error('Failed to delete task:', error);
        }
    }
}