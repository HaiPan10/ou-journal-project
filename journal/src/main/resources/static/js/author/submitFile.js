const fileTypeList = {
    "originFile": {
        "type": "originFile",
        "displayName": "File gốc",
        "description": "File gốc kèm thông tin tác giả"
    },
    "anonymousFile": {
        "type": "anonymousFile",
        "displayName": "File ẩn danh",
        "description": "File ẩn danh là file gốc không kèm thông tin tác giả"
    },
    "apendixFile": {
        "type": "apendixFile",
        "displayName": "File phụ lục",
        "description": "File gốc kèm thông tin tác giả"
    }
}

let fileList = [];

function MyFile(id, multipleFile, fileName, fileSize, lastModified, fileType) {
    this.id = id;
    this.multipleFile = multipleFile;
    this.fileName = fileName;
    this.fileSize = fileSize;
    this.lastModified = lastModified;
    this.fileType = fileType;
}

function changeFixedSize(size) {
    var fileSizeInKB = size / 1024;
    var fileSizeInMB = fileSizeInKB / 1024;

    if (fileSizeInMB < 1) {
        return fileSizeInKB.toFixed(2) + " KB";
    } else {
        return fileSizeInMB.toFixed(2) + " MB";
    }
}

function changeLastModifiedToDate(lastModified) {
    var date = new Date(lastModified);
    var formattedDate = date.toLocaleDateString("vi-VN");
    return formattedDate;
}

const fileTypeDescription = document.querySelector("#description");

const fileTypeSelector = document.querySelector("#file-type-selector");
fileTypeSelector.addEventListener("change", function () {
    const fileType = fileTypeList[fileTypeSelector.value];
    fileTypeDescription.innerText = fileType["description"]
})

function createNormalCol(value) {
    const col = document.createElement("td");
    col.classList.add("px-6", "py-3")
    col.innerText = value;
    return col;
}


function downloadFileFunc(file) {
    var link = document.createElement('a');
    // var blob = new Blob([file]);
    link.href = URL.createObjectURL(file);
    link.download = file.name;

    document.body.appendChild(link);

    link.click();

    document.body.removeChild(link);

    URL.revokeObjectURL(link.href);
}

function delFileFunc(obj) {
    const fileId = obj.getAttribute("fileId");
    if (fileId) {
        const fileEl = document.getElementById(fileId);
        if (fileEl) fileEl.remove();
        const file = fileList.filter(file => file.id == fileId);
        if (file.length > 0) {
            const type = file[0]["fileType"]["type"];
            const requiredFile = document.querySelector(`[data-file-type-required=${type}`);
            if (requiredFile) requiredFile.classList.remove("success");
        }
        fileList = fileList.filter(file => file.id != fileId);
    }
}

function delAFile(obj) {
    if (confirm("Bạn có chắc chắn muốn xóa file này không")) {
        delFileFunc(obj)
    }
}

function downloadAFile(obj) {
    const fileId = obj.getAttribute("fileId");
    if (fileId) {
        const file = fileList.filter(file => file.id == fileId);
        if (file.length > 0) {
            downloadFileFunc(file[0].multipleFile);

        }
    }
}


const checkAll = document.querySelector("#check-all");
checkAll.addEventListener("change", function () {
    const fileBoxes = document.querySelectorAll("input[type='checkbox'][name='file-check-box']");
    fileBoxes.forEach(box => {
        box.checked = checkAll.checked;
    })
})
const bodyTableFiles = document.querySelector('#body-table-files');

