xquery version "1.0";

declare namespace s="http://schemas.xmlsoap.org/soap/envelope/";
declare namespace ns2="http://soap.services.samples/";
declare default element namespace "";

let $envs := .//s:Envelope
let $min := $envs[s:Body/ns2:getQuoteResponse/return/last = min($envs/s:Body/ns2:getQuoteResponse/return/last)]
return $min

