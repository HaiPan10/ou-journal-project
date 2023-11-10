const note = document.querySelector("#note");
const authorNote = document.querySelector("#author-note");

const btnNote = document.querySelector("#btn-collapse-note");
const btnNoteIcon = document.querySelector("#icon-collapse-note");
const noteContent = document.querySelector("#note-content");

const btnAuthorNote = document.querySelector("#btn-collapse-author-note");
const btnAuthorNoteIcon = document.querySelector("#icon-collapse-author-note");
const authorNoteContent = document.querySelector("#author-note-content");

 
try {
    btnNote.addEventListener('click', function () {
        note.classList.toggle('note-hidden');
        btnNoteIcon.classList.toggle('rotate-90');
        noteContent.classList.toggle('hidden');
    });
} catch (e) {

}

try {
    btnAuthorNote.addEventListener('click', function () {
        authorNote.classList.toggle('note-hidden');
        btnAuthorNoteIcon.classList.toggle('rotate-90');
        authorNoteContent.classList.toggle('hidden');
    });
} catch (e) {

}
