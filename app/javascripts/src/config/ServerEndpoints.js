"use strict";
exports.__esModule = true;
var ServerEndpoints = /** @class */ (function () {
    function ServerEndpoints() {
    }
    ServerEndpoints.prefix = "http://localhost:9000";
    ServerEndpoints.allLanguages = ServerEndpoints.prefix + "/backoffice/translator/lang";
    return ServerEndpoints;
}());
exports["default"] = ServerEndpoints;
