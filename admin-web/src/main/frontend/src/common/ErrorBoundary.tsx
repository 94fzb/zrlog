import { useState, useEffect, FunctionComponent, PropsWithChildren } from "react";
import UnknownErrorPage from "../components/unknown-error-page";

const ErrorBoundary: FunctionComponent<PropsWithChildren> = ({ children }) => {
    const [hasError, setHasError] = useState(false);

    useEffect(() => {
        const handleError = () => {
            setHasError(true);
        };

        window.addEventListener("error", handleError);

        return () => {
            window.removeEventListener("error", handleError);
        };
    }, []);

    if (hasError) {
        return <UnknownErrorPage data={{ message: "Something went wrong. Please refresh the page." }} code={"500"} />;
    }

    return <>{children}</>;
};

export default ErrorBoundary;
