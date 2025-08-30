const today = new Date();

// 日別学習時間グラフ
const ctx1 = document.getElementById('dailyHours');

const dateList = dailyHoursList.map(dto => {
    const d = new Date(dto.date);
    return `${d.getMonth() + 1}月${d.getDate()}日`;
});
const dailyTotalHoursList = dailyHoursList.map(dto => dto.totalHours);

// 当日だけ不透明度を上げてハイライト
const thisMonth = today.getMonth() + 1;
const todayDate = today.getDate();
const todayLabel = `${today.getMonth() + 1}月${today.getDate()}日`;
const colorsDaily = dateList.map(label =>
    label === todayLabel ? 'rgba(220, 159, 149, 0.8)' : 'rgba(220, 159, 149, 0.3)'
);

// 縦棒グラフ
new Chart(ctx1, {
    type: 'bar',
    data: {
        labels: dateList,
        datasets: [{
            data: dailyTotalHoursList,
            backgroundColor: colorsDaily,
            borderColor: 'rgba(220, 159, 149, 1)',
            borderWidth: 1
        }]
    },
    plugins: [ChartDataLabels],
    options: {
        responsive: true,
        plugins: {
            tooltip: {
              enabled: false
            },
            datalabels: {
                anchor: 'end',
                align: 'end',
                offset: -3,
                font: {
                    weight: 'bold'
                },
                formatter: (value) => value
            },
            legend: {
              display: false
            }
        },
        scales:{
            y: {
                beginAtZero: true,
                suggestedMax: Math.max(...dailyTotalHoursList) * 1.1,
                ticks: {
                    color: '#664B4F'
                }
            },
            x: {
                ticks: {
                    font: function(context) {
                        const label = context.chart.data.labels[context.index];
                        return {
                          weight: label === todayLabel ? 'bold' : 'normal'
                        };
                    },
                    color: '#664B4F'
                }
            }
        }
    }
});

// 月別学習時間グラフ
const ctx2 = document.getElementById('monthlyHours');

const monthList = monthlyHoursList.map(dto => {
    const d = new Date(dto.date);
    return `${d.getMonth() + 1}月`;
});
const monthlyTotalHoursList = monthlyHoursList.map(dto => dto.totalHours);

// 当月だけ不透明度を上げてハイライト
const thisMonthLabel = `${today.getMonth() + 1}月`;
const colorsMonthly = monthList.map(label =>
    label === thisMonthLabel ? 'rgba(220, 159, 149, 0.8)' : 'rgba(220, 159, 149, 0.3)'
);

// 縦棒グラフ
new Chart(ctx2, {
    type: 'bar',
    data: {
        labels: monthList,
        datasets: [{
            data: monthlyTotalHoursList,
            backgroundColor: colorsMonthly,
            borderColor: 'rgba(220, 159, 149, 1)',
            borderWidth: 1
        }]
    },
    plugins: [ChartDataLabels],
    options: {
        responsive: true,
        plugins: {
            tooltip: {
              enabled: false
            },
            datalabels: {
                anchor: 'end',
                align: 'end',
                offset: -3,
                font: {
                    weight: 'bold'
                },
                color: '#664B4F',
                formatter: (value) => value
            },
            legend: {
              display: false
            }
        },
        scales:{
            y: {
                beginAtZero: true,
                suggestedMax: Math.max(...monthlyTotalHoursList) * 1.1,
                ticks: {
                    color: '#664B4F'
                }
            },
            x: {
                ticks: {
                    font: function(context) {
                        const label = context.chart.data.labels[context.index];
                        return {
                          weight: label === thisMonthLabel ? 'bold' : 'normal'
                        };
                    },
                    color: '#664B4F'
                }
            }
        }
    }
});

// タグ別学習時間グラフ
const ctx3 = document.getElementById('tagHours');

const tagNameList = tagHoursList.map(dto => dto.tagName);
const tagTotalHoursList = tagHoursList.map(dto => dto.totalHours);

// 上位3位だけ不透明度を上げてハイライト
const highlightRank = 3;
const colorsTags = tagTotalHoursList.map((v, i) =>
    i < highlightRank ? 'rgba(189, 221, 196, 0.8)' : 'rgba(189, 221, 196, 0.3)'
);

// 横棒グラフ
new Chart(ctx3, {
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