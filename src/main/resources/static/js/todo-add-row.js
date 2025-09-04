document.addEventListener("DOMContentLoaded", () => {
    const table = document.getElementById("variable-table");
    const addButton = document.getElementById("add-row");

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
                     name="contentList[${index}]" />
            </td>
            <td>
              <button type="button" class="remove-row-button">×</button>
            </td>
        `;
        tbody.appendChild(contentRow);

        return tbody;
    };

    // 行ブロックを追加
    addButton.addEventListener("click", () => {
        const blocks = table.querySelectorAll("tbody[data-row-id]");
        const index = blocks.length;
        const newBlock = createBlock(index);
        table.appendChild(newBlock);
    })

    // 追加した行ブロックを削除
    table.addEventListener("click", (e) => {
        if(e.target.classList.contains("remove-row-button")) {
            const tbody = e.target.closest("tbody");
            tbody.remove();

            updateLabels();
        }
    });

    // ラベルの番号を振り直す
    function updateLabels() {
        const blocks = table.querySelectorAll("tbody[data-row-id]");
        blocks.forEach((block, i) => {
            block.querySelector(".task-th").textContent = "内容 " + (i + 1);
        });
    }
});