import { auth, onAuthStateChanged, signOut } from "./firebaseInitializing.js?v=1.0.4";

let loginButton;

//check auth state
export function initAuthListener() {
    try {
        onAuthStateChanged(auth, (user) => {
            if (user) {
                console.log("Logged in user:", user.uid);
                updateUIForLoggedInUser(user);
            } else {
                console.log("No user logged in");
                updateUIForLoggedOutUser();
            }
        });
    } catch (error) {
        updateUIForLoggedOutUser();
    }
}

export function updateUIForLoggedInUser(user) {
    loginButton = document.getElementById("loginButton");
    if (loginButton) {
        loginButton.textContent = "Log Out";
        loginButton.onclick = async () => {
            try {
                await signOut(auth);
                window.location.href = '/LogIn.html';
            } catch (error) {
                console.error("Sign out error:", error);
            }
        };
    }
}
  
export function updateUIForLoggedOutUser() {
    loginButton = document.getElementById("loginButton");
    if (loginButton) {
        loginButton.textContent = "Log In";
        loginButton.onclick = () => window.location.href = "/LogIn.html";
    }
    alert("You are not logged in. Redirecting to login screen");
    window.location.href = './LogIn.html';
}
