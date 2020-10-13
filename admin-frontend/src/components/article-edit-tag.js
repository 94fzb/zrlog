import React from "react";

import {Input, Tag} from 'antd';
import {TweenOneGroup} from 'rc-tween-one';
import {PlusOutlined, TagOutlined} from '@ant-design/icons';
import Divider from "antd/lib/divider";
import {BaseResourceComponent} from "./base-resource-component";

export class ArticleEditTag extends BaseResourceComponent {

    state = {
        tags: this.props.tags,
        allTags: this.props.allTags
    }

    initState() {
        return {
            inputVisible: false,
            inputValue: '',
            tags: [],
            allTags: []
        }
    }

    handleClose = removedTag => {
        const tags = this.tags.filter(tag => tag !== removedTag);
        console.log(tags);
        this.setState({tags});
    };

    showInput = () => {
        this.setState({inputVisible: true}, () => this.input.focus());
    };

    handleInputChange = e => {
        this.setState({inputValue: e.target.value});
    };

    handleInputConfirm = () => {
        const {inputValue} = this.state;
        let {tags} = this.state;
        if (inputValue && tags.indexOf(inputValue) === -1) {
            tags = [...tags, inputValue];
        }
        console.log(tags);
        this.setState({
            tags,
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

    }

    tagForMap = tag => {
        const tagElem = (
            <Tag icon={<TagOutlined/>} onAuxClick={this.allTagsOnClick()} closable={false} color="#108ee9">
                {tag}
            </Tag>
        );
        return (
            <span key={tag} style={{display: 'inline-block'}}>
        {tagElem}
      </span>
        );
    };

    render() {
        const {inputVisible, allTags, tags, inputValue} = this.state;
        const tagChild = this.props.tags.map(this.forMap);
        const allTagChild = this.props.allTags.map(this.tagForMap);
        return (
            <>
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
                        {tagChild}
                    </TweenOneGroup>

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
                        <Tag onClick={this.showInput} className="site-tag-plus">
                            <PlusOutlined/> New Tag
                        </Tag>
                        <Divider/>
                        <div style={{maxHeight: "120px", overflowY: "scroll"}}>
                            {allTagChild}
                        </div>
                    </>

                )}

            </>
        );
    }
}