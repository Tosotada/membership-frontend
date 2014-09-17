define([
    'src/utils/atob',
    'ajax',
    'src/utils/cookie',
    'config/appCredentials'
], function(AtoB, ajax, cookie, appCredentials){

    var MEM_USER_COOKIE_KEY = appCredentials.membership.userCookieKey;

    var isLoggedIn = function(){
        return !!getUserFromCookie();
    };

    var getUserFromCookie = function(){
        var userFromCookieCache = null;

        function readCookie(name){
            var nameEQ = name + '=';
            var ca = document.cookie.split(';');
            for (var i = 0; i < ca.length; i++) {
                var c = ca[i];
                while (c.charAt(0) === ' ') { c = c.substring(1, c.length); }
                if (c.indexOf(nameEQ) === 0) { return c.substring(nameEQ.length, c.length); }
            }
            return null;
        }

        function decodeBase64(str){
            return decodeURIComponent(escape(new AtoB()(str.replace(/-/g, '+').replace(/_/g, '/').replace(/,/g, '='))));
        }

        if (userFromCookieCache === null) {
            var cookieData = readCookie('GU_U'),
                userData = cookieData ? JSON.parse(decodeBase64(cookieData.split('.')[0])) : null;
            if (userData) {
                userFromCookieCache = {
                    id: userData[0],
                    primaryemailaddress: userData[1], // this and displayname are non camelcase to match with formstack
                    displayname: userData[2],
                    accountCreatedDate: userData[6],
                    emailVerified: userData[7],
                    rawResponse: cookieData
                };
            }
        }

        return userFromCookieCache;
    };

    var requestMemberDetail = (function () {

        var queue = [];

        return {
            request: function (callback) {

                var self = this;

                if (callback) {

                    queue.push(callback);
                    if (queue.length === 1) self.request.apply(self);

                } else if (queue.length) {

                    var queuePop = function () {
                        var cb = queue.shift();
                        self.request.apply(self);
                        cb.apply(this, arguments);
                    };

                    getMemberDetail(queuePop);
                }
            }
        };

    })();

    /**
     * get the membership user details.
     * This will call '/user/me' if a valid identity user is logged in and the membership cookie is not stored and the
     * membershipUserId does not match the identity userId,
     * If the identity member does not have a membership tier then the cookie is stored with the identity user id,
     * If the identity member does have a membership tier then the membership details are stored in the membershipUser
     * cookie.
     * If membership cookie exists and matches identity credentials then this is used over preference of calling
     * '/user/me'
     * @param callback
     */
    var getMemberDetail = (function () {

        var pendingXHR;

        var callbacks = [];

        return function (callback) {

            var membershipUser = cookie.getCookie(MEM_USER_COOKIE_KEY);
            var identityUser = getUserFromCookie();

            if (identityUser) {
                if (membershipUser && membershipUser.userId === identityUser.id) {
                    callback(membershipUser);
                } else {
                    if (!pendingXHR) {
                        cookie.removeCookie(MEM_USER_COOKIE_KEY);

                        pendingXHR = ajax({
                            url: '/user/me',
                            method: 'get'
                        }).then(function (resp) {
                            cookie.setCookie(MEM_USER_COOKIE_KEY, resp);
                            callbacks.forEach(function (callback) { callback(resp); });
                        }).fail(function (err) {
                            if (err.status === 403) {
                                cookie.setCookie(MEM_USER_COOKIE_KEY, { userId: identityUser.id });
                            }
                            callbacks.forEach(function (callback) { callback(null, err); });
                        }).always(function () {
                            pendingXHR = undefined;
                        });
                    }

                    callbacks.push(callback);
                }
            } else {
                callback(null, { message: 'no membership user' });
            }
        };

    })();

    return {
        isLoggedIn: isLoggedIn,
        getUserFromCookie: getUserFromCookie,
        getMemberDetail: getMemberDetail,
        requestMemberDetail: requestMemberDetail
    };
});
