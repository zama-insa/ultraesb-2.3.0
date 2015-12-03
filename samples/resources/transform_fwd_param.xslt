<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fn="http://www.w3.org/2005/02/xpath-functions"
	xmlns:m0="http://soap.services.samples/"
    xmlns:map="java:java.util.Map"
	exclude-result-prefixes="m0 fn map">
<xsl:output method="xml" omit-xml-declaration="yes" indent="yes"/>
<xsl:param name="map"/>

<xsl:template match="/">
  <xsl:apply-templates select="//m0:CheckPriceRequest" />
</xsl:template>

<xsl:template match="m0:CheckPriceRequest">

<m:getQuote xmlns:m="http://soap.services.samples/">
	<request><symbol><xsl:value-of select="m0:Code"/></symbol></request>
    <name><xsl:value-of select="map:get($map, 'name')"/></name>
    <company><xsl:value-of select="map:get($map, 'company')"/></company>
</m:getQuote>

</xsl:template>
</xsl:stylesheet>