function loadFile(e) {
    if (e.target.files && e.target.files[0]) {
        const fileId = "id-" + crypto.randomUUID();
        const file = e.target.files[0];
        const fileName = file.name;
        const fileSize = changeFixedSize(file.size)
        const fileLastModified = changeLastModifiedToDate(file.lastModified)
        const fileType = fileTypeList[fileTypeSelector.value]

        const requiredFile = document.querySelector(`[data-file-type-required=${fileType['type']}`);
        if (requiredFile) requiredFile.classList.add("success");
        oldFile = fileList.filter(x => x.fileType.type == fileType.type);

        if (oldFile.length > 0) {
            const oldFileId = oldFile[0].id;
            const fileEl = document.getElementById(oldFileId);
            if (fileEl) fileEl.remove();
            let index = 0;
            for (index; index < fileList.length; index++) {
                if (fileList[index].id == oldFile[0].id)
                    break;
            }
            if (index < fileList.length)
                fileList[index] = new MyFile(fileId, file, fileName, fileSize, fileLastModified, fileType);
        } else {
            fileList.push(new MyFile(fileId, file, fileName, fileSize, fileLastModified, fileType));
        }

        const row = document.createElement("tr");
        row.id = fileId;
        row.classList.add("table-row")

        const colSelector = createNormalCol('');
        colSelector.classList.add("text-center");
        const selector = document.createElement("input");
        selector.type = "checkbox";
        selector.name = "file-check-box"
        selector.classList.add("filter-input");
        selector.setAttribute("fileId", fileId);
        colSelector.appendChild(selector);
        row.appendChild(colSelector);

        const colTypeName = createNormalCol(fileType["displayName"]);
        colTypeName.setAttribute("data-file-type", fileType["type"]);
        row.appendChild(colTypeName);

        const colDescription = createNormalCol(fileType["description"]);
        row.appendChild(colDescription);

        const colName = createNormalCol(fileName);
        row.appendChild(colName);

        const colSize = createNormalCol(fileSize);
        row.appendChild(colSize);

        const colLastModified = createNormalCol(fileLastModified);
        row.appendChild(colLastModified);

        const colActions = createNormalCol("");
        const actionsContainer = document.createElement("div");
        actionsContainer.classList.add("flex", "justify-center", "gap-2");
        const downloadAction = document.createElement("a");
        downloadAction.classList.add("link");
        downloadAction.innerText = 'Tải';
        downloadAction.setAttribute("fileId", fileId);
        downloadAction.addEventListener("click", function () {
            downloadAFile(downloadAction)
        })
        const deleteAction = document.createElement("a");
        deleteAction.classList.add("link");
        deleteAction.innerText = 'Xóa';
        deleteAction.setAttribute("fileId", fileId);
        deleteAction.addEventListener("click", function () {
            delAFile(deleteAction)
        })

        actionsContainer.appendChild(downloadAction);
        actionsContainer.appendChild(deleteAction);
        colActions.appendChild(actionsContainer);
        row.appendChild(colActions);



        bodyTableFiles.appendChild(row);
        e.target.value = "";

    }
}

function downloadAllFile() {
    let fileBoxes = document.querySelectorAll("input[type='checkbox'][name='file-check-box']");
    fileBoxes = Array.from(fileBoxes).filter(x => x.checked == true);
    fileBoxes.forEach(el => {
        downloadAFile(el);
    })
}

function delAllFile() {
    let fileBoxes = document.querySelectorAll("input[type='checkbox'][name='file-check-box']");
    fileBoxes = Array.from(fileBoxes).filter(x => x.checked == true);
    if (fileBoxes.length > 0)
        if (confirm("Bạn có chắc chắn muốn xóa các file đã chọn không")) {
            fileBoxes.forEach(el => {
                delFileFunc(el);
            })
        }
}

function goBackStep() {
    if (fileList.length > 0)
        if (!confirm("Các file hiện tại sẽ mất nếu trở về, bạn có chắc chắn muốn trở về không")) {
            return;
        }
    window.location.href = '/submit/step5';

}


function completeStep() {
    if (fileList.length <= 0) {
        alert("Bạn chưa cung cấp các file theo yêu cầu!");
        return;
    }

    originFile = fileList.filter(x => x.fileType.type == "originFile");
    anonymousFile = fileList.filter(x => x.fileType.type == "anonymousFile");
    apendixFile = fileList.filter(x => x.fileType.type == "apendixFile");

    if (originFile.length <= 0) {
        alert("Vui lòng cung cấp file gốc của bài gửi");
        fileTypeSelector.value = "originFile";
        return;
    }

    if (anonymousFile.length <= 0) {
        alert("Vui lòng cung cấp file ẩn danh của bài gửi");
        fileTypeSelector.value = "anonymousFile";
        return;
    }


    var formData = new FormData();
    formData.append('file', originFile[0].multipleFile);
    formData.append('anonymousFile', anonymousFile[0].multipleFile);
    if (apendixFile.length > 0) {
        formData.append('appendixFile', apendixFile[0].multipleFile);
    }
    fetch("/submit/step6", {
        method: "POST",
        body: formData,
        redirect: 'follow'
    }).then(response => {
        if (response.ok) {
            window.location.href = response.url;
        } else {
            alert ("Có lỗi xảy ra");
        }
    });
}