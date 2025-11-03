// Enter押下時に次のフィールドに移動させる関数
function enableEnterToNextField(formSelector) {
    const form = document.querySelector(formSelector);
    if (!form) return;

    let previousKey = '';
    const inputField = form.querySelectorAll('input, select, textarea');
    inputField.forEach((input, index) => {
        input.addEventListener('keydown', (e) => {

            const currentKey = e.key;
            if (input.tagName.toLowerCase() === 'textarea') {
                return;
            }

            if (e.key == 'Enter') {
                // ドロップダウン選択中と想定される場合はスキップする。
                if (input.tagName.toLowerCase() === 'select' &&
                    ['ArrowUp', 'ArrowDown', 'ArrowRight', 'ArrowLeft'].includes(previousKey)) {
                  previousKey = currentKey;
                  return;
                }

                e.preventDefault();

                const next = inputField[index + 1];
                if (next) {
                    // 次のフィールドのクラスが"tags-input"の場合はtagify入力欄にフォーカスする。
                    if (next.classList.contains('tags-input')) {
                        const tagify = tagifyMap.get(next);
                        if (tagify && tagify.DOM && tagify.DOM.input) {
                            tagify.DOM.input.focus();
                            return;
                        }
                    }

                    // 次のフィールドのidが"content"でeasyMDEが存在する場合はeasyMDEエディタにフォーカスする。
                    if (next.id === 'content' && typeof easyMDE !== 'undefined') {
                        easyMDE.codemirror.focus();
                        return;
                    }

                    next.focus();
                } else {
                    const submitButton = form.querySelector('button[type="submit"]');
                    if (submitButton) submitButton.click();
                }
            }

            previousKey = currentKey;
        });
    });
}