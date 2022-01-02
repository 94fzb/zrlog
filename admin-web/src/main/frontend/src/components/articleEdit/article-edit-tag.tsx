import {Input, Tag} from 'antd';
import {TweenOneGroup} from 'rc-tween-one';
import {PlusOutlined, TagOutlined} from '@ant-design/icons';
import Title from "antd/es/typography/Title";
import {FunctionComponent, useRef, useState} from "react";
import {getRes} from "../../utils/constants";


type ArticleEditTagProps = {
    allTags: string[],
    keywords: string,
}

type ArticleEditTagState = {
    keywords: string,
    inputVisible: boolean,
    inputValue: string,
}

const ArticleEditTag: FunctionComponent<ArticleEditTagProps> = ({allTags, keywords}) => {

    const [state, setState] = useState<ArticleEditTagState>({
        keywords: "",
        inputVisible: false,
        inputValue: ''
    })

    const inputRef = useRef(null);

    const handleClose = (removedTag: string) => {
        const tags = state.keywords.split(",").filter(tag => tag !== removedTag);
        console.info(tags.join(","));
        setState({
            ...state,
            keywords: tags.join(",")
        })
    };

    const showInput = () => {
        setState({...state, inputVisible: true});
    };

    const handleInputChange = (e: any) => {
        setState({...state, inputValue: e.target.value});
    };

    const handleInputConfirm = () => {
        const {inputValue} = state;
        let {keywords} = state;
        if (inputValue) {
            if (keywords) {
                keywords = keywords += "," + inputValue;
            } else {
                keywords = inputValue;
            }
        }
        console.log(keywords);
        setState({
            keywords: keywords,
            inputVisible: false,
            inputValue: '',
        });
    };

    const forMap = (tag: string) => {
        const tagElem = (
            <Tag
                icon={<TagOutlined/>}
                color="#108ee9"
                closable
                onClose={e => {
                    e.preventDefault();
                    handleClose(tag);
                }}
            >
                {tag}
            </Tag>
        );
        return (
            <span key={tag} style={{display: 'inline-block'}}>
        {tagElem}
      </span>
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
        setState({
            ...state,
            keywords: tags.join(',')
        })
    }

    const tagForMap = (tag: string) => {
        const tagElem = (
            <Tag icon={<TagOutlined/>} onClick={(e) => allTagsOnClick(e)} closable={false} color="#108ee9">
                {tag}
            </Tag>
        );
        return (
            <span key={"all-" + tag} style={{display: 'inline-block'}}>
        {tagElem}
      </span>
        );
    };

    const {inputVisible, inputValue} = state;
    let tagChild;
    if (state.keywords === '') {
        if (keywords != null) {
            state.keywords = keywords;
        }
    }
    if (state.keywords !== undefined && state.keywords != null) {
        const newTags = Array.from(new Set(state.keywords.split(",").filter(x => x !== '')));
        state.keywords = newTags.join(",");
        tagChild = newTags.map(forMap);
    } else {
        state.keywords = '';
        tagChild = [].map(forMap);
    }
    const allTagChild = allTags.map(tagForMap);
    return (
        <>
            <Input id='keywords' value={state.keywords} hidden={true}/>
            <div style={{marginBottom: 16}}>
                <TweenOneGroup
                    enter={{
                        scale: 0.8,
                        opacity: 0,
                        type: 'from',
                        duration: 100,
                    }}
                    leave={{opacity: 0, width: 0, scale: 0, duration: 200}}
                    appear={false}
                >

                </TweenOneGroup>
                {tagChild}

            </div>
            {inputVisible && (
                <Input
                    ref={inputRef}
                    type="text"
                    size="small"
                    style={{width: 78}}
                    value={inputValue}
                    onChange={handleInputChange}
                    onBlur={handleInputConfirm}
                    onPressEnter={handleInputConfirm}
                />
            )}
            {!inputVisible && (
                <>
                    <Tag onClick={showInput}>
                        <PlusOutlined/> {getRes()['tagTips']}
                    </Tag>
                    <Title level={5} style={{paddingTop: "15px"}}>{getRes()['allTag']}</Title>
                    <div style={{maxHeight: "120px", overflowY: "scroll"}}>
                        {allTagChild}
                    </div>
                </>

            )}

        </>
    );
}
export default ArticleEditTag;