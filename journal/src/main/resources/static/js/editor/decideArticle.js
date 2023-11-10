document.getElementById('decide-files').addEventListener('change', function (e) {
    const fileDislay = document.querySelector("#file-display");
    const fileDisplayExtension = fileDislay.querySelector("#file-display-extension");
    const fileDisplayName = fileDislay.querySelector("#file-display-name");
    showFile(e, fileDislay, fileDisplayExtension, fileDisplayName);
});

const form = document.forms.namedItem("decide-form");
function decideArticle(status) {
    const formData = new FormData(form);
    formData.append("status", status);
    const file = formData.get('decideFiles');

    if (file && file.size > 0) {
        fetch(`/api/articles/editor/decide/${articleId}`, {
            method: 'POST',
            body: formData
        }).then(res => {
            if (res.ok) {
                window.location.href = "/editor/reviewed-articles"
            } else {
                throw new Error(res.error)
            }
        }).catch(err => {
            alert(err);
            console.log(err);
        });
    }
}

function viewReviewFile(reviewArticleId) {
    window.open(`/api/review-articles/view/${reviewArticleId}`, '_blank');
}

function downloadReviewFile(articleId, manuscriptId, reviewArticleId) {
    var name = "article-" + articleId + "-manuscript-" + manuscriptId + "-reviewId-" + reviewArticleId + "-RV";
    fetch(`/api/review-articles/${reviewArticleId}`, {
        method: 'GET',
    }).then(res => {
        if (res.ok) {
            return res.json()
        } else {
            throw new Error(res.error)
        }
    }).then(json => {
        downloadFile(json.type, name, json.content)
    }).catch(err => {
        alert(err);
        console.log(err);
    });
}