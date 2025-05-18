
const API_BASE = "/tryingAuth/api/user";


fetch(API_BASE + "/status")
.then (res => {
    if (res.ok) {
        
    } else {
        window.location.href = "login.html";
    }
})

window.onpageshow = function(event) {
    if (event.persisted) {
        window.location.reload(); // reload if page is served from cache
    }
};






function logout() {
  fetch(API_BASE + "/logout")
  .then (res => res.text())
  .then (data => {
    console.log(data);
  })
  window.location.href = "login.html"
}