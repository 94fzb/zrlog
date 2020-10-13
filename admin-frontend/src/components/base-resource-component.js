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

    componentDidMount() {
        this.fetchRes();
    }

    fetchRes() {
        axios.get('/api/public/resource').then(({data}) => {
            data.data.copyrightTips = data.data.copyright + ' ZrLog';
            this.setState({
                res: data.data,
                resLoading: false
            });
            document.title = [this.getSecondTitle(), data.data['admin.management'], this.state.res.websiteTitle].filter(Boolean).join(" | ");
        })
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