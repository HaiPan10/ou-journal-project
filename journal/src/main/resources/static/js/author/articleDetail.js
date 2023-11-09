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

// async function reSubmitManuscript(articleId, obj) {
//     obj.disabled = true;
//     const errorManuscript = document.querySelector("#manuscript-errors");
//     const errorAuthorNote = document.querySelector("#author-note-errors");
//     const errorReference = document.querySelector("#reference-errors");
//     const errorAnonymousFile = document.querySelector("#anonymous-file-errors");
//     const form = document.forms.namedItem("resubmit");

//     const formData = new FormData(form);

//     const file = formData.get('file');
//     const authorNote = formData.get('note');
//     const fileAnonymous = formData.get('file-anonymous');
//     const reference = formData.get('reference').trim();

//     if (file && file.size > 0 && authorNote && fileAnonymous && fileAnonymous.size > 0 && reference.length > 0) {

//         formData.set('id', articleId);

//         const options = {
//             method: 'POST',
//             body: formData
//         };

//         const res = await fetch(`/api/articles/resubmit/${articleId}`, options);
//         if (res.ok) {
//             alert("Gửi lại bài thành công");
//             window.location.reload();
//             obj.disabled = false;

//         } else {
//             alert("Đã có lỗi xảy ra")
//             const error = await res.json();
//             console.log(error);
//             obj.disabled = false;

//         }

//     } else {
//         obj.disabled = false;

//         !file || file.size <= 0 ? errorManuscript.innerText = "Vui lòng gửi manuscript mới." : errorManuscript.innerText = "";

//         !authorNote ? errorAuthorNote.innerText = "Vui lòng nhập ghi chú phần đã chính sửa." : errorAuthorNote.innerText = "";

//         !reference || reference.length === 0 ? errorReference.innerText = "Vui lòng nhập trích dẫn" : errorReference.innerText = "";

//         !fileAnonymous || fileAnonymous.size <= 0 ? errorAnonymousFile.innerText = "Vui lòng gửi tệp nặc danh mới." : errorAnonymousFile.innerText = "";
//     }
// }

