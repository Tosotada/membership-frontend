define([
    'gumshoejs',
    'smoothScroll',
    'src/utils/helper'
], function (gumshoe, smoothScroll, helper) {
    'use strict';

    var SELECTORS = {
        sectionNav: '.js-section-nav'
    };
    var ACTIVE_CLASS = 'is-active';

    function init() {
        var navEl = document.querySelector(SELECTORS.sectionNav);
        if(navEl) {
            sectionNavigation(navEl);
        }
    }

    function sectionNavigation(navEl) {
        var navOffset = helper.getOuterHeight(navEl);
        gumshoe.init({
            activeClass: ACTIVE_CLASS,
            offset: navOffset + 100
        });
        new smoothScroll('a[href*="#"]', {
            speed: 500,
            easing: 'easeInQuad',
            offset: navOffset + 20
        });
    }

    return {
        init: init
    };

});
