import * as serviceWorker from "./serviceWorker";
import { createRoot } from "react-dom/client";
import SsData from "./base/SsData";
import ConfigProviderApp from "./base/ConfigProviderApp";

const Index = () => {
    return (
        <SsData>
            <ConfigProviderApp />
        </SsData>
    );
};

const container = document.getElementById("app");
const root = createRoot(container!); // createRoot(container!) if you use TypeScript
root.render(<Index />);
// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.register();
