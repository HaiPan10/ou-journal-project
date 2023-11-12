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