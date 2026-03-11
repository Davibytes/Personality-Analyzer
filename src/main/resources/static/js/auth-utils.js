/**
 * Authentication Utilities for Frontend
 * Use these functions to manage user authentication state and protect pages
 */

/**
 * Check if user is currently logged in
 * @returns {boolean} true if user is logged in, false otherwise
 */
function isUserLoggedIn() {
    const userId = localStorage.getItem('userId');
    const authToken = localStorage.getItem('authToken');
    return userId && authToken;
}

/**
 * Get current logged-in user information
 * @returns {Object} User object with userId, fullName, email, or null if not logged in
 */
function getCurrentUser() {
    if (!isUserLoggedIn()) {
        return null;
    }

    return {
        userId: localStorage.getItem('userId'),
        fullName: localStorage.getItem('userFullName'),
        email: localStorage.getItem('userEmail'),
        authToken: localStorage.getItem('authToken')
    };
}

/**
 * Protect a page - redirect to login if user is not authenticated
 * Call this function at the beginning of any protected page's script
 * @param {string} loginPageUrl - URL to redirect to if not logged in (default: '/login')
 */
function requireLogin(loginPageUrl = '/login') {
    if (!isUserLoggedIn()) {
        console.warn('User not authenticated. Redirecting to login...');
        window.location.href = loginPageUrl;
    }
}

/**
 * Logout user and redirect to login page
 * @param {string} loginPageUrl - URL to redirect to after logout (default: '/login')
 */
function logout(loginPageUrl = '/login') {
    // Clear all authentication data from localStorage
    localStorage.removeItem('userId');
    localStorage.removeItem('userFullName');
    localStorage.removeItem('userEmail');
    localStorage.removeItem('authToken');
    localStorage.removeItem('rememberEmail');

    console.log('User logged out successfully');
    
    // Redirect to login page
    window.location.href = loginPageUrl;
}

/**
 * Display user name in the page (useful for greeting)
 * @param {string} elementId - ID of element to display user name in
 */
function displayUserName(elementId) {
    const element = document.getElementById(elementId);
    if (element) {
        const user = getCurrentUser();
        if (user) {
            element.textContent = user.fullName;
        }
    }
}

/**
 * Display user email in the page
 * @param {string} elementId - ID of element to display user email in
 */
function displayUserEmail(elementId) {
    const element = document.getElementById(elementId);
    if (element) {
        const user = getCurrentUser();
        if (user) {
            element.textContent = user.email;
        }
    }
}

/**
 * Make authenticated API request with auth token
 * @param {string} url - API endpoint URL
 * @param {Object} options - Fetch options (method, body, etc.)
 * @returns {Promise} Response promise
 */
async function authenticatedFetch(url, options = {}) {
    const user = getCurrentUser();
    
    if (!user) {
        throw new Error('User not authenticated');
    }

    // Add auth token to headers
    const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${user.authToken}`,
        ...options.headers
    };

    return fetch(url, {
        ...options,
        headers: headers
    });
}

/**
 * Setup logout button functionality
 * @param {string} logoutButtonId - ID of logout button element
 * @param {string} loginPageUrl - URL to redirect to after logout
 */
function setupLogoutButton(logoutButtonId, loginPageUrl = '/login') {
    const logoutButton = document.getElementById(logoutButtonId);
    if (logoutButton) {
        logoutButton.addEventListener('click', (e) => {
            e.preventDefault();
            logout(loginPageUrl);
        });
    }
}

/**
 * Initialize protected page
 * Call this at the beginning of protected pages (like dashboard.html)
 */
function initProtectedPage() {
    // Check if user is logged in
    requireLogin();
    
    // Display user information if elements exist
    displayUserName('userName');
    displayUserEmail('userEmail');
    
    // Setup logout functionality
    setupLogoutButton('logoutBtn');
}

