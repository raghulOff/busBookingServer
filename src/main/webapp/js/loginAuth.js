const API_BASE = "/tryingAuth/api/user";

async function handleLogin(event) {
  event.preventDefault();
  var username = document.getElementById('login-input-id').value;
  var password = document.getElementById('login-input-pass').value;

  const res = await fetch(API_BASE + "/login", {
    method: "POST",
    headers: {'Content-Type': "application/json"},
    credentials: 'include',
    body: JSON.stringify({username, password})
  })

  if (res.ok) {
    window.location.href = "home.html";
    document.getElementById('welcomeName').textContent = username;
  } else {
    alert("Invalid login");
    window.location.href = "login.html";
  }

}
