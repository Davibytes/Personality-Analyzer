let isLoginMode = true;

document.addEventListener('DOMContentLoaded', () => {
    const toggleToSignup = document.getElementById('toggleToSignup');
    const toggleToLogin = document.getElementById('toggleToLogin');
    const loginBox = document.getElementById('loginBox');
    const signupBox = document.getElementById('signupBox');
    const authForm = document.getElementById('authForm');
    const signupForm = document.getElementById('signupForm');

    // Toggle between login and signup modes
    if (toggleToSignup) {
        toggleToSignup.addEventListener('click', (e) => {
            e.preventDefault();
            isLoginMode = false;
            loginBox.style.display = 'none';
            signupBox.style.display = 'block';
        });
    }

    if (toggleToLogin) {
        toggleToLogin.addEventListener('click', (e) => {
            e.preventDefault();
            isLoginMode = true;
            loginBox.style.display = 'block';
            signupBox.style.display = 'none';
        });
    }

    // Handle login form submission
    if (authForm) {
        authForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const email = document.getElementById('email').value.trim();
            const password = document.getElementById('password').value;
            const rememberMe = document.getElementById('rememberMe').checked;

            // Validate inputs
            if (!email || !password) {
                showAlert('alertContainer', 'Please fill in all fields', 'error');
                return;
            }

            // Show loading state
            const submitBtn = authForm.querySelector('.btn-submit');
            const originalText = submitBtn.textContent;
            submitBtn.disabled = true;
            submitBtn.textContent = 'Signing in...';

            try {
                // Make API request to backend
                const response = await fetch('/api/auth/login', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        email: email,
                        password: password
                    })
                });

                const data = await response.json();

                if (data.success) {
                    // Store user data
                    localStorage.setItem('userId', data.userId);
                    localStorage.setItem('userFullName', data.fullName);
                    localStorage.setItem('userEmail', data.email);
                    localStorage.setItem('authToken', 'bearer_' + data.userId);
                    
                    if (rememberMe) {
                        localStorage.setItem('rememberEmail', email);
                    }

                    showAlert('alertContainer', 'Login successful! Redirecting...', 'success');
                    
                    // Redirect to dashboard after brief delay
                    setTimeout(() => {
                        window.location.href = 'dashboard.html';
                    }, 1000);
                } else {
                    showAlert('alertContainer', data.message || 'Login failed', 'error');
                }
            } catch (error) {
                console.error('Login error:', error);
                showAlert('alertContainer', 'An error occurred. Please try again.', 'error');
            } finally {
                // Reset button state
                submitBtn.disabled = false;
                submitBtn.textContent = originalText;
            }
        });
    }

    // Handle signup form submission
    if (signupForm) {
        signupForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const fullName = document.getElementById('fullName').value.trim();
            const email = document.getElementById('signupEmail').value.trim();
            const password = document.getElementById('signupPassword').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            const agreeTerms = document.getElementById('agreeTerms').checked;

            // Validate inputs
            if (!fullName || !email || !password || !confirmPassword) {
                showAlert('alertContainer2', 'Please fill in all fields', 'error');
                return;
            }

            if (!agreeTerms) {
                showAlert('alertContainer2', 'Please agree to Terms of Service', 'error');
                return;
            }

            if (password !== confirmPassword) {
                showAlert('alertContainer2', 'Passwords do not match', 'error');
                return;
            }

            if (password.length < 6) {
                showAlert('alertContainer2', 'Password must be at least 6 characters', 'error');
                return;
            }

            // Show loading state
            const submitBtn = signupForm.querySelector('.btn-submit');
            const originalText = submitBtn.textContent;
            submitBtn.disabled = true;
            submitBtn.textContent = 'Creating account...';

            try {
                // Make API request to backend
                const response = await fetch('/api/auth/register', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        fullName: fullName,
                        email: email,
                        password: password,
                        confirmPassword: confirmPassword
                    })
                });

                const data = await response.json();

                if (data.success) {
                    // Store user data
                    localStorage.setItem('userId', data.userId);
                    localStorage.setItem('userFullName', data.fullName);
                    localStorage.setItem('userEmail', data.email);
                    localStorage.setItem('authToken', 'bearer_' + data.userId);

                    showAlert('alertContainer2', 'Registration successful! Redirecting...', 'success');
                    
                    // Redirect to dashboard after brief delay
                    setTimeout(() => {
                        window.location.href = 'dashboard.html';
                    }, 1000);
                } else {
                    showAlert('alertContainer2', data.message || 'Registration failed', 'error');
                }
            } catch (error) {
                console.error('Registration error:', error);
                showAlert('alertContainer2', 'An error occurred. Please try again.', 'error');
            } finally {
                // Reset button state
                submitBtn.disabled = false;
                submitBtn.textContent = originalText;
            }
        });
    }
});

/**
 * Display alert message to user
 * @param {string} containerId - ID of alert container
 * @param {string} message - Alert message
 * @param {string} type - Alert type: 'success', 'error', 'warning'
 */
function showAlert(containerId, message, type) {
    const alertContainer = document.getElementById(containerId);
    if (!alertContainer) return;

    const alertClass = type === 'success' ? 'alert-success' : 
                       type === 'error' ? 'alert-danger' : 'alert-warning';
    
    const alertHTML = `
        <div class="alert ${alertClass} alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    `;

    alertContainer.innerHTML = alertHTML;

    // Auto-remove alert after 5 seconds
    setTimeout(() => {
        const alert = alertContainer.querySelector('.alert');
        if (alert) {
            alert.remove();
        }
    }, 5000);
}
