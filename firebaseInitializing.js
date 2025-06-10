import { initializeApp } from "https://www.gstatic.com/firebasejs/11.8.1/firebase-app.js";
import { 
  getAuth, 
  createUserWithEmailAndPassword, 
  signInWithEmailAndPassword,
  sendEmailVerification,
  onAuthStateChanged,
  signOut,
  applyActionCode
} from "https://www.gstatic.com/firebasejs/11.8.1/firebase-auth.js";
import { getFirestore, doc, setDoc, collection, addDoc, query, where, getDocs, updateDoc, getDoc, arrayUnion } from "https://www.gstatic.com/firebasejs/11.8.1/firebase-firestore.js";

const firebaseConfig = {
    apiKey: "AIzaSyBgcaBwACCzLYkS3hbYwrrd3h8DQd0xPWY",
    authDomain: "lovegame-a32b6.firebaseapp.com",
    projectId: "lovegame-a32b6",
    storageBucket: "lovegame-a32b6.firebasestorage.app",
    messagingSenderId: "179236464018",
    appId: "1:179236464018:web:1a657b7962611816294d4b",
    measurementId: "G-G2EFKT1GLH"
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
    getDoc,
    arrayUnion };