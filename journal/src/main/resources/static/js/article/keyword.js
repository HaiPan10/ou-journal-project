const kwBox = document.querySelector("#keywords-box")

function insertKeywordBox(elementHTML) {
    kwBox.insertAdjacentHTML('beforeend', elementHTML);
}

document.addEventListener('DOMContentLoaded', function () {
    kwBox.innerText = keywords;
    //if (keywords) {
    //    let kws = keywords.split(" ");
    //    kws.forEach(k => {
    //        if (k.trim()) {
    //            let newKwHTML = ` <div id="kw-${k}" class="kw-box ml-0" role="alert">
    //                <div class="kw text-sm font-medium whitespace-nowrap">
    //                    ${k}
    //                </div>
    //              </div>`
    //            insertKeywordBox(newKwHTML);
    //        }
    //
    //    });
    //}
});
