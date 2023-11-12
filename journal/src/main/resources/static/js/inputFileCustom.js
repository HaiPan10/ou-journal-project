function showFile(e, displayEl, extensionEl, nameEl) {
    if (e.target.files && e.target.files[0]) {
        var fileName = e.target.files[0].name;
        var fileExtension = fileName.split('.').pop();
        displayEl.classList.remove("hidden");
        extensionEl.innerHTML = fileExtension;
        nameEl.innerHTML = fileName
    } else {
        displayEl.classList.add("hidden");
    }
}


const inputsFileHaveDisplayComponent = document.querySelectorAll("input[type=file][data-file-display]");
inputsFileHaveDisplayComponent.forEach(input => {
    try {
        let dataFileDislay = input.getAttribute("data-file-display");
        input.addEventListener('change', function (e) {
            const fileDislay = document.querySelector(`#${dataFileDislay}`);
            const fileDisplayExtension = fileDislay.querySelector("[data-file-display-extension]");
            const fileDisplayName = fileDislay.querySelector("[data-file-display-name]");
            showFile(e, fileDislay, fileDisplayExtension, fileDisplayName);
        });
    } catch (e) {}
})