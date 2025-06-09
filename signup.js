console.log("Script loaded!");

import { auth, createUserWithEmailAndPassword, sendEmailVerification, doc, setDoc, db } from "./firebaseInitializing.js?v=1.0.4";

const nameElement = document.getElementById("name");
const emailElement = document.getElementById("email");
const passElement = document.getElementById("pass");
const passRElement = document.getElementById("passRepeat");
let errorElement = document.getElementById("errorDisplay");

nameElement.addEventListener("input", changeButton);
emailElement.addEventListener("input", changeButton);
passElement.addEventListener("input", changeButton);
passRElement.addEventListener("input", () => {
    changeButton();
    if (passElement.value !== passRElement.value || passElement.length<6) {
        passRElement.style.borderColor = "red";
    } else {
        passRElement.style.borderColor = "green";
    }
});

const signButton = document.getElementById("signupButton");

function changeButton() {
    const name = nameElement.value.trim();
    const email = emailElement.value.trim();
    const password = passElement.value;
    const passwordRepeat = passRElement.value;
    signButton.disabled = !(name && email && password && passwordRepeat && password === passwordRepeat && password.length >= 6);
}

signButton.addEventListener("click", async () => {

    const name = nameElement.value.trim();
    const email = emailElement.value.trim();
    const password = passElement.value;
    const passwordRepeat = passRElement.value;
  
    if (password === passwordRepeat && password.length>=6) {
        try {
            //creating the user
            const userCredential = await createUserWithEmailAndPassword(auth, email, password);
            const user = userCredential.user;

            await setDoc(doc(db, "users", user.uid), {
                name: name,
                email: email,
                quizAnswers: [],
              });
        
            //send verification email
            await sendEmailVerification(user, {
              url: 'https://yourstylist.stereopi.com/LogIn.html', //redirect to verified after verification
            });

            alert('Verification email sent! Check your inbox (and spam folder!).');
            window.location.href = '/LogIn.html';
          } catch (error) {

            switch(error.code) {
              case 'auth/email-already-in-use':
                errorElement.textContent = 'Email already registered. Try logging in.';
                break;
              case 'auth/invalid-email':
                errorElement.textContent = 'Invalid email format.';
                break;
              case 'auth/weak-password':
                errorElement.textContent = 'Password must be at least 6 characters.';
                break;
              default:
                errorElement.textContent = error.message;
            }
        }
    } else {
        alert("Please, enter the same password (6 characters minimum) in the second field.");
    }

}); 