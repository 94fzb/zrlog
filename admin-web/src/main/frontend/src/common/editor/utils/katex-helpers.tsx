import katex from "katex";
import "katex/dist/katex.min.css";

const texInline = /\$(.+?)\$/g;
const texBlock = /\$\$([\s\S]+?)\$\$/g;

export const renderTex = (text: string) => {
    // 替换块级公式
    text = text.replace(texBlock, (_, expr) => katex.renderToString(expr, { displayMode: true, throwOnError: false }));
    // 替换行内公式
    text = text.replace(texInline, (_, expr) =>
        katex.renderToString(expr, { displayMode: false, throwOnError: false })
    );
    return text;
};
