// LOGIN VALIDATION

const loginForm = document.getElementById("loginForm");

if(loginForm){

    loginForm.addEventListener("submit", function(e){

        e.preventDefault();

        let email = document.getElementById("loginEmail").value;
        let password = document.getElementById("loginPassword").value;
        let error = document.getElementById("loginError");

        if(email === "" || password === ""){
            error.textContent = "Please fill in all fields";
            return;
        }

        error.textContent = "";
        alert("Login request ready to send to backend");

    });

}


// REGISTER VALIDATION

const registerForm = document.getElementById("registerForm");

if(registerForm){

    registerForm.addEventListener("submit", function(e){

        e.preventDefault();

        let password = document.getElementById("password").value;
        let confirmPassword = document.getElementById("confirmPassword").value;
        let error = document.getElementById("registerError");

        if(password !== confirmPassword){
            error.textContent = "Passwords do not match";
            return;
        }

        error.textContent = "";
        alert("Registration request ready to send to backend");

    });

}