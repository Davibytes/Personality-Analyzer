let isLoginMode = true;

document.addEventListener('DOMContentLoaded', () => {
    const toggleToSignup = document.getElementById('toggleToSignup');
    const toggleToLogin = document.getElementById('toggleToLogin');
    const loginBox = document.getElementById('loginBox');
    const signupBox = document.getElementById('signupBox');
    const authForm = document.getElementById('authForm');
    const signupForm = document.getElementById('signupForm');

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

    // Login Form
    if (authForm) {
        authForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            const email = document.getElementById('email').value.trim();
            const password = document.getElementById('password').value.trim();

            if (!email || !password) {
                showAlert('Please fill in all fields', 'error', 'alertContainer');
                return;
            }

            try {
                const response = await fetch('/api/auth/login', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({ email, password })
                });

                const data = await response.json();

                if (data.success) {
                    // Store user info (NO personality type yet)
                    localStorage.setItem('userId', data.userId);
                    localStorage.setItem('email', data.email);
                    localStorage.setItem('fullName', data.fullName);

                    showAlert('Login successful! Redirecting...', 'success', 'alertContainer');
                    setTimeout(() => {
                        window.location.href = 'dashboard.html';
                    }, 1500);
                } else {
                    showAlert(data.message || 'Login failed', 'error', 'alertContainer');
                }
            } catch (error) {
                console.error('Error:', error);
                showAlert('Login failed: ' + error.message, 'error', 'alertContainer');
            }
        });
    }

    // Signup Form
    if (signupForm) {
        signupForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            const fullName = document.getElementById('fullName').value.trim();
            const email = document.getElementById('signupEmail').value.trim();
            const password = document.getElementById('signupPassword').value.trim();
            const confirmPassword = document.getElementById('confirmPassword').value.trim();
            const agreeTerms = document.getElementById('agreeTerms').checked;

            // Validation
            if (!fullName || !email || !password || !confirmPassword) {
                showAlert('Please fill in all fields', 'error', 'alertContainer2');
                return;
            }

            if (password !== confirmPassword) {
                showAlert('Passwords do not match', 'error', 'alertContainer2');
                return;
            }

            if (password.length < 6) {
                showAlert('Password must be at least 6 characters', 'error', 'alertContainer2');
                return;
            }

            if (!agreeTerms) {
                showAlert('Please agree to the terms', 'error', 'alertContainer2');
                return;
            }

            try {
                const response = await fetch('/api/auth/register', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({ fullName, email, password })
                });

                const data = await response.json();

                if (data.success) {
                    showAlert('Registration successful! Loading onboarding...', 'success', 'alertContainer2');
                    setTimeout(() => {
                        // Store user info (NO personality type yet)
                        localStorage.setItem('userId', data.userId);
                        localStorage.setItem('email', data.email);
                        localStorage.setItem('fullName', data.fullName);
                        localStorage.setItem('isNewUser', 'true');

                        // Redirect to onboarding
                        window.location.href = 'onboarding.html';
                    }, 1500);
                } else {
                    showAlert(data.message || 'Registration failed', 'error', 'alertContainer2');
                }
            } catch (error) {
                console.error('Error:', error);
                showAlert('Registration failed: ' + error.message, 'error', 'alertContainer2');
            }
        });
    }
});

function showAlert(message, type, containerId) {
    const container = document.getElementById(containerId);
    if (!container) return;

    container.innerHTML = `
        <div class="alert alert-${type}">
            ${message}
        </div>
    `;

    // Auto-remove after 5 seconds
    setTimeout(() => {
        container.innerHTML = '';
    }, 5000);
}