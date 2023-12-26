function handleCategoryHover(obj) {
    const categoryName = document.querySelector(`.category-name[data-category='${obj.id}']`);
    if (categoryName)
        categoryName.classList.add("category-name-active");
}

function handleCategoryOutHover(obj) {
    const categoryName = document.querySelector(`.category-name[data-category='${obj.id}']`);
    if (categoryName)
        categoryName.classList.remove("category-name-active");
}

function handleCategoryNameHover(obj) {
    const id = obj.getAttribute("data-category");
    if (id) {
        category = document.querySelector(`#${id + ""}`);
        if (category) {
            category.classList.add("category-active");
        }
    }
}

function handleCategoryNameOutHover(obj) {
    const id = obj.getAttribute("data-category");
    if (id) {
        category = document.querySelector(`#${id + ""}`);
        if (category) {
            category.classList.remove("category-active");
        }
    }
}