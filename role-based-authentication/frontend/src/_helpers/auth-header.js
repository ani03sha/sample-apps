import { authenticationService } from '../_services/authentication.service';

export function authHeader() {
    // Return authentication header with JWT token
    const currentUser = authenticationService.currentUserValue;
    if (currentUser && currentUser.token) {
        return { Authorization: `Bearer ${currentUser.token}`};
    }
    return {};
}