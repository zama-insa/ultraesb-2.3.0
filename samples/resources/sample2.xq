declare namespace m0="http://mock.samples/";
declare variable $payload as document-node() external;
declare variable $balance as xs:double external;

<m:confirmation xmlns:m="http://mock.samples">
  <m:id>{$payload//m0:updateSubscription/m0:id/child::text()}</m:id>
  <m:username>{$payload//m0:updateSubscription/m0:username/child::text()}</m:username>
  <m:currentBalance>{$balance}</m:currentBalance>
  <m:currency>{$payload//m0:updateSubscription/m0:currency/child::text()}</m:currency>
  <m:timestamp>{$payload//m0:updateSubscription/m0:dateOfPayment/child::text()}</m:timestamp>
</m:confirmation>

