// 入力フォームに行ブロックを追加する関数
function initTodoAddTbody() {
    const table = document.getElementById("variable-table");
    const addButton = document.getElementById("add-row");
    if (!table || !addButton) return;

    // 追加する行ブロックの中身
    const createBlock = (index) => {
        const tbody = document.createElement("tbody");
        tbody.dataset.rowId = index;

        const contentRow = document.createElement("tr");
        contentRow.className = "variable-row";
        contentRow.innerHTML = `
            <th scope="row" class="task-th">内容 ${index + 1}</th>
            <td class="blank-cell">
              <input type="hidden" name="idList[${index}]" value="0" />
            </td>
            <td>
              <input type="text" class="input-area"
                     name="contentList[${index}]"
                     placeholder=${contentPlaceholder} />
            </td>
            <td>
              <button type="button" class="remove-row-button">×</button>
            </td>
        `;
        tbody.appendChild(contentRow);

        return tbody;
    };

    // 追加ボタンをクリックしたときに行ブロックを追加する。
    addButton.onclick = () => {
        const blocks = table.querySelectorAll("tbody[data-row-id]");
        const index = blocks.length;
        const newBlock = createBlock(index);
        table.appendChild(newBlock);
    };

    // 削除ボタンをクリックしたときに行ブロックを削除する。
    table.onclick = (e) => {
        if(e.target.classList.contains("remove-row-button")) {
            e.target.closest("tbody").remove();
            updateLabels();
        }
    };

    // ラベルの番号を振り直す関数
    function updateLabels() {
        const blocks = table.querySelectorAll("tbody[data-row-id]");
        blocks.forEach((block, i) => {
            block.querySelector(".task-th").textContent = "内容 " + (i + 1);
        });
    }
}