const easyMDE = new EasyMDE({
    element: document.getElementById("content"),
    spellChecker: false,
    toolbar: [
        "undo",
        "redo",
        "bold",
        "italic",
        "strikethrough",
        "unordered-list",
        "ordered-list",
        "code",
        "quote",
        "link",
        "table",
        "preview",
        "guide"
    ]
});