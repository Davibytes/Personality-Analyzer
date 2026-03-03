let isLoginMode = true;

document.addEventListener('DOMContentLoaded', () => {
    const authForm = document.getElementById('authForm');
    const toggleAuthLink = document.getElementById('toggleAuth');
    const fullNameGroup = document.getElementById('fullNameGroup');
    const confirmPasswordGroup = document.getElementById('confirmPasswordGroup');
    const authTitle = document.getElementById('authTitle');

    if (authForm) {
        authForm.addEventListener('submit', handleAuthSubmit);
    }

    if (toggleAuthLink) {
        toggleAuthLink.addEventListener('click', (e) => {
            e.preventDefault();
            isLoginMode = !isLoginMode;
            updateAuthUI();
        });
    }

    function updateAuthUI() {
        authTitle.textContent = isLoginMode ? 'Login' : 'Sign Up';
        fullNameGroup.style.display = isLoginMode ? 'none' : 'block';
        confirmPasswordGroup.style.display = isLoginMode ? 'none' : 'block';
        toggleAuthLink.textContent = isLoginMode ? 'Sign Up' : 'Login';
    }

    async function handleAuthSubmit(e) {
        e.preventDefault();

        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;

        if (isLoginMode) {
            await login(email, password);
        } else {
            const fullName = document.getElementById('fullName').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            await signup(fullName, email, password, confirmPassword);
        }
    }

    async function login(email, password) {
        try {
            const response = await apiRequest('/auth/login', 'POST', {
                email,
                password,
            });

            authToken = response.data.token;
            localStorage.setItem('authToken', authToken);
            localStorage.setItem('userId', response.data.userId);

            showAlert('Login successful!', 'success');
            setTimeout(() => {
                window.location.href = 'dashboard.html';
            }, 1000);
        } catch (error) {
            console.error('Login failed:', error);
        }
    }

    async function signup(fullName, email, password, confirmPassword) {
        if (password !== confirmPassword) {
            showAlert('Passwords do not match', 'error');
            return;
        }

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

            showAlert('Signup successful!', 'success');
            setTimeout(() => {
                window.location.href = 'dashboard.html';
            }, 1000);
        } catch (error) {
            console.error('Signup failed:', error);
        }
    }
});