API_BASE = "/tryingAuth/api/user";

fetch(API_BASE + "/adminStatus")
.then (res => {
  if (res.ok) {
    
    return res.json();
  } else {
    window.location.href = "adminLogin.html";
  }
})
.then (user => {
  console.log(user.username);
})




// fetch(API_BASE + "/adminStatus").then((res) => {
//   if (res.ok) {
//     window.location.href = "admin.html";
//   } else {
//     window.location.href = "adminLogin.html";
//   }
// });

// const res = fetch(API_BASE + "/signup", {
//   method: "POST",
//   headers: { "Content-Type": "application/json" },
//   credentials: "include",
//   body: JSON.stringify({ username, password, role: "ADMIN" }),
// });

// if (res.ok) {
//   window.location.href = "admin.html";
// } else {
//   console.log(res);
// }
