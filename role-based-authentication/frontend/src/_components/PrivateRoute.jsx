import React from 'react';
import { Route, Redirect } from 'react-router-dom';

import { authenticationService } from '../_services/authentication.service';

export const PrivateRoute = ({ component: Component, roles, ...rest }) => (
    <Route {...rest} render={props => {
        const currentUser = authenticationService.currentUserValue;
        if (!currentUser) {
            // Not logged in so redirect to login page with the return url
            return <Redirect to={{ pathname: '/login', state: { from: props.location } }} />
        }
        // Check if route is restricted by role
        if (roles && roles.indexOf(currentUser.role) === -1) {
            // role not authorized so redirect to home page
            return <Redirect to={{ pathname: '/' }} />
        }
        // Authorized so return component
        return <Component {...props} />
    }} />
)