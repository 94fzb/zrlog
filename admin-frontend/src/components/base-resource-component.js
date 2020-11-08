import React from "react";
import axios from "axios";

export class BaseResourceComponent extends React.Component {

    constructor(props) {
        super(props);
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
        this.fetchRes();
    }

    getSpinDelayTime() {
        return 200;
    }

    handleRes(data){
        data.data.copyrightTips = data.data.copyright + ' ZrLog';
        this.setState({
            res: data.data,
            resLoading: false
        },() => {
            this.fetchResSuccess(data.data);
            document.title = [this.getSecondTitle(), data.data['admin.management'], this.state.res.websiteTitle].filter(Boolean).join(" | ");
        });
    }

    fetchRes() {
        const resourceKey = "commonRes";
        const resourceData = window.sessionStorage.getItem(resourceKey);
        if(resourceData === null) {
            axios.get('/api/public/resource').then(({data}) => {
                this.handleRes(data);
                window.sessionStorage.setItem(resourceKey, JSON.stringify(data));
            })
        }else{
            this.handleRes(JSON.parse(resourceData));
        }
    }

    getSecondTitle() {
        return null;
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

}
