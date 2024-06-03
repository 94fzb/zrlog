import UnknownErrorPage from "../components/unknown-error-page";
import { FunctionComponent, PropsWithChildren } from "react";

const Offline: FunctionComponent<PropsWithChildren> = () => {
    return <UnknownErrorPage data={{ message: "Current offline page" }} code={"500"} />;
};

export default Offline;
