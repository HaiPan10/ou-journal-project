async function withdraw(articleId, obj) {
    obj.disabled = false;
    if (articleId) {
        const options = {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            }
        };
        const res = await fetch(`/api/articles/author/article/withdraw/${articleId}`, options);
        if (res.ok) {
            alert("Rút bài thành công")
            window.location.reload();
            obj.disabled = true;

        }
        else {
            alert("Đã có lỗi xảy ra")
            const error = await res.json();
            console.log(error);
            obj.disabled = true;
        }
    }
}

async function reSubmitManuscript(articleId, obj) {
    obj.disabled = true;
    const errorManuscript = document.querySelector("#manuscript-errors");
    const errorAuthorNote = document.querySelector("#author-note-errors");
    const form = document.forms.namedItem("resubmit");

    const formData = new FormData(form);

    const file = formData.get('file');
    const authorNote = formData.get('note');
    if (file && file.size > 0 && authorNote) {

        formData.set('id', articleId);

        const options = {
            method: 'POST',
            body: formData
        };
    
        const res = await fetch(`/api/articles/re-submit/${articleId}`, options);
        if (res.ok) {
            alert("Gửi lại bài thành công");
            window.location.reload();
            obj.disabled = false;

        } else {
            alert("Đã có lỗi xảy ra")
            const error = await res.json();
            console.log(error);
            obj.disabled = false;

        }
        
    } else {
        obj.disabled = false;
        if (!file || file.size <= 0) {
            errorManuscript.innerText = "Vui lòng gửi manuscript mới.";
        } else {
            errorManuscript.innerText = "";
        }

        if (!authorNote) {
            errorAuthorNote.innerText = "Vui lòng nhập ghi chú phần đã chính sửa.";
        } else {
            errorAuthorNote.innerText = "";
        }
    }
}

