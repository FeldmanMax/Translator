"use strict";
exports.__esModule = true;
var ServerEndpoints_1 = require("../config/ServerEndpoints");
var LanguageService = /** @class */ (function () {
    function LanguageService(httpCalls) {
        this.httpCalls = httpCalls;
    }
    LanguageService.prototype.getAllLanguages = function (resultElement) {
        if (!resultElement)
            return;
        var getMethod = this.httpCalls.get(resultElement);
        getMethod(ServerEndpoints_1["default"].allLanguages, function (result, destination) {
            destination.innerHTML = result.responseText;
        });
    };
    return LanguageService;
}());
exports["default"] = LanguageService;
