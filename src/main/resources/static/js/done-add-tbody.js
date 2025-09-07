document.addEventListener("DOMContentLoaded", () => {
    const table = document.getElementById("variable-table");
    const addButton = document.getElementById("add-row");

    // 追加する行ブロックの中身
    const createBlock = (index) => {
        const tbody = document.createElement("tbody");
        tbody.dataset.rowId = index;

        const contentRow = document.createElement("tr");
        contentRow.className = "content-row";
        contentRow.innerHTML = `
            <th scope="row" class="task-th">内容 ${index + 1}</th>
            <td class="blank-cell">
              <input type="hidden" name="idList[${index}]" value="0" />
            </td>
            <td>
              <input type="text" class="input-area"
                     name="contentList[${index}]" />
            </td>
            <td rowspan="2">
              <button type="button" class="remove-row-button">×</button>
            </td>
        `;
        tbody.appendChild(contentRow);

        const hoursRow = document.createElement("tr");
        hoursRow.className = "hours-row";
        hoursRow.innerHTML = `
            <th scope="row" class="task-th">学習時間 ${index + 1}</th>
            <td class="blank-cell"></td>
            <td>
              <input type="text" class="input-area"
                     name="hoursList[${index}]" />
            </td>
        `;
        tbody.appendChild(hoursRow);

        const memoRow = document.createElement("tr");
        memoRow.className = "memo-row";
        memoRow.innerHTML = `
            <th scope="row" class="task-th">メモ ${index + 1}</th>
            <td class="blank-row"></td>
            <td colspan="2" class="done-memo-area">
              <textarea class="input-done-memo-area" name="memoList[${index}]"></textarea>
            </td>
        `;
        tbody.appendChild(memoRow);

        const tagRow = document.createElement("tr");
        tagRow.className = "tag-row";
        tagRow.innerHTML = `
            <th scope="row" class="task-th">タグ ${index + 1}</th>
            <td class="blank-cell"></td>
            <td colspan="2">
              <input type="text" class="tags-input"
                     id="tags-input-${index}"
                     name="tagsList[${index}]" />
            </td>
        `;
        tbody.appendChild(tagRow);

        const tagInput = tagRow.querySelector(".tags-input");
        initTagify(tagInput);

        const borderRow = document.createElement("tr");
        borderRow.className = "border-row";
        borderRow.innerHTML = `
            <td colspan="4">
              <div class="border-line"></div>
            </td>
        `;
        tbody.appendChild(borderRow);

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
            block.querySelector(".content-row > .task-th").textContent = "内容 " + (i + 1);
            block.querySelector(".hours-row > .task-th").textContent = "学習時間 " + (i + 1);
            block.querySelector(".memo-row > .task-th").textContent = "メモ " + (i + 1);
            block.querySelector(".tag-row > .task-th").textContent = "タグ " + (i + 1);
        });
    }
});