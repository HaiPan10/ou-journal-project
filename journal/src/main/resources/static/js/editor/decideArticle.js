document.getElementById('editor-file').addEventListener('change', function (e) {
    const fileDislay = document.querySelector("#file-display");
    const fileDisplayExtension = fileDislay.querySelector("#file-display-extension");
    const fileDisplayName = fileDislay.querySelector("#file-display-name");
    showFile(e, fileDislay, fileDisplayExtension, fileDisplayName);
});