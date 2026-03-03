// API Configuration
const API_BASE_URL = '/api';
let authToken = localStorage.getItem('authToken');

// Utility Functions
async function apiRequest(endpoint, method = 'GET', body = null) {
    const options = {
        method,
        headers: {
            'Content-Type': 'application/json',
        },
    };

    if (authToken) {
        options.headers['Authorization'] = `Bearer ${authToken}`;
    }

    if (body) {
        options.body = JSON.stringify(body);
    }

    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, options);
        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || 'API Error');
        }

        return data;
    } catch (error) {
        showAlert(error.message, 'error');
        throw error;
    }
}

function showAlert(message, type = 'info') {
    const alertContainer = document.getElementById('alertContainer');
    if (!alertContainer) return;

    const alert = document.createElement('div');
    alert.className = `alert alert-${type}`;
    alert.textContent = message;

    alertContainer.innerHTML = '';
    alertContainer.appendChild(alert);

    setTimeout(() => alert.remove(), 5000);
}

// Logout
document.addEventListener('DOMContentLoaded', () => {
    const logoutBtns = document.querySelectorAll('#logoutBtn');
    logoutBtns.forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            authToken = null;
            localStorage.removeItem('authToken');
            localStorage.removeItem('userId');
            window.location.href = 'index.html';
        });
    });

    // Check if user is on protected page
    const pathname = window.location.pathname;
    if ((pathname.includes('dashboard') || pathname.includes('tasks')) && !authToken) {
        window.location.href = 'login.html';
    }
});