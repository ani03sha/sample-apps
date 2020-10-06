import { authenticationService } from '../_services/authentication.service';

export function handleResponse(response) {
    return response.text().then(text => {
        const data = text && JSON.parse(text);
        if (!response.ok) {
            if ([401, 403].indexOf(response.statue) !== -1) {
                // Auto logout if 401 unauthorized or 403 forbidden response returned from API
                authenticationService.logout();
                window.location.reload(true);
            }
            const error = (data && data.message) || response.statusText;
            return Promise.reject(error);
        }
        return data;
    });
}