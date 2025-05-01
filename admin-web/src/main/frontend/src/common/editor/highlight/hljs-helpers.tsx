import flowchart from 'flowchart.js';
import hljs from 'highlight.js/lib/common';
import shell from 'highlight.js/lib/languages/shell';
import {createRoot, Root} from "react-dom/client";

//@ts-ignore
import SequenceDiagram from 'react-sequence-diagram';
import {marked} from "marked";

const renderer = new marked.Renderer();

const md5 = require("md5");

const renderMap = new Map<Element, Root>();

hljs.registerLanguage("shell", shell)

export const getCodeLanguages = (): Record<string, string[]> => {
    const records: Record<string, string[]> = {};
    hljs.listLanguages().forEach(lang => {
        const language = hljs.getLanguage(lang);
        if (language) {
            records[lang] = [language.name as string];
        }
    })
    return records;
};

function renderDiagramReact(el: Element, content: JSX.Element) {
    let root = renderMap.get(el);
    if (!root) {
        root = createRoot(el);
        renderMap.set(el, root);
    }

    root.render(content);
}

renderer.code = function ({text, lang}) {
    const validLang = lang && hljs.getLanguage(lang) ? lang : '';
    if (validLang) {
        const highlighted = hljs.highlight(text, {language: validLang}).value;
        return `<pre><code class="hljs language-${validLang}">${highlighted}</code></pre>`;
    } else if (lang === "flow") {
        const id = 'flow_' + md5(text);
        // 异步绘图，先返回占位 div
        setTimeout(() => {
            const container = document.getElementById(id);
            if (container?.innerHTML != "") {
                return;
            }
            if (container) {
                try {

                    const chart = flowchart.parse(text);
                    chart.drawSVG(id);
                } catch (err) {
                    container.innerHTML = `<pre style="color:red">${String(err)}</pre>`;
                }
            }
        }, 10);
        return `<div id="${id}" class="flow"></div>`;
    } else if (lang === "seq") {
        const id = 'seq_' + md5(text);
        // 异步绘图，先返回占位 div
        setTimeout(() => {
            const container = document.getElementById(id);
            if (container) {
                const options = {
                    theme: 'simple'
                };
                try {
                    renderDiagramReact(container, <SequenceDiagram input={text} options={options}/>)
                } catch (err) {
                    container.innerHTML = `<pre style="color:red">${String(err)}</pre>`;
                }
            }
        }, 10);
        return `<div id="${id}" class="seq"></div>`;
    }
    const highlighted = hljs.highlightAuto(text).value;
    return `<pre><code class="hljs">${highlighted}</code></pre>`;
};


marked.setOptions({
    gfm: true,
    breaks: true,
    renderer
}); // ✅ 这样确保类型对得上

