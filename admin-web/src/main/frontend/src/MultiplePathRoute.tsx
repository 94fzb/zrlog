import React from "react";
import {FunctionComponent, ReactNode} from "react";
import {Route} from "react-router-dom";

export type MultiplePathRouterProps = {
    path: string[];
    element?: ReactNode;
}

const MultiplePathRoute: FunctionComponent<MultiplePathRouterProps> = ({path, element}) => {

    return <React.Fragment>
        {path.map((e, i) => {
            return <Route key={e + "" + i} path={e} element={<div key={i}>{element}</div>}/>
        })}
    </React.Fragment>
}

export default MultiplePathRoute;