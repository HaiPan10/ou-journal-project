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

// Gắn hàm xử lý vào sự kiện 'change'
menuToogle.addEventListener('change', handleChange);
