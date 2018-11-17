class ServerEndpoints {
    private static prefix: string = "http://localhost:9000";
    static allLanguages: string = ServerEndpoints.prefix + "/backoffice/translator/lang";
}

export default ServerEndpoints;