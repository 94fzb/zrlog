import UnknownErrorPage from "../components/unknown-error-page";
import { FunctionComponent, PropsWithChildren } from "react";

const Offline: FunctionComponent<PropsWithChildren> = () => {
    return <UnknownErrorPage data={{ message: "Network offline" }} code={"500"} />;
};

export default Offline;
