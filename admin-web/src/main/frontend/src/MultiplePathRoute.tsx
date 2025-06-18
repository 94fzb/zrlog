import {FunctionComponent, ReactNode} from "react";
import {Route} from "react-router-dom";

export type MultiplePathRouterProps = {
    path: string[];
    element?: ReactNode;
}

const MultiplePathRoute: FunctionComponent<MultiplePathRouterProps> = ({path, element}) => {

    return <>
        {path.map((e) => {
            return <Route key={e} path={e} element={element}/>
        })}
    </>
}

export default MultiplePathRoute;