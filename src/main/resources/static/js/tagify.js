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

const tagifyMap = new WeakMap();

function initAllTagify(){
    document.querySelectorAll(".tags-input").forEach(input => {
        if (!tagifyMap.has(input)) {
            tagifyMap.set(input, initTagify(input));
        }
    });
}