export const MyLoadingComponent = ({isLoading, error}: { isLoading: boolean; error: any }) => {
    if (isLoading) {
        return <div/>;
    } else if (error) {
        console.error(error);
        return <div>{error.toString()}</div>;
    } else {
        return null;
    }
};

export default MyLoadingComponent;
