function updateTextareaSize() {
    const textareas = document.querySelectorAll('.textarea-scale');

    textareas.forEach(textarea => {
        textarea.style.height = 'auto';
        textarea.style.height = textarea.scrollHeight + 3 + 'px';
        textarea.addEventListener('input', updateTextareaSize);
    })
}

updateTextareaSize();