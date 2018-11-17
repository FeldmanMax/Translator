export type OnSuccess =   (request: XMLHttpRequest, destination: Element)     => void;

// Represents the http get method, and returns a function which will
// be executed by the caller with the correct params.
export type HttpGetType =   (url: string, onSuccess: OnSuccess)               => void

interface HttpCalls {
    get(resultElement: Element): HttpGetType
}

export class HttpCallsImplementation implements HttpCalls {
    constructor() {}
    public get(resultElement: Element): HttpGetType {
        return (url: string, onSuccess: OnSuccess) => {
            const xmlHttpRequest = new XMLHttpRequest();
            xmlHttpRequest.open("GET", url);
            xmlHttpRequest.withCredentials = false;
            xmlHttpRequest.onreadystatechange = () => this.onReturn(xmlHttpRequest, resultElement, onSuccess);
            xmlHttpRequest.send();
        };
    }

    private onReturn(request: XMLHttpRequest, resultElement: Element, onSuccess: OnSuccess): void {
        if(       this.isWithinRange(request.status, 200, 300)) {
            onSuccess(request, resultElement);
        } else if(this.isWithinRange(request.status, 300, 400)) {
            // will need to send an event to a proper place
            console.log("status 300: " + request.status);
        } else if(this.isWithinRange(request.status, 400, 500)) {
            // will need to send an event to a proper place
            console.log("status 400: " + request.status);
        } else {
            // will need to send an event to a proper place
            console.log("status 500: " + request.status);
        }
    }

    private isWithinRange(status: number, min: number, nonInclusiveMax: number): boolean {
        return status >= min && status < nonInclusiveMax;
    }
}


export default HttpCalls;