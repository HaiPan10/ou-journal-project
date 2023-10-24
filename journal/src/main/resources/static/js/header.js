// Lấy phần tử input
const menuToogle = document.querySelector('#menu-toggle');
const menuIcon = document.querySelector('#menu-icon');

// Định nghĩa hàm xử lý sự kiện
function handleChange(event) {
  if (event.target.checked) {
    menuIcon.innerHTML = "menu_open"
  } else {
    menuIcon.innerHTML = "menu"
  }
}

function callChangeRoleApi() {
  let roleName = document.getElementById("roleName").value
  console.log(roleName)
  fetch("/api/accounts/change-role", {
    headers: {
      "Content-Type": "application/json"
    },
    method: "POST",
    body: JSON.stringify({
      roleName: roleName
    })
  }).then(response => {
    if(response.status === 204){
      window.location.reload()
    } else if(response.status === 403){
      alert("Không có vai trò đó")
    } else {
      throw new Error()
    }
  }).catch(err => {
    alert("Something Wrong")
  })
}

// Gắn hàm xử lý vào sự kiện 'change'
menuToogle.addEventListener('change', handleChange);
