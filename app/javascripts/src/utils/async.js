"use strict";
exports.__esModule = true;
var HttpCallsImplementation = /** @class */ (function () {
    function HttpCallsImplementation() {
    }
    HttpCallsImplementation.prototype.get = function (resultElement) {
        var _this = this;
        return function (url, onSuccess) {
            var xmlHttpRequest = new XMLHttpRequest();
            xmlHttpRequest.open("GET", url);
            xmlHttpRequest.withCredentials = false;
            xmlHttpRequest.onreadystatechange = function () { return _this.onReturn(xmlHttpRequest, resultElement, onSuccess); };
            xmlHttpRequest.send();
        };
    };
    HttpCallsImplementation.prototype.onReturn = function (request, resultElement, onSuccess) {
        if (this.isWithinRange(request.status, 200, 300)) {
            onSuccess(request, resultElement);
        }
        else if (this.isWithinRange(request.status, 300, 400)) {
            // will need to send an event to a proper place
            console.log("status 300: " + request.status);
        }
        else if (this.isWithinRange(request.status, 400, 500)) {
            // will need to send an event to a proper place
            console.log("status 400: " + request.status);
        }
        else {
            // will need to send an event to a proper place
            console.log("status 500: " + request.status);
        }
    };
    HttpCallsImplementation.prototype.isWithinRange = function (status, min, nonInclusiveMax) {
        return status >= min && status < nonInclusiveMax;
    };
    return HttpCallsImplementation;
}());
exports.HttpCallsImplementation = HttpCallsImplementation;
