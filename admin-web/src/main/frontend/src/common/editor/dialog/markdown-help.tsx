import { Content } from "antd/es/layout/layout";
import EnvUtils from "../../../utils/env-utils";
import { marked } from "marked";
import { StyledPreview } from "../styled-preview";

const helpMd = `
### Markdown语法教程 (Markdown syntax tutorial)

- [Markdown Syntax](http://daringfireball.net/projects/markdown/syntax/ "Markdown Syntax")
- [Mastering Markdown](https://guides.github.com/features/mastering-markdown/ "Mastering Markdown")
- [Markdown Basics](https://help.github.com/articles/markdown-basics/ "Markdown Basics")
- [GitHub Flavored Markdown](https://help.github.com/articles/github-flavored-markdown/ "GitHub Flavored Markdown")
- [Markdown 语法说明（简体中文）](http://www.markdown.cn/ "Markdown 语法说明（简体中文）")
- [Markdown 語法說明（繁體中文）](http://markdown.tw/ "Markdown 語法說明（繁體中文）")

### 流程图参考 (Flowchart reference)

[http://adrai.github.io/flowchart.js/](http://adrai.github.io/flowchart.js/)

### 时序图参考 (SequenceDiagram reference)

[http://bramp.github.io/js-sequence-diagrams/](http://bramp.github.io/js-sequence-diagrams/)

### TeX/LaTeX reference

[http://meta.wikimedia.org/wiki/Help:Formula](http://meta.wikimedia.org/wiki/Help:Formula)
`;

const MarkdownHelp = () => {
    const helpHtml = marked(helpMd);
    return (
        <StyledPreview style={{ borderRadius: 8 }}>
            <Content
                className={"markdown-body"}
                style={{ padding: 0, background: EnvUtils.isDarkMode() ? "#202020" : "#FFFFFF" }}
                dangerouslySetInnerHTML={{ __html: helpHtml }}
            />
        </StyledPreview>
    );
};

export default MarkdownHelp;
