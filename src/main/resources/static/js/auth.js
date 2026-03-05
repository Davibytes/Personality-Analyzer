let isLoginMode = true;

document.addEventListener('DOMContentLoaded', () => {
    const toggleToSignup = document.getElementById('toggleToSignup');
    const toggleToLogin = document.getElementById('toggleToLogin');
    const loginBox = document.getElementById('loginBox');
    const signupBox = document.getElementById('signupBox');
    const authForm = document.getElementById('authForm');
    const signupForm = document.getElementById('signupForm');

    // Toggle between login and signup
    if (toggleToSignup) {
        toggleToSignup.addEventListener('click', (e) => {
            e.preventDefault();
            isLoginMode = false;
            updateAuthUI();
        });
    }

    if (toggleToLogin) {
        toggleToLogin.addEventListener('click', (e) => {
            e.preventDefault();
            isLoginMode = true;
            updateAuthUI();
        });
    }

    // Form submissions
    if (authForm) {
        authForm.addEventListener('submit', handleLoginSubmit);
    }

    if (signupForm) {
        signupForm.addEventListener('submit', handleSignupSubmit);
    }

    function updateAuthUI() {
        if (isLoginMode) {
            loginBox.style.display = 'block';
            signupBox.style.display = 'none';
            window.scrollTo(0, 0);
        } else {
            loginBox.style.display = 'none';
            signupBox.style.display = 'block';
            window.scrollTo(0, 0);
        }
    }

    async function handleLoginSubmit(e) {
        e.preventDefault();

        const email = document.getElementById('email').value.trim();
        const password = document.getElementById('password').value;

        // Clear previous errors
        clearErrors(['emailError', 'passwordError']);

        // Validation
        if (!email) {
            showError('emailError', 'Email is required');
            return;
        }
        if (!password) {
            showError('passwordError', 'Password is required');
            return;
        }

        await login(email, password);
    }

    async function handleSignupSubmit(e) {
        e.preventDefault();

        const fullName = document.getElementById('fullName').value.trim();
        const email = document.getElementById('signupEmail').value.trim();
        const password = document.getElementById('signupPassword').value;
        const confirmPassword = document.getElementById('confirmPassword').value;
        const agreeTerms = document.getElementById('agreeTerms').checked;

        // Clear previous errors
        clearErrors(['fullNameError', 'signupEmailError', 'signupPasswordError', 'confirmPasswordError']);

        // Validation
        if (!fullName) {
            showError('fullNameError', 'Full name is required');
            return;
        }

        if (!email) {
            showError('signupEmailError', 'Email is required');
            return;
        }

        if (!isValidEmail(email)) {
            showError('signupEmailError', 'Please enter a valid email address');
            return;
        }

        if (!password) {
            showError('signupPasswordError', 'Password is required');
            return;
        }

        if (password.length < 8) {
            showError('signupPasswordError', 'Password must be at least 8 characters');
            return;
        }

        if (!/[A-Z]/.test(password)) {
            showError('signupPasswordError', 'Password must contain at least one uppercase letter');
            return;
        }

        if (!/\d/.test(password)) {
            showError('signupPasswordError', 'Password must contain at least one number');
            return;
        }

        if (password !== confirmPassword) {
            showError('confirmPasswordError', 'Passwords do not match');
            return;
        }

        if (!agreeTerms) {
            showAlert('Please agree to the terms and conditions', 'error', 'alertContainer2');
            return;
        }

        await signup(fullName, email, password, confirmPassword);
    }

    async function login(email, password) {
        const button = authForm.querySelector('.btn-submit');
        setButtonLoading(button, true);

        try {
            const response = await apiRequest('/auth/login', 'POST', {
                email,
                password,
            });

            authToken = response.data.token;
            localStorage.setItem('authToken', authToken);
            localStorage.setItem('userId', response.data.userId);
            localStorage.setItem('userName', response.data.fullName);

            showAlert('Login successful! Redirecting...', 'success', 'alertContainer');

            setTimeout(() => {
                window.location.href = 'dashboard.html';
            }, 1500);
        } catch (error) {
            showAlert(error.message || 'Login failed. Please try again.', 'error', 'alertContainer');
        } finally {
            setButtonLoading(button, false);
        }
    }

    async function signup(fullName, email, password, confirmPassword) {
        const button = signupForm.querySelector('.btn-submit');
        setButtonLoading(button, true);

        try {
            const response = await apiRequest('/auth/signup', 'POST', {
                fullName,
                email,
                password,
                confirmPassword,
            });

            authToken = response.data.token;
            localStorage.setItem('authToken', authToken);
            localStorage.setItem('userId', response.data.userId);
            localStorage.setItem('userName', response.data.fullName);

            showAlert('Account created! Redirecting to dashboard...', 'success', 'alertContainer2');

            setTimeout(() => {
                window.location.href = 'dashboard.html';
            }, 1500);
        } catch (error) {
            showAlert(error.message || 'Signup failed. Please try again.', 'error', 'alertContainer2');
        } finally {
            setButtonLoading(button, false);
        }
    }

    function isValidEmail(email) {
        return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
    }

    function showError(elementId, message) {
        const element = document.getElementById(elementId);
        if (element) {
            element.textContent = message;
            element.classList.add('show');
        }
    }

    function clearErrors(errorIds) {
        errorIds.forEach(id => {
            const element = document.getElementById(id);
            if (element) {
                element.textContent = '';
                element.classList.remove('show');
            }
        });
    }

    function setButtonLoading(button, isLoading) {
        const spinner = button.querySelector('.spinner');
        const text = button.querySelector('span:first-child');

        if (isLoading) {
            button.disabled = true;
            spinner.style.display = 'inline-block';
            text.style.opacity = '0.7';
        } else {
            button.disabled = false;
            spinner.style.display = 'none';
            text.style.opacity = '1';
        }
    }
});