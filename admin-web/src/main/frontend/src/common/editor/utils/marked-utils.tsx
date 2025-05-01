import { renderTex } from "./katex-helpers";
import { marked } from "marked";

import flowchart from "flowchart.js";

//@ts-ignore
import SequenceDiagram from "react-sequence-diagram";
import { createRoot, Root } from "react-dom/client";

const renderMap = new Map<Element, Root>();

function renderDiagramReact(el: Element, content: JSX.Element) {
    let root = renderMap.get(el);
    if (!root) {
        root = createRoot(el);
        renderMap.set(el, root);
    }
    root.render(content);
}

const createHideElement = () => {
    const hiddenContainer = document.createElement("div");
    hiddenContainer.style.position = "absolute";
    hiddenContainer.style.left = "-9999px";
    hiddenContainer.style.top = "-9999px";
    hiddenContainer.style.visibility = "hidden";
    return hiddenContainer;
};

function renderSequenceWithObserver(el: Element, code: string): Promise<void> {
    return new Promise((resolve) => {
        const hiddenContainer = createHideElement();
        document.body.appendChild(hiddenContainer);

        renderDiagramReact(hiddenContainer, <SequenceDiagram input={code} options={{ theme: "simple" }} />);

        const observer = new MutationObserver(() => {
            el.innerHTML = hiddenContainer.innerHTML;
            observer.disconnect();
            resolve();
        });

        observer.observe(hiddenContainer, { childList: true, subtree: true });

        setTimeout(() => {
            observer.disconnect();
            resolve(); // fallback 超时，避免卡死
        }, 1000);
    });
}

async function hydrateCodeBlocks(virtualElement: HTMLElement) {
    virtualElement.querySelectorAll(".flow").forEach((div) => {
        const code = decodeURIComponent((div as HTMLDivElement).dataset.code || "");
        try {
            const hiddenContainer = createHideElement();
            document.body.appendChild(hiddenContainer);
            const chart = flowchart.parse(code);
            chart.drawSVG(hiddenContainer);
            div.innerHTML = hiddenContainer.innerHTML;
            document.body.removeChild(hiddenContainer);
        } catch (err) {
            div.innerHTML = `<pre style="color:red">${String(err)}</pre>`;
        }
    });

    const tasks: Promise<void>[] = [];
    virtualElement.querySelectorAll(".seq").forEach((div) => {
        const code = decodeURIComponent((div as HTMLDivElement).dataset.code || "");
        try {
            tasks.push(renderSequenceWithObserver(div, code));
        } catch (err) {
            div.innerHTML = `<pre style="color:red">${String(err)}</pre>`;
        }
    });
    await Promise.all(tasks);
}

export const markdownToHtml = async (markdownValue: string) => {
    const text = marked(markdownValue) as string;
    const rawHtml = renderTex(text);
    // 创建离屏容器（不挂载到页面）
    const container = document.createElement("div");
    container.innerHTML = rawHtml;
    await hydrateCodeBlocks(container);
    return container.innerHTML;
};
