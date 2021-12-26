import UnknownErrorPage from "./unknown-error-page";

export const MyLoadingComponent = ({isLoading, error}: { isLoading: boolean, error: any }) => {
    if (isLoading) {
        return <div/>;
    } else if (error) {
        console.error(error);
        return <UnknownErrorPage message={error.toString()}/>;
    } else {
        return null;
    }
};

export default MyLoadingComponent;
