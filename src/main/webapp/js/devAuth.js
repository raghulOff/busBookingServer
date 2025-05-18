const res = await fetch(API_BASE + "/login", {
  method: "POST",
  headers: { "Content-Type": "application/json" },
  credentials: "include",
  body: JSON.stringify({ username, password, role: "DEVELOPER" }),
});

if (res.ok) {
  // console.log("bro is fckn stored");
  window.location.href = "developer.html";
} else {
  console.log(res);
}
