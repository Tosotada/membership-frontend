/*global guardian:true */
define([
    'src/utils/cookie',
    'src/modules/analytics/ga',
    'src/modules/analytics/ophan',
    'src/modules/analytics/krux',
    'src/modules/analytics/facebook',
    'src/modules/analytics/uet',
    'src/modules/analytics/campaignCode'
], function (
    cookie,
    ga,
    ophan,
    krux,
    facebook,
    uet,
    campaignCode
) {
    'use strict';

    /*
     Re: https://bugzilla.mozilla.org/show_bug.cgi?id=1023920#c2

     The landscape at the moment is:

     On navigator [Firefox, Chrome, Opera]
     On window [IE, Safari]
     */
    var isDNT = navigator.doNotTrack == '1' || window.doNotTrack == '1';

    var analyticsEnabled = (
        guardian.analyticsEnabled &&
        !isDNT &&
        !cookie.getCookie('ANALYTICS_OFF_KEY')
    );

    function setupAnalytics() {
        ophan.init();
        ophan.loaded.then(ga.init,ga.init);
        uet.init();
        campaignCode.init();
    }

    function setupThirdParties() {
        krux.init();
        facebook.init();
    }

    function init() {

        if (analyticsEnabled) {
            setupAnalytics();
        }

        if(analyticsEnabled && !guardian.isDev) {
            setupThirdParties();
        }
    }

    return {
        init: init,
        enabled: analyticsEnabled
    };
});
