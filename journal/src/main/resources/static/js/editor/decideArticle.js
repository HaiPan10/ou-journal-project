function showFiles(e, displayEl) {
    if (e.target.files && e.target.files.length > 0) {
        displayEl.innerHTML = '';
        let htmlString = ''
        for (let file of e.target.files) {
            var fileName = file.name;
            htmlString += `
            <div class="w-full flex gap-x-3 p-3 mb-3 shadow flex items-center rounded-lg bg-main/10">
                                <div 
                                    class="uppercase bg-white relative text-xs font-semibold w-14 h-14 rounded shadow flex justify-center items-center overflow-hidden after:content-[''] after:w-full after:h-1 after:absolute after:bottom-0 after:left-0 after:bg-main">
                                    ${fileName.split('.').pop()}
                                </div>
                                <p>
                                    ${fileName}
                                </p>
                            </div>
            `
        }
        displayEl.innerHTML = htmlString
        displayEl.classList.remove("hidden");
    } else {
        displayEl.classList.add("hidden");
    }
}

document.getElementById('decide-files').addEventListener('change', function (e) {
    const fileDislay = document.querySelector("#editor-file-display");
    showFiles(e, fileDislay);
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