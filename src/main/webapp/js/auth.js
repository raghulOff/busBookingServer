// const API_BASE = "/tryingAuth/api/user";

// async function handleRegister(e) {
//   e.preventDefault();
//   var username = document.getElementById('reg-input-id').value;
//   var password = document.getElementById('reg-input-password').value;
//   // console.log(username);
//   // console.log(password);
//   const res = await fetch(API_BASE + "/signup", {
//     method: "POST",
//     headers: {'Content-Type': 'application/json'},
//     credentials: 'include',
//     body: JSON.stringify({ username, password})
//   });


//   if (res.ok) {
//     console.log("bro is fckn stored");
//     window.location.href = "login.html";
//   } else {
//     console.log("bro is not stored");
//     window.location.href = "signup.html";
    
//   }

      
// }


// async function handleLogin(event) {
//   event.preventDefault();
//   var username = document.getElementById('login-input-id').value;
//   var password = document.getElementById('login-input-pass').value;

//   const res = await fetch(API_BASE + "/login", {
//     method: "POST",
//     headers: {'Content-Type': "application/json"},
//     credentials: 'include',
//     body: JSON.stringify({username, password})
//   })

//   if (res.ok) {
//     window.location.href = "home.html";
//     document.getElementById('welcomeName').textContent = username;
//   } else {
//     alert("Invalid login");
//     window.location.href = "login.html";
//   }

// }


// function logout() {
//   fetch(API_BASE + "/logout")
//   .then (res => res.text())
//   .then (data => {
//     console.log(data);
//   })
// }