window.onpageshow = function (event) {
  console.log("pageshow fired", event.persisted);
  if (event.persisted) {
    console.log("Page loaded from cache. Reloading...");
    window.location.reload();
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
