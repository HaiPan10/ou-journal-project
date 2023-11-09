document.getElementById('review-file').addEventListener('change', function (e) {
    const fileDislay = document.querySelector("#file-display");
    const fileDisplayExtension = fileDislay.querySelector("#file-display-extension");
    const fileDisplayName = fileDislay.querySelector("#file-display-name");
    showFile(e, fileDislay, fileDisplayExtension, fileDisplayName);
});

function doneReview(status) {
    const form = document.forms.namedItem("review-form");
    const formData = new FormData(form);
    formData.append("status", status);
    const file = formData.get('reviewFile');

    if (file && file.size > 0) {
        fetch(`/api/review-articles/reviewer/review/${id}`, {
            method: 'POST',
            body: formData
        }).then(res => {
            if (res.ok) {
                window.location.reload();
            } else {
            }
        }).catch(err => {
            alert(err);
            console.error(err);
        });
    }
}