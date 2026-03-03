document.addEventListener('DOMContentLoaded', () => {
    if (!authToken) {
        window.location.href = 'login.html';
        return;
    }

    loadDashboard();
    setInterval(loadDashboard, 30000); // Refresh every 30 seconds
});

async function loadDashboard() {
    try {
        const response = await apiRequest('/dashboard', 'GET');
        const dashboard = response.data;

        document.getElementById('avgDup').textContent = dashboard.averageDup.toFixed(1) + '%';
        document.getElementById('completionRate').textContent = dashboard.completionRate.toFixed(1) + '%';
        document.getElementById('totalCompleted').textContent = dashboard.totalTasksCompleted;
        document.getElementById('lateSubmissions').textContent = dashboard.lateSubmissions;

        const personalityBadge = document.getElementById('personalityBadge');
        if (personalityBadge) {
            personalityBadge.textContent = dashboard.personalityType;
            const className = `personality-${dashboard.personalityType.toLowerCase().replace(/ /g, '-')}`;
            personalityBadge.className = `personality-badge ${className}`;
        }

        const recommendationEl = document.getElementById('recommendation');
        if (recommendationEl) {
            recommendationEl.textContent = dashboard.recommendation;
        }

        // Load tasks
        const taskListEl = document.getElementById('taskList');
        if (taskListEl && dashboard.recentTasks) {
            taskListEl.innerHTML = dashboard.recentTasks.map(task => `
                <div class="task-item">
                    <div class="task-title">${task.title}</div>
                    <p>${task.description || 'No description'}</p>
                    <div class="task-meta">
                        <span>📅 ${task.deadline}</span>
                        <span>⏱️ ${task.timeRemaining}</span>
                        <span>📊 DUP: ${task.dup.toFixed(1)}%</span>
                        <span>${task.status}</span>
                    </div>
                </div>
            `).join('');
        }
    } catch (error) {
        console.error('Failed to load dashboard:', error);
    }
}