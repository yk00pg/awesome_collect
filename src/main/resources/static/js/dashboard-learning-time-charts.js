const today = new Date();
const basicColor = 'rgba(220, 159, 149, 1)';
const textColor = '#664B4F';
const highlightColor = 'rgba(220, 159, 149, 0.8)';
const unfocusedColor = 'rgba(220, 159, 149, 0.3)';

// 日別学習時間グラフ
const ctx1 = document.getElementById('dailyTime');

const dateList = dailyTimeList.map(dto => {
    const d = new Date(dto.date);
    return `${d.getMonth() + 1}月${d.getDate()}日`;
});
const dailyTotalTimeList = dailyTimeList.map(dto => dto.totalTime);

// 当日だけ不透明度を上げてハイライト
const thisMonth = today.getMonth() + 1;
const todayDate = today.getDate();
const todayLabel = `${today.getMonth() + 1}月${today.getDate()}日`;
const colorsDaily = dateList.map(label =>
    label === todayLabel
    ? highlightColor
    : unfocusedColor
);

// 縦棒グラフ
new Chart(ctx1, {
    type: 'bar',
    data: {
        labels: dateList,
        datasets: [{
            data: dailyTotalTimeList,
            backgroundColor: colorsDaily,
            borderColor: basicColor,
            borderWidth: 1
        }]
    },
    plugins: [ChartDataLabels],
    options: {
        responsive: true,
        plugins: {
            tooltip: {
                enabled: false,
            },
            datalabels: {
                anchor: 'end',
                align: 'end',
                offset: -3,
                color: textColor,
                formatter: (value) => formatMinutesToHM(value)
            },
            legend: {
              display: false
            }
        },
        scales:{
            y: {
                beginAtZero: true,
                suggestedMax: Math.max(...dailyTotalTimeList) * 1.1,
                ticks: {
                    display: false
                },
                grid: {
                    display: false
                }
            },
            x: {
                ticks: {
                    font: function(context) {
                        const label = context.chart.data.labels[context.index];
                        return {
                            weight: label === todayLabel
                                    ? 'bold'
                                    : 'normal'
                        };
                    },
                    color: textColor
                }
            }
        }
    }
});

// 曜日別学習時間グラフ
const ctx2 = document.getElementById('dayOfWeekTime');

const dayOfWeekList = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
const avgTimeList = dayOfWeekTimeList.map(dto => dto.avgTime);

// 当日と同じ曜日だけ不透明度を上げてハイライト
const todayIndex = today.getDay();
const colorsDayOfWeek = dayOfWeekList.map((_, idx) =>
    idx === todayIndex
    ? highlightColor
    : unfocusedColor
);

// 縦棒グラフ
new Chart(ctx2, {
    type: 'bar',
    data: {
        labels: dayOfWeekList,
        datasets: [{
            data: avgTimeList,
            backgroundColor: colorsDayOfWeek,
            borderColor: basicColor,
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
                color: textColor,
                formatter: (value) => {
                    if (!value) return null;
                    return formatMinutesToHM(value);
                }
            },
            legend: {
              display: false
            }
        },
        scales:{
            y: {
                beginAtZero: true,
                grace: '10%',
                ticks: {
                    display: false,
                },
                grid: {
                    display: false
                }
            },
            x: {
                ticks: {
                    font: function(context) {
                        return {
                            weight: context.index === todayIndex
                                    ? 'bold'
                                    : 'normal'
                        };
                    },
                    color: textColor
                }
            }
        }
    }
});

// 月別学習時間グラフ
const ctx3 = document.getElementById('monthlyTime');

const monthList = monthlyTimeList.map(dto => {
    const d = new Date(dto.date);
    return `${d.getMonth() + 1}月`;
});
const monthlyTotalTimeList = monthlyTimeList.map(dto => dto.totalTime);

// 当月だけ不透明度を上げてハイライト
const thisMonthLabel = `${today.getMonth() + 1}月`;
const colorsMonthly = monthList.map(label =>
    label === thisMonthLabel
    ? highlightColor
    : unfocusedColor
);

// 縦棒グラフ
new Chart(ctx3, {
    type: 'bar',
    data: {
        labels: monthList,
        datasets: [{
            data: monthlyTotalTimeList,
            backgroundColor: colorsMonthly,
            borderColor: basicColor,
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
                color: textColor,
                formatter: (value) => formatMinutesToHM(value)
            },
            legend: {
              display: false
            }
        },
        scales:{
            y: {
                beginAtZero: true,
                suggestedMax: Math.max(...monthlyTotalTimeList) * 1.1,
                ticks: {
                    display: false
                },
                grid: {
                    display: false
                }
            },
            x: {
                ticks: {
                    font: function(context) {
                        const label = context.chart.data.labels[context.index];
                        return {
                            weight: label === thisMonthLabel
                                  ? 'bold'
                                  : 'normal'
                        };
                    },
                    color: textColor
                }
            }
        }
    }
});

// タグ別学習時間グラフ
const ctx4 = document.getElementById('tagTime');

const tagNameList = tagTimeList.map(dto => dto.tagName);
const tagTotalTimeList = tagTimeList.map(dto => dto.totalTime);

// 上位3位だけ不透明度を上げてハイライト
const highlightRank = 3;
const colorsTags = tagTotalTimeList.map((v, i) =>
    i < highlightRank
    ? 'rgba(189, 221, 196, 0.8)'
    : 'rgba(189, 221, 196, 0.3)'
);

// 横棒グラフ
new Chart(ctx4, {
    type: 'bar',
    data: {
        labels: tagNameList,
        datasets: [{
            data: tagTotalTimeList,
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
                color: '#627962',
                formatter: (value) => formatMinutesToHM(value)
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
                            weight: context.index < highlightRank
                                    ? 'bold'
                                    : 'normal'
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
                suggestedMax: Math.max(...tagTotalTimeList) * 1.1,
            }
        }
    }
});

function formatMinutesToHM(minutes) {
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return (hours > 0 ? hours + 'h ' : '') + mins + 'm';
}