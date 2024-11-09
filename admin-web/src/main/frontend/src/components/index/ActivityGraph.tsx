import React from "react";
import { getRes } from "../../utils/constants";

// 定义类型
export interface ActivityDay {
    date: string; // YYYY-MM-DD 格式
    count: number; // 动作数量，用于决定颜色深浅
}

interface ActivityGraphProps {
    data: ActivityDay[]; // 一年的数据
}

const ActivityGraph: React.FC<ActivityGraphProps> = ({ data }) => {
    const daysInWeek = 7;
    const weekWidth = 15; // 每周宽度（包含间距）
    const dayHeight = 15; // 每天高度（包含间距）
    const cellSize = 10; // 每个格子的大小
    const textOffset = 20; // 顶部和左侧的文字偏移
    const textOffsetX = 20; // 左侧文字偏移量
    const textOffsetY = 20; // 顶部文字偏移量

    // 调整数据，确保以星期一开头并填充前后空白
    const generateActivityGrid = () => {
        const grid: ActivityDay[][] = [];
        let currentWeek: ActivityDay[] = [];

        const firstDate = new Date(data[0]?.date);
        const firstDayOfWeek = firstDate.getDay(); // 获取第一天的星期几 (0 表示星期日)
        const offsetStart = (firstDayOfWeek + 6) % 7; // 计算偏移量 (0: 星期一, 6: 星期日)

        // 填充前面的空白天
        for (let i = 0; i < offsetStart; i++) {
            currentWeek.push({ date: "", count: 0 });
        }

        // 填充实际数据
        data.forEach((day) => {
            currentWeek.push(day);

            // 如果满一周，则推入 grid 并开启新的一周
            if (currentWeek.length === daysInWeek) {
                grid.push(currentWeek);
                currentWeek = [];
            }
        });

        // 填充最后一周的空白天（保持完整列）
        while (currentWeek.length > 0 && currentWeek.length < daysInWeek) {
            currentWeek.push({ date: "", count: 0 });
        }
        if (currentWeek.length === daysInWeek) {
            grid.push(currentWeek);
        }

        return grid;
    };

    const activityGrid = generateActivityGrid();

    // 颜色梯度
    const getColor = (count: number) => {
        if (count === 0) return "#ebedf0"; // 无活动
        if (count < 2) return "#9be9a8";
        if (count < 4) return "#40c463";
        if (count < 8) return "#30a14e";
        return "#216e39";
    };

    // 生成月份标记
    const generateMonths = () => {
        const months: { x: number; name: string }[] = [];
        const monthNames = (getRes()["lang"] as string).includes("en")
            ? ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"]
            : ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"];

        let currentMonth = -1;

        data.forEach((day, index) => {
            const date = new Date(day.date);
            const month = date.getMonth();

            // 每次遇到新的月份时添加标记
            if (month !== currentMonth) {
                currentMonth = month;
                months.push({ x: Math.floor(index / daysInWeek) * weekWidth, name: monthNames[month] });
            }
        });

        return months;
    };

    const months = generateMonths();

    return (
        <svg width={activityGrid.length * weekWidth + textOffset} height={daysInWeek * dayHeight + textOffset}>
            {/* 月份标记 */}
            {months.map((month, index) => (
                <text key={index} x={month.x + textOffset} y={textOffset - 5} fontSize="10" fill="#666">
                    {month.name}
                </text>
            ))}

            {/* 星期标记 */}
            {(getRes()["lang"] === "en" ? ["Mon", "Wed", "Fri"] : ["一", "三", "五"]).map((label, index) => (
                <text
                    key={index}
                    x={textOffsetX - 5}
                    y={textOffsetY + (index * 2 + 1) * dayHeight - 9}
                    fontSize="7"
                    fill="#666"
                    textAnchor="end" // 文字右对齐
                    dominantBaseline="middle" // 垂直居中
                >
                    {label}
                </text>
            ))}

            {/* 活动格子 */}
            {activityGrid.map((week, weekIndex) =>
                week.map((day, dayIndex) => (
                    <rect
                        key={`${weekIndex}-${dayIndex}`}
                        x={weekIndex * weekWidth + textOffset}
                        y={dayIndex * dayHeight + textOffset}
                        width={cellSize}
                        height={cellSize}
                        rx={2} // 圆角
                        ry={2} // 圆角
                        fill={getColor(day.count)}
                        stroke="#ccc"
                    >
                        <title>{day.date ? `${day.date}. ${getRes()["article"]}: ${day.count}` : "No data"}</title>
                    </rect>
                ))
            )}
        </svg>
    );
};

// 修改后的 generateCompleteData 函数，用于补全数据
export const generateCompleteData = (partialData: ActivityDay[]): ActivityDay[] => {
    const today = new Date();
    const startDate = new Date(today);
    startDate.setFullYear(today.getFullYear() - 1);
    startDate.setDate(1);

    const completeData: ActivityDay[] = [];
    const partialDataMap = new Map(partialData.map((item) => [item.date, item.count]));

    const currentDate = startDate;
    while (currentDate <= today) {
        const dateStr = currentDate.toISOString().split("T")[0];
        completeData.push({
            date: dateStr,
            count: partialDataMap.get(dateStr) || 0, // 填充缺失数据为 0
        });
        currentDate.setDate(currentDate.getDate() + 1);
    }

    return completeData;
};

export default ActivityGraph;
