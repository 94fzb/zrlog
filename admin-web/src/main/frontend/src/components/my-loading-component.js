import UnknownErrorPage from "./unknown-error-page";
import React from "react";

export const MyLoadingComponent = ({isLoading, error}) => {
    if (isLoading) {
        return <div/>;
    } else if (error) {
        console.info(error);
        return <UnknownErrorPage message={error.toString()}/>;
    } else {
        return null;
    }
};

export default MyLoadingComponent;
