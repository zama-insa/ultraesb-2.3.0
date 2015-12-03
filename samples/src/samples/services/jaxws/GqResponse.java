/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package samples.services.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for gqResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="gqResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="change" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="earnings" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="high" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="last" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="lastTradeTimestamp" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="low" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="marketCap" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="open" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="peRatio" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="percentageChange" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="prevClose" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="symbol" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="volume" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "gqResponse", propOrder = {
    "change",
    "earnings",
    "high",
    "last",
    "lastTradeTimestamp",
    "low",
    "marketCap",
    "name",
    "open",
    "peRatio",
    "percentageChange",
    "prevClose",
    "symbol",
    "volume"
})
public class GqResponse {

    protected double change;
    protected double earnings;
    protected double high;
    protected double last;
    protected String lastTradeTimestamp;
    protected double low;
    protected double marketCap;
    protected String name;
    protected double open;
    protected double peRatio;
    protected double percentageChange;
    protected double prevClose;
    protected String symbol;
    protected int volume;

    /**
     * Gets the value of the change property.
     * 
     */
    public double getChange() {
        return change;
    }

    /**
     * Sets the value of the change property.
     * 
     */
    public void setChange(double value) {
        this.change = value;
    }

    /**
     * Gets the value of the earnings property.
     * 
     */
    public double getEarnings() {
        return earnings;
    }

    /**
     * Sets the value of the earnings property.
     * 
     */
    public void setEarnings(double value) {
        this.earnings = value;
    }

    /**
     * Gets the value of the high property.
     * 
     */
    public double getHigh() {
        return high;
    }

    /**
     * Sets the value of the high property.
     * 
     */
    public void setHigh(double value) {
        this.high = value;
    }

    /**
     * Gets the value of the last property.
     * 
     */
    public double getLast() {
        return last;
    }

    /**
     * Sets the value of the last property.
     * 
     */
    public void setLast(double value) {
        this.last = value;
    }

    /**
     * Gets the value of the lastTradeTimestamp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastTradeTimestamp() {
        return lastTradeTimestamp;
    }

    /**
     * Sets the value of the lastTradeTimestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastTradeTimestamp(String value) {
        this.lastTradeTimestamp = value;
    }

    /**
     * Gets the value of the low property.
     * 
     */
    public double getLow() {
        return low;
    }

    /**
     * Sets the value of the low property.
     * 
     */
    public void setLow(double value) {
        this.low = value;
    }

    /**
     * Gets the value of the marketCap property.
     * 
     */
    public double getMarketCap() {
        return marketCap;
    }

    /**
     * Sets the value of the marketCap property.
     * 
     */
    public void setMarketCap(double value) {
        this.marketCap = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the open property.
     * 
     */
    public double getOpen() {
        return open;
    }

    /**
     * Sets the value of the open property.
     * 
     */
    public void setOpen(double value) {
        this.open = value;
    }

    /**
     * Gets the value of the peRatio property.
     * 
     */
    public double getPeRatio() {
        return peRatio;
    }

    /**
     * Sets the value of the peRatio property.
     * 
     */
    public void setPeRatio(double value) {
        this.peRatio = value;
    }

    /**
     * Gets the value of the percentageChange property.
     * 
     */
    public double getPercentageChange() {
        return percentageChange;
    }

    /**
     * Sets the value of the percentageChange property.
     * 
     */
    public void setPercentageChange(double value) {
        this.percentageChange = value;
    }

    /**
     * Gets the value of the prevClose property.
     * 
     */
    public double getPrevClose() {
        return prevClose;
    }

    /**
     * Sets the value of the prevClose property.
     * 
     */
    public void setPrevClose(double value) {
        this.prevClose = value;
    }

    /**
     * Gets the value of the symbol property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Sets the value of the symbol property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSymbol(String value) {
        this.symbol = value;
    }

    /**
     * Gets the value of the volume property.
     * 
     */
    public int getVolume() {
        return volume;
    }

    /**
     * Sets the value of the volume property.
     * 
     */
    public void setVolume(int value) {
        this.volume = value;
    }

}
