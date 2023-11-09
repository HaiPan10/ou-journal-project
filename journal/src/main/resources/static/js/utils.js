function downloadFile(type, name, byteContent) {
    var binaryString = window.atob(byteContent);
    var binaryLen = binaryString.length;
    var bytes = new Uint8Array(binaryLen);
    for (var i = 0; i < binaryLen; i++) {
        var ascii = binaryString.charCodeAt(i);
        bytes[i] = ascii;
    }
    var blob = new Blob([bytes], { type: `${type}` });
    var link = document.createElement('a');
    link.href = window.URL.createObjectURL(blob);
    link.download = name;
    link.click();
}

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
