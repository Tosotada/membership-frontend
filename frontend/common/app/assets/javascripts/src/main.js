require([
    'src/utils/analytics/omniture',
    'src/utils/router',
    'domready',
    'ajax',
    'src/modules/joiner/form',
    'src/modules/events/ctaButton',
    'src/modules/account',
    'src/modules/events/DatetimeEnhance',
    'src/modules/events/modifyEvent'
], function(omnitureAnalytics, router, domready, ajax, StripeForm, ctaButton, account, DatetimeEnhance, modifyEvent) {
    'use strict';

    ajax.init({page: {ajaxUrl: ''}});

    router.match('/event').to(function () {
        var dateEnhance = new DatetimeEnhance();
        dateEnhance.init();
        ctaButton.init();
        modifyEvent.init();
    });

    router.match('*/payment').to(function () {
        var stripe = new StripeForm();
        stripe.init(undefined, function (response) {
            var self = this;
            // token contains id, last4, and card type
            var token = response.id;

            ajax({
                url: '/subscription/subscribe',
                method: 'post',
                data: {
                    stripeToken: token,
                    tier: self.getElement('TIER_FIELD').val()
                },
                success: function () {
                    self.stopLoader();
                    window.location = window.location.href.replace('payment', 'thankyou');
                },
                error: function (error) {

                    var errorObj,
                        errorMessage;

                    try {
                        errorObj = error.response && JSON.parse(error.response);
                        errorMessage = self.getErrorMessage(errorObj);
                        if (errorMessage) {
                            self.handleErrors([errorMessage]);
                        }
                    } catch (e) {}

                    self.stopLoader();
                }
            });
        });
    });

    router.match('/tier/change/partner').to(function () {
        var stripe = new StripeForm();
        stripe.init(undefined, function (response, form) {
            var self = this;
            // token contains id, last4, and card type...
            var token = response.id;

            var inputs = Array.prototype.slice.call(form.querySelectorAll('input[name]'))
                            .concat(Array.prototype.slice.call(form.querySelectorAll('select[name]')));

            var serializedForm = {
                stripeToken: token
            };

            for (var i=0; i<inputs.length; i++) {
                var input = inputs[i];
                serializedForm[input.name] = input.value;
            }

            ajax({
                url: '/tier/change/partner',
                method: 'post',
                data: serializedForm,
                success: function () {
                    self.stopLoader();
                    window.location = window.location.href.replace('payment', 'thankyou');
                },
                error: function (error) {

                    var errorObj,
                        errorMessage;

                    try {
                        errorObj = error.response && JSON.parse(error.response);
                        errorMessage = self.getErrorMessage(errorObj);
                        if (errorMessage) {
                            self.handleErrors([errorMessage]);
                        }
                    } catch (e) {}

                    self.stopLoader();
                }
            });
        });
    });

    router.match('*').to(function () {
        account.init();
        omnitureAnalytics.init();
    });

    domready(function() {
        router.go();
    });

});