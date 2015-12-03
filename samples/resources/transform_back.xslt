<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fn="http://www.w3.org/2005/02/xpath-functions"
	xmlns:m0="http://soap.services.samples/"
	exclude-result-prefixes="m0 fn">
<xsl:output method="xml" omit-xml-declaration="yes" indent="yes"/>

<xsl:template match="/">
  <xsl:apply-templates select="//m0:getQuoteResponse" />
</xsl:template>

<xsl:template match="return">

<m:CheckPriceResponse xmlns:m="http://soap.services.samples/">
	<m:Code><xsl:value-of select="symbol"/></m:Code>
	<m:Price><xsl:value-of select="last"/></m:Price>
</m:CheckPriceResponse>

</xsl:template>
</xsl:stylesheet>
