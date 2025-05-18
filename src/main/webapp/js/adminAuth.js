
fetch(API_BASE + "/status")
.then (res => {
    if (res.ok) {
        
    } else {
        window.location.href = "login.html";
    }
})

