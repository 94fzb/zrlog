import { Input, Space, Tag } from "antd";
import { TweenOneGroup } from "rc-tween-one";
import { PlusOutlined, TagOutlined } from "@ant-design/icons";
import Title from "antd/es/typography/Title";
import { FunctionComponent, useState } from "react";
import { getColorPrimary, getRes } from "../../utils/constants";

type ArticleEditTagProps = {
    allTags: string[];
    keywords: string;
    onKeywordsChange: (text: string) => void;
};

type ArticleEditTagState = {
    keywords: string;
    inputVisible: boolean;
    inputValue: string;
};

const ArticleEditTag: FunctionComponent<ArticleEditTagProps> = ({ allTags, keywords, onKeywordsChange }) => {
    const [state, setState] = useState<ArticleEditTagState>({
        keywords: "",
        inputVisible: false,
        inputValue: "",
    });

    const handleClose = (removedTag: string) => {
        const tags = state.keywords.split(",").filter((tag) => tag !== removedTag);
        const nowKeywords = tags.join(",");
        setState({
            ...state,
            keywords: nowKeywords,
        });
        onKeywordsChange(nowKeywords);
    };

    const showInput = () => {
        setState({ ...state, inputVisible: true });
    };

    const handleInputChange = (e: any) => {
        setState({ ...state, inputValue: e.target.value });
    };

    const handleInputConfirm = () => {
        const { inputValue } = state;
        let { keywords } = state;
        if (inputValue) {
            if (keywords) {
                keywords = keywords += "," + inputValue;
            } else {
                keywords = inputValue;
            }
        }
        //console.log(keywords);
        setState({
            keywords: keywords,
            inputVisible: false,
            inputValue: "",
        });
        onKeywordsChange(keywords);
    };

    const forMap = (tag: string) => {
        return (
            <Tag
                icon={<TagOutlined />}
                color={getColorPrimary()}
                closable
                onClose={(e) => {
                    e.preventDefault();
                    handleClose(tag);
                }}
            >
                {tag}
            </Tag>
        );
    };

    const allTagsOnClick = (e: any) => {
        e.currentTarget.remove();
        let tags: any[];
        if (state.keywords) {
            tags = state.keywords.split(",");
        } else {
            tags = [];
        }
        tags.push(e.currentTarget.textContent);
        const nowKeywords = tags.join(",");
        setState({
            ...state,
            keywords: nowKeywords,
        });
        onKeywordsChange(nowKeywords);
    };

    const tagForMap = (tag: string) => {
        return (
            <Tag
                key={"all-" + tag}
                icon={<TagOutlined />}
                onClick={(e) => allTagsOnClick(e)}
                closable={false}
                color={getColorPrimary()}
            >
                {tag}
            </Tag>
        );
    };

    const { inputVisible, inputValue } = state;
    let tagChild;
    if (state.keywords === "") {
        if (keywords != null) {
            state.keywords = keywords;
        }
    }
    if (state.keywords !== undefined && state.keywords != null) {
        const newTags = Array.from(new Set(state.keywords.split(",").filter((x) => x !== "")));
        state.keywords = newTags.join(",");
        tagChild = newTags.map(forMap);
    } else {
        state.keywords = "";
        tagChild = [].map(forMap);
    }
    const allTagChild = allTags.map(tagForMap);
    return (
        <>
            <div style={{ marginBottom: 16 }}>
                <TweenOneGroup
                    enter={{
                        scale: 0.8,
                        opacity: 0,
                        type: "from",
                        duration: 100,
                    }}
                    leave={{ opacity: 0, width: 0, scale: 0, duration: 200 }}
                    appear={false}
                />
                <Space size={[0, 8]} wrap>
                    {tagChild}
                </Space>
            </div>
            {inputVisible && (
                <Input
                    type="text"
                    size="small"
                    style={{ width: 98 }}
                    value={inputValue}
                    onChange={handleInputChange}
                    onBlur={handleInputConfirm}
                    onPressEnter={handleInputConfirm}
                />
            )}
            {!inputVisible && (
                <>
                    <Space size={[0, 8]} wrap>
                        <Tag color={getColorPrimary()} onClick={showInput}>
                            <PlusOutlined /> {getRes()["tagTips"]}
                        </Tag>
                    </Space>
                    <Title level={5}>{getRes()["allTag"]}</Title>
                    <div style={{ maxHeight: "120px", overflowY: "scroll" }}>
                        <Space size={[0, 8]} wrap>
                            {allTagChild}
                        </Space>
                    </div>
                </>
            )}
        </>
    );
};
export default ArticleEditTag;
