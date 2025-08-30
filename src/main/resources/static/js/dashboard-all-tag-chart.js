// タグ別学習時間グラフ
const ctx = document.getElementById('allTagHours');

const tagNameList = tagHoursList.map(dto => dto.tagName);
const tagTotalHoursList = tagHoursList.map(dto => dto.totalHours);

// 上位3位だけ不透明度を上げてハイライト
const highlightRank = 3;
const colorsTags = tagTotalHoursList.map((v, i) =>
    i < highlightRank ? 'rgba(189, 221, 196, 0.8)' : 'rgba(189, 221, 196, 0.3)'
);

// 横棒グラフ
new Chart(ctx, {
    type: 'bar',
    data: {
        labels: tagNameList,
        datasets: [{
            data: tagTotalHoursList,
            backgroundColor: colorsTags,
            borderColor: 'rgba(189, 221, 196, 1)',
            borderWidth: 1
        }]
    },
    plugins: [ChartDataLabels],
    options: {
        indexAxis: 'y',
        responsive: true,
        plugins: {
            tooltip: {
              enabled: false
            },
            datalabels: {
                anchor: 'end',
                align: 'end',
                font: {
                    weight: 'bold'
                },
                color: '#627962',
                formatter: (value) => value
            },
            legend: {
              display: false
            }
        },
        scales: {
            y: {
                grid: {
                    display: false
                },
                beginAtZero: true,
                ticks: {
                    font: function(context) {
                        return {
                          weight: context.index < highlightRank ? 'bold' : 'normal'
                        };
                    },
                    color: '#627962',
                }
            },
            x: {
                ticks: {
                    display: false
                },
                grid: {
                    display: false
                },
                suggestedMax: Math.max(...tagTotalHoursList) * 1.1,
            }
        }
    }
});