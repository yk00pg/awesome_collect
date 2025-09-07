function initTagify(input){
    return new Tagify(input, {
        pattern: /#/,
        whitelist: tagNameList,
        dropdown: {
            enabled: 0,
            classname: "tagsList-look",
            maxItems: 10,
            closeOnSelect: false
        },
        enforceWhitelist: false,
    });
}

window.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".tags-input").forEach(input => {
        initTagify(input);
    });
});