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


document.getElementById('file-upload').addEventListener('change', function (e) {
    const fileDislay = document.querySelector("#file-display");
    const fileDisplayExtension = fileDislay.querySelector("#file-display-extension");
    const fileDisplayName = fileDislay.querySelector("#file-display-name");
    showFile(e, fileDislay, fileDisplayExtension, fileDisplayName);
});

document.getElementById('anonymous-file-upload').addEventListener('change', function (e) {
    const anonymousFileDislay = document.querySelector("#anonymous-file-display");
    const anonymousFileDisplayExtension = anonymousFileDislay.querySelector("#anonymous-file-display-extension");
    const anonymousFileDisplayName = anonymousFileDislay.querySelector("#anonymous-file-display-name");
    showFile(e, anonymousFileDislay, anonymousFileDisplayExtension, anonymousFileDisplayName);
});

document.getElementById('appendix-file-upload').addEventListener('change', function (e) {
    const appendixFileDislay = document.querySelector("#appendix-file-display");
    const appendixFileDisplayExtension = appendixFileDislay.querySelector("#appendix-file-display-extension");
    const appendixFileDisplayName = appendixFileDislay.querySelector("#appendix-file-display-name");
    showFile(e, appendixFileDislay, appendixFileDisplayExtension, appendixFileDisplayName);
});