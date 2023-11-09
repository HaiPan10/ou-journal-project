// Lấy phần tử input
const menuToogle = document.querySelector('#menu-toggle');
const menuIcon = document.querySelector('#menu-icon');

// Định nghĩa hàm xử lý sự kiện
function handleChange(event) {
  if (event.target.checked) {
    menuIcon.classList.add("rotate-90")
  } else {
    menuIcon.classList.remove("rotate-90")
  }
}

function callChangeRoleApi(obj) {
  if (confirm("Bạn có chắc chắn muốn đổi vai trò không")) {
    let roleName = obj.getAttribute("data-role");
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
      if (response.status === 204) {
        window.location.href = "/main-menu"
      } else if (response.status === 403) {
        alert("Không có vai trò đó")
      } else {
        throw new Error()
      }
    }).catch(err => {
      alert("Something Wrong")
    })
  }

}
var url = window.location.pathname;
var tabName = url.split("/");
// var activeItem;

switch (tabName[1]) {
  case 'homepage':
    document.querySelector("#nav-item-homepage").classList.add("active");
    break;
  case 'main-menu': case 'submission': case 'reviewer': case 'editor':
    document.querySelector("#nav-item-main-menu").classList.add("active");
    break;
  case 'submit':
    document.querySelector("#nav-item-submit").classList.add("active");
    break;
  default:
    activeItem = 'homepage';
}
// console.log("Active item is: " + activeItem);

// Gắn hàm xử lý vào sự kiện 'change'
menuToogle.addEventListener('change', handleChange);
