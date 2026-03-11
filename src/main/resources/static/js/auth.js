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

    if (authForm) {
        authForm.addEventListener('submit', (e) => {
            e.preventDefault();
            // Demo login
            localStorage.setItem('authToken', 'demo-token');
            localStorage.setItem('userId', 'demo-user');
            alert('Demo Login Successful!');
            window.location.href = 'dashboard.html';
        });
    }

    if (signupForm) {
        signupForm.addEventListener('submit', (e) => {
            e.preventDefault();
            // Demo signup
            localStorage.setItem('authToken', 'demo-token');
            localStorage.setItem('userId', 'demo-user');
            alert('Demo Signup Successful!');
            window.location.href = 'dashboard.html';
        });
    }
});