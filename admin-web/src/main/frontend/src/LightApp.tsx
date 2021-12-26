import {MyLoadingComponent} from "./components/my-loading-component";
import './theme.light.less';
import Loadable from "react-loadable";

const AsyncLightApp = Loadable({
    loader: () => import("./AppBase"),
    loading: MyLoadingComponent
});

export default AsyncLightApp;
