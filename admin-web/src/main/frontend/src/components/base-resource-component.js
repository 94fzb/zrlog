import React from "react";
import {Modal} from "antd";
import EnvUtils from "../utils/env-utils";

const axios = require('axios')
const resourceKey = "commonRes.v4";

export class BaseResourceComponent extends React.Component {

    constructor(props) {
        super(props);
        axios.interceptors.response.use((response) => {
            if (response.data.error === 9001) {
                Modal.warn({
                    title: '会话过期',
                    content: response.data.message,
                    okText: '确认'
                });
                return Promise.reject(response.data);
            }
            return response;
        }, (error) => {
            if (error && error.response) {
                if (error.response.status === 502) {
                    Modal.error({
                        title: "服务未启动",
                        content: <div style={{paddingTop: 20}}
                                      dangerouslySetInnerHTML={{__html: error.response.data}}/>,
                        okText: '确认'
                    });
                    return Promise.reject(error.response)
                }
            }
            return Promise.reject(error);
        });
        const basicMap = {
            resLoading: true,
            res: {},
            statisticsInfo: {}
        };
        this.state = {
            ...basicMap,
            ...this.initState()
        }
    }

    initState() {

    }

    fetchResSuccess(res) {

    }

    componentDidMount() {
        this.initRes();
    }

    getSpinDelayTime() {
        return 200;
    }

    handleRes(data) {
        EnvUtils.setTheme(data.data['admin_darkMode'] === true ? "dark" : "light");
        data.data.copyrightTips = data.data.copyright + ' <a target="_blank" href="https://blog.zrlog.com/about?footer">ZrLog</a>';
        this.setState({
            res: data.data,
            resLoading: false
        }, () => {
            this.fetchResSuccess(data.data);
            this.reloadTitle(data.data);
            window.sessionStorage.setItem(resourceKey, JSON.stringify(data));
        });
    }

    reloadTitle(res) {
        document.title = [this.getSecondTitle(), res['admin.management'], this.state.res.websiteTitle].filter(Boolean).join(" | ");
    }

    initRes() {
        const resourceData = window.sessionStorage.getItem(resourceKey);
        if (resourceData === null) {
            const jsonStr = document.getElementById("resourceInfo").textContent;
            if (jsonStr && jsonStr !== '') {
                this.handleRes({"data": JSON.parse(jsonStr)});
            } else {
                this.loadResourceFromServer();
            }
        } else {
            this.handleRes(JSON.parse(resourceData));
        }
    }

    getSecondTitle() {
        return null;
    }

    loadResourceFromServer() {
        const resourceApi = '/api/public/blogResource';
        this.getAxios().get(resourceApi).then(({data}) => {
            this.handleRes(data);
        }).catch(error => {
            Modal.error({
                title: 'Load error',
                content: <div style={{paddingTop: 20}}
                              dangerouslySetInnerHTML={{__html: error.response.data}}/>,
                okText: '确认'
            });
        })
    }

    mapToQueryString(map) {
        return '?' + Object.keys(map).reduce(function (a, k) {
            if (map[k] === undefined) {
                a.push(k + "=");
            } else {
                a.push(k + '=' + encodeURIComponent(map[k]));
            }
            return a;
        }, []).join('&');
    }

    getAxios() {
        return axios;
    }

}
