"use strict";
exports.__esModule = true;
var languageService_1 = require("./services/languageService");
var async_1 = require("./utils/async");
var MainClass = /** @class */ (function () {
    function MainClass() {
        // should not be used
        this.setup = function () { };
        // should not be used
        this.global = function () { };
        this.init = function () {
            console.log("I AM HERE - 1");
            var languageService = new languageService_1["default"](new async_1.HttpCallsImplementation());
            languageService.getAllLanguages(document.querySelector("div[data-name=\"languages\"]"));
        };
        this.init();
    }
    return MainClass;
}());
exports.MainClass = MainClass;
console.log("I AM HERE - 2");
console.log("I AM HERE - 3");
new MainClass();
console.log("I AM HERE - 4");
