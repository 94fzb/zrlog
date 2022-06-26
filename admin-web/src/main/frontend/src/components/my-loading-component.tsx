export const MyLoadingComponent = ({ isLoading }: { isLoading: boolean }) => {
    if (isLoading) {
        return <div />;
    } /*if (error) {
        console.error(error);
        return <UnknownErrorPage message={error.toString()} />;
    } else */ else {
        return null;
    }
};

export default MyLoadingComponent;
