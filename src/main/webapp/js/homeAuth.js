
window.onpageshow = function (event) {
  if (event.persisted) {
    window.location.reload(); // reload if page is served from cache
  }
};



const API_BASE = "/tryingAuth/api/user";

fetch(API_BASE + "/status")
  .then((res) => {
    if (res.ok) {
      return res.json();
    } else {
      window.location.href = "login.html";
    }
  })
  .then((user) => {
    if (user) {
      document.getElementById("welcomeName").textContent = user.username;
      document.getElementById("roleId").textContent = user.role;
    }
  });

function logout() {
  fetch(API_BASE + "/logout")
    .then((res) => res.text())
    .then((data) => {
      console.log(data);
      window.location.href = "login.html";
    });
}
