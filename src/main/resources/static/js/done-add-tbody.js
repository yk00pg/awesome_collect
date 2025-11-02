// 入力フォームに行ブロックを追加する関数
function initDoneAddTbody() {
    const table = document.getElementById("variable-table");
    const addButton = document.getElementById("add-row");
    if (!table || !addButton) return;

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
            <td colspan="3">
              <input type="text" class="input-area"
                     name="contentList[${index}]"
                     placeholder=${contentPlaceholder} />
            </td>
            <td>
              <button type="button" class="remove-row-button">×</button>
            </td>
        `;
        tbody.appendChild(contentRow);

        const timeRow = document.createElement("tr");
        timeRow.className = "time-row";
        timeRow.innerHTML = `
            <th scope="row" class="task-th">学習時間 ${index + 1}</th>
            <td class="blank-cell"></td>
            <td>
              <input type="number" class="input-area"
                     name="hoursList[${index}]"
                     placeholder=${hoursPlaceholder}
                     min="0" max="24" value="0" />
            </td>
            <td>
              <span>時間</span>
            </td>
            <td>
              <input type="number" class="input-area"
                     name="minutesList[${index}]"
                     placeholder=${minutesPlaceholder}
                     min="0" max="59" value="0" />
            </td>
            <td>
              <span>分</span>
            </td>
        `;
        tbody.appendChild(timeRow);

        const memoRow = document.createElement("tr");
        memoRow.className = "memo-row";
        memoRow.innerHTML = `
            <th scope="row" class="task-th">メモ ${index + 1}</th>
            <td class="blank-row"></td>
            <td colspan="4" class="done-memo-area">
              <textarea class="input-done-memo-area" name="memoList[${index}]"
                        placeholder=${memoPlaceholder} ></textarea>
            </td>
        `;
        tbody.appendChild(memoRow);

        const tagRow = document.createElement("tr");
        tagRow.className = "tag-row";
        tagRow.innerHTML = `
            <th scope="row" class="task-th">タグ ${index + 1}</th>
            <td class="blank-cell"></td>
            <td colspan="4">
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
            <td colspan="6">
              <div class="border-line"></div>
            </td>
        `;
        tbody.appendChild(borderRow);

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
            block.querySelector(".content-row > .task-th").textContent = "内容 " + (i + 1);
            block.querySelector(".time-row > .task-th").textContent = "学習時間 " + (i + 1);
            block.querySelector(".memo-row > .task-th").textContent = "メモ " + (i + 1);
            block.querySelector(".tag-row > .task-th").textContent = "タグ " + (i + 1);
        });
    }
}