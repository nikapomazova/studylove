import { initializeApp } from "https://www.gstatic.com/firebasejs/11.6.0/firebase-app.js";
import { 
  getAuth, 
  createUserWithEmailAndPassword, 
  signInWithEmailAndPassword,
  sendEmailVerification,
  onAuthStateChanged,
  signOut,
  applyActionCode
} from "https://www.gstatic.com/firebasejs/11.6.0/firebase-auth.js";
import { getFirestore, doc, setDoc, collection, addDoc, query, where, getDocs, updateDoc, getDoc } from "https://www.gstatic.com/firebasejs/11.6.0/firebase-firestore.js";

const firebaseConfig = {
    apiKey: "AIzaSyCQ907RTSmRyw38xBoE9kOK7af4Ut4wIG0",
    authDomain: "yourstylist-450ba.firebaseapp.com",
    projectId: "yourstylist-450ba",
    storageBucket: "yourstylist-450ba.firebasestorage.app",
    messagingSenderId: "774264219494",
    appId: "1:774264219494:web:c207dfc2e053e87653caa7"
};

const app = initializeApp(firebaseConfig);
const auth = getAuth(app);
const db = getFirestore(app);

export { auth,
    applyActionCode,
    signOut,
    onAuthStateChanged,
    createUserWithEmailAndPassword,
    signInWithEmailAndPassword,
    sendEmailVerification,
    doc,
    setDoc,
    db,
    collection,
    addDoc,
    query,
    where,
    getDocs,
    updateDoc,
    getDoc };