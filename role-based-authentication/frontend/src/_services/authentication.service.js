import { BehaviorSubject } from 'rxjs';

import config from 'config';
import { handleResponse } from '../_helpers/handle-response';

const currentUserSubject = new BehaviorSubject(JSON.parse(localStorage.getItem('currentUser')));

export const authenticationService = {
    login,
    logout,
    currentUser: currentUserSubject.asObservable(),
    get currentUserValue () { return currentUserSubject.value }
};

function login(username, password) {
    const requestOptions = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json'},
        body: JSON.stringify({ username, password })
    };
    return fetch(`${config.apiUrl}/users/authenticate`, requestOptions)
        .then(handleResponse)
        .then(user => {
            // Store user details and JWT token in local storage to keep
            // user logged in between page refreshes
            localStorage.setItem('currentUser', JSON.stringify(user));
            currentUserSubject.next(user);
            return user;
        });
}

function logout() {
    // Remove user from local storage on logout
    localStorage.removeItem('currentUser');
    currentUserSubject.next(null);
}