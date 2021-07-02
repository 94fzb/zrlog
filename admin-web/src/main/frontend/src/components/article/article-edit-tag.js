import React from "react";

import {Input, Tag} from 'antd';
import {TweenOneGroup} from 'rc-tween-one';
import {PlusOutlined, TagOutlined} from '@ant-design/icons';
import {BaseResourceComponent} from "../base-resource-component";
import Title from "antd/es/typography/Title";


export class ArticleEditTag extends BaseResourceComponent {

    initState() {
        return {
            inputVisible: false,
            inputValue: '',
            keywords: this.props.keywords,
            allTags: this.props.allTags
        }
    }

    handleClose = removedTag => {
        const tags = this.state.keywords.split(",").filter(tag => tag !== removedTag);
        console.info(tags.join(","));
        //this.props.tags = tags;
        this.setState({
            keywords: tags.join(",")
        })
    };

    showInput = () => {
        this.setState({inputVisible: true}, () => this.input.focus());
    };

    handleInputChange = e => {
        this.setState({inputValue: e.target.value});
    };

    handleInputConfirm = () => {
        const {inputValue} = this.state;
        let {keywords} = this.state;
        if (inputValue) {
            if (keywords) {
                keywords = keywords += "," + inputValue;
            } else {
                keywords = inputValue;
            }
        }
        console.log(keywords);
        this.setState({
            keywords: keywords,
            inputVisible: false,
            inputValue: '',
        });
    };

    saveInputRef = input => {
        this.input = input;
    };

    forMap = tag => {
        const tagElem = (
            <Tag
                icon={<TagOutlined/>}
                color="#108ee9"
                closable
                onClose={e => {
                    e.preventDefault();
                    this.handleClose(tag);
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

    allTagsOnClick(e) {
        e.currentTarget.remove();
        let tags;
        if (this.state.keywords) {
            tags = this.state.keywords.split(",");
        } else {
            tags = [];
        }
        tags.push(e.currentTarget.textContent);
        this.setState({
            keywords: tags.join(',')
        })
    }

    tagForMap = tag => {
        const tagElem = (
            <Tag icon={<TagOutlined/>} onClick={(e) => this.allTagsOnClick(e)} closable={false} color="#108ee9">
                {tag}
            </Tag>
        );
        return (
            <span key={"all-" + tag} style={{display: 'inline-block'}}>
        {tagElem}
      </span>
        );
    };

    render() {
        const {inputVisible, inputValue} = this.state;
        let tagChild;
        if (this.state.keywords === '') {
            this.state.keywords = this.props.keywords;
        }
        if (this.state.keywords !== undefined && this.state.keywords != null) {
            let newTags = Array.from(new Set(this.state.keywords.split(",").filter(x => x !== '')));
            this.state.keywords = newTags.join(",");
            tagChild = newTags.map(this.forMap);
        } else {
            this.state.keywords = '';
            tagChild = [].map(this.forMap);
        }
        const allTagChild = this.props.allTags.map(this.tagForMap);
        return (
            <>
                <Input id='keywords' value={this.state.keywords} hidden={true}/>
                <div style={{marginBottom: 16}}>
                    <TweenOneGroup
                        enter={{
                            scale: 0.8,
                            opacity: 0,
                            type: 'from',
                            duration: 100,
                            onComplete: e => {
                                e.target.style = '';
                            },
                        }}
                        leave={{opacity: 0, width: 0, scale: 0, duration: 200}}
                        appear={false}
                    >

                    </TweenOneGroup>
                    {tagChild}

                </div>
                {inputVisible && (
                    <Input
                        ref={this.saveInputRef}
                        type="text"
                        size="small"
                        style={{width: 78}}
                        value={inputValue}
                        onChange={this.handleInputChange}
                        onBlur={this.handleInputConfirm}
                        onPressEnter={this.handleInputConfirm}
                    />
                )}
                {!inputVisible && (
                    <>
                        <Tag onClick={this.showInput}>
                            <PlusOutlined/> {this.state.res['tagTips']}
                        </Tag>
                        <Title level={5} style={{paddingTop: "15px"}}>{this.state.res['allTag']}</Title>
                        <div style={{maxHeight: "120px", overflowY: "scroll"}}>
                            {allTagChild}
                        </div>
                    </>

                )}

            </>
        );
    }
}
