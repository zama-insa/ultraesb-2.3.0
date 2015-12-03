declare variable $id as xs:string external;
declare variable $startdate as xs:string external;
declare variable $enddate as xs:string external;
declare variable $category as xs:int external;
declare variable $username as xs:string external;

<m:subscription xmlns:m="http://mock.samples">
  <m:id>{$id}</m:id>
  <m:startdate>{$startdate}</m:startdate>
  <m:enddate>{$enddate}</m:enddate>
  <m:category>{$category}</m:category>
  <m:username>{$username}</m:username>
</m:subscription>
