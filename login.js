import {auth, signInWithEmailAndPassword} from "./firebaseInitializing.js?v=1.0.4";

const loginButton = document.getElementById("loginButton");

const email = document.getElementById("email");
const password = document.getElementById("pass");

email.addEventListener("input", changeButton);
password.addEventListener("input", changeButton);
var emailV = email.value.trim();
var passwordV = password.value.trim();

function changeButton() {
    emailV = email.value.trim();
    passwordV = password.value.trim();
    loginButton.disabled = !(emailV && passwordV);
}

document.getElementById("loginButton").addEventListener("click", async () => {
    const errorText = document.getElementById("errorDisplay");

    try {
        //logging the user in
        console.log("Email type: ", typeof emailV, ". Value: ", emailV);
        console.log("Password type: ", typeof passwordV, ". Value: ", passwordV);
        const userCredential = await signInWithEmailAndPassword(auth, emailV, passwordV);
        const user = userCredential.user;
    
        //checking if email is verified
        if (!user.emailVerified) {
          await auth.signOut(); //force logout if not verified
          errorText.textContent ='Please verify your email first. Check your inbox.';
        }
    
        //redirecting to main
        window.location.href = '/Main.html';
      } catch (error) {
        //handling specific errors
        switch(error.code) {
          case 'auth/user-not-found':
            errorText.textContent = 'Email not registered. Sign up first.';
            if (confirm("Go to sign up?")) {
                window.location.href = "/index.html";
            }
            break;
          case 'auth/wrong-password':
            errorText.textContent = 'Incorrect password.';
            break;
          default:
            errorText.textContent = error.message;
        }
      }
});