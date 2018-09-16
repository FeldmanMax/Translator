import HttpCalls, {HttpGetType} from "../utils/async";
import ServerEndpoints from "../config/ServerEndpoints";

export default class LanguageService {
    constructor(readonly httpCalls: HttpCalls) {}

    public getAllLanguages(resultElement: HTMLElement | null): void {
        if (!resultElement) return;
        const getMethod: HttpGetType = this.httpCalls.get(resultElement);
        getMethod(ServerEndpoints.allLanguages, (result: XMLHttpRequest, destination: Element) => {
            destination.innerHTML = result.responseText;
        });
    }
}
