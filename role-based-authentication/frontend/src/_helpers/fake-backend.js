import { Role } from './';

export function configureFakeBackend() {
    let users = [
        { id: 1, username: 'admin', password: 'admin', firstName: 'Admin', lastName: 'User', role: Role.Admin },
        { id: 2, username: 'user', password: 'user', firstName: 'Normal', lastName: 'User', role: Role.User }
    ];
    let realFetch = window.fetch;
    window.fetch = function (url, opts) {
        const authHeader = opts.headers['Authorization'];
        const isLoggedIn = authHeader && authHeader.startsWith('Bearer fake-jwt-token');
        const roleString = isLoggedIn && authHeader.split('.')[1];
        const role = roleString ? Role[roleString] : null;

        return new Promise((resolve, reject) => {
            // Wrap in timeout to simulate server API call
            setTimeout(() => {
                if (url.endsWith('/users/authenticate') && opts.method === 'POST') {
                    const params = JSON.parse(opts.body);
                    const user = users.find(x => x.username === params.username && x.password === params.password);
                    if (!user) {
                        return error('Username or password is incorrect');
                    }
                    return ok({
                        id: user.id,
                        username: user.username,
                        firstName: user.firstName,
                        lastName: user.lastName,
                        role: user.role,
                        token: `fake-jwt-token.${user.role}`
                    });
                }
                // Get user by id - admin or user (user can only access their own record)
                if (url.match(/\/users\/\d+$/) && opts.method === 'GET') {
                    if (!isLoggedIn) {
                        return unauthorized();
                    }
                    // Get ID from request URL
                    let urlParts = url.split('/');
                    let id = parseInt(urlParts[urlParts.length - 1]);
                    // Only allow normal users to access their own records
                    const currentUser = users.find(x => x.role === role);
                    if (id !== currentUser.id && role != Role.Admin) {
                        return unauthorized();
                    }
                    const user = users.find(x => x.id === id);
                    return ok(user);
                }
                // Get all users - admin only
                if (url.endsWith('/users') && opts.method === 'GET') {
                    if (role != Role.Admin) {
                        return unauthorized();
                    }
                    return ok(users);
                }
                // Pass through any request not handled above
                realFetch(url, opts).then(response => resolve(response));
                // Private helper functions
                function ok(body) {
                    resolve({
                        ok: true,
                        text: () => Promise.resolve(JSON.stringify(body))
                    })
                }
                function unauthorized() {
                    resolve({
                        status: 401,
                        text: Promise.resolve(JSON.stringify({ message: 'Unauthorized ' }))
                    })
                }
                function error(message) {
                    resolve({
                        status: 400,
                        text: () => Promise.resolve(JSON.stringify({ message }))
                    })
                }
            }, 500);
        });
    }
}