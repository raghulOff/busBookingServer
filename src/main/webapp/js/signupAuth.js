const API_BASE = "/tryingAuth/api/user";

async function handleRegister(e) {
  e.preventDefault();
  var username = document.getElementById("reg-input-id").value;
  var password = document.getElementById("reg-input-password").value;
  // console.log(username);
  // console.log(password);
  const res = await fetch(API_BASE + "/signup", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    credentials: "include",
    body: JSON.stringify({ username, password, role: "USER" }),
  });

  if (res.ok) {
    // console.log("bro is fckn stored");
    window.location.href = "login.html";
  } else {
    // console.log("bro is not stored");
    alert("User already exists");
    window.location.href = "signup.html";
  }
}
