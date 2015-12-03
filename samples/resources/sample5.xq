declare variable $symbol as xs:string external;

<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:soap="http://soap.services.samples/">
   <soapenv:Body>
      <soap:getQuote>
         <request>
            <symbol>{$symbol}</symbol>
         </request>
      </soap:getQuote>
   </soapenv:Body>
</soapenv:Envelope>