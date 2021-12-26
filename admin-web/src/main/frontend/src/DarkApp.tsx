import './theme.dark.less';
import Loadable from "react-loadable";
import {MyLoadingComponent} from "./components/my-loading-component";

const AsyncAppBase = Loadable({
    loader: () => import("./AppBase"),
    loading: MyLoadingComponent
});

export default AsyncAppBase;
