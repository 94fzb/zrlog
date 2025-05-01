import hljs from "highlight.js/lib/common";
import shell from "highlight.js/lib/languages/shell";
import { marked } from "marked";

const renderer = new marked.Renderer();

const md5 = require("md5");

hljs.registerLanguage("shell", shell);

export const getCodeLanguages = (): Record<string, string[]> => {
    const records: Record<string, string[]> = {};
    hljs.listLanguages().forEach((lang) => {
        const language = hljs.getLanguage(lang);
        if (language) {
            records[lang] = [language.name as string];
        }
    });
    return records;
};

renderer.code = function ({ text, lang }) {
    const validLang = lang && hljs.getLanguage(lang) ? lang : "";
    if (validLang) {
        const highlighted = hljs.highlight(text, { language: validLang }).value;
        return `<pre><code class="hljs language-${validLang}">${highlighted}</code></pre>`;
    } else if (lang === "flow") {
        const id = "flow_" + md5(text);
        // 返回占位 div
        return `<div id="${id}"  data-code="${encodeURIComponent(text)}" class="flow"></div>`;
    } else if (lang === "seq") {
        const id = "seq_" + md5(text);
        // 返回占位 div
        return `<div id="${id}" data-code="${encodeURIComponent(text)}" class="seq"></div>`;
    }
    const highlighted = hljs.highlightAuto(text).value;
    return `<pre><code class="hljs">${highlighted}</code></pre>`;
};

marked.setOptions({
    gfm: true,
    breaks: true,
    renderer,
}); // ✅ 这样确保类型对得上
