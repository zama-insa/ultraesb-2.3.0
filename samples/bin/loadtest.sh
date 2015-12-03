#/bin/sh

# Directory which contains the soa-toolbox-x.x.x.jar
RUN_DIR=../../lib/samples
# Script to loop test
LOOP=../../samples/bin/loop.sh
# Sample requests directory
REQ_DIR=../../samples/resources/requests
# Depends on the ESB 
SERVICE_PREFIX=$1

cd $RUN_DIR
echo Executing in directory $PWD

echo "Warm-up..."
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 1000 20 urn:buyStocks.2 $SERVICE_PREFIX/DirectProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 1000 20 urn:buyStocks.2 $SERVICE_PREFIX/CBRProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 1000 20 urn:buyStocks.2 $SERVICE_PREFIX/CBRSOAPHeaderProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 1000 20 urn:buyStocks.2 $SERVICE_PREFIX/CBRTransportHeaderProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 1000 20 urn:buyStocks.2 $SERVICE_PREFIX/XSLTProxy
$LOOP 1 $REQ_DIR/1K_buyStocks_secure.xml 1000 20 urn:buyStocks.2 $SERVICE_PREFIX/SecureProxy

echo "Begin performance test..."
echo `date`

# $LOOP <iterations> <request.file> <n> <c> <action> <url>

#Direct Proxy
echo "Direct 500B"
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 1000 20   urn:buyStocks.2 $SERVICE_PREFIX/DirectProxy
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 1000 40   urn:buyStocks.2 $SERVICE_PREFIX/DirectProxy
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 100  80   urn:buyStocks.2 $SERVICE_PREFIX/DirectProxy
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 100  160  urn:buyStocks.2 $SERVICE_PREFIX/DirectProxy
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 100  320  urn:buyStocks.2 $SERVICE_PREFIX/DirectProxy
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 10   640  urn:buyStocks.2 $SERVICE_PREFIX/DirectProxy
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 10   1280 urn:buyStocks.2 $SERVICE_PREFIX/DirectProxy
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 10   2560 urn:buyStocks.2 $SERVICE_PREFIX/DirectProxy

echo "Direct 1K"
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 1000 20   urn:buyStocks.2 $SERVICE_PREFIX/DirectProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 1000 40   urn:buyStocks.2 $SERVICE_PREFIX/DirectProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 100  80   urn:buyStocks.2 $SERVICE_PREFIX/DirectProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 100  160  urn:buyStocks.2 $SERVICE_PREFIX/DirectProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 100  320  urn:buyStocks.2 $SERVICE_PREFIX/DirectProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 10   640  urn:buyStocks.2 $SERVICE_PREFIX/DirectProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 10   1280 urn:buyStocks.2 $SERVICE_PREFIX/DirectProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 10   2560 urn:buyStocks.2 $SERVICE_PREFIX/DirectProxy

echo "Direct 5K"
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 1000 20   urn:buyStocks.6 $SERVICE_PREFIX/DirectProxy
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 1000 40   urn:buyStocks.6 $SERVICE_PREFIX/DirectProxy
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 100  80   urn:buyStocks.6 $SERVICE_PREFIX/DirectProxy
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 100  160  urn:buyStocks.6 $SERVICE_PREFIX/DirectProxy
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 100  320  urn:buyStocks.6 $SERVICE_PREFIX/DirectProxy
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 10   640  urn:buyStocks.6 $SERVICE_PREFIX/DirectProxy
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 10   1280 urn:buyStocks.6 $SERVICE_PREFIX/DirectProxy
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 10   2560 urn:buyStocks.6 $SERVICE_PREFIX/DirectProxy

echo "Direct 10K"
$LOOP 1 $REQ_DIR/10K_buyStocks.xml 1000 20   urn:buyStocks.12 $SERVICE_PREFIX/DirectProxy
$LOOP 1 $REQ_DIR/10K_buyStocks.xml 1000 40   urn:buyStocks.12 $SERVICE_PREFIX/DirectProxy
$LOOP 1 $REQ_DIR/10K_buyStocks.xml 100  80   urn:buyStocks.12 $SERVICE_PREFIX/DirectProxy
$LOOP 1 $REQ_DIR/10K_buyStocks.xml 100  160  urn:buyStocks.12 $SERVICE_PREFIX/DirectProxy
$LOOP 1 $REQ_DIR/10K_buyStocks.xml 100  320  urn:buyStocks.12 $SERVICE_PREFIX/DirectProxy
$LOOP 1 $REQ_DIR/10K_buyStocks.xml 10   640  urn:buyStocks.12 $SERVICE_PREFIX/DirectProxy

echo "Direct 100K"
$LOOP 1 $REQ_DIR/100K_buyStocks.xml 1000 20   urn:buyStocks.110 $SERVICE_PREFIX/DirectProxy
$LOOP 1 $REQ_DIR/100K_buyStocks.xml 1000 40   urn:buyStocks.110 $SERVICE_PREFIX/DirectProxy
$LOOP 1 $REQ_DIR/100K_buyStocks.xml 100  80   urn:buyStocks.110 $SERVICE_PREFIX/DirectProxy
$LOOP 1 $REQ_DIR/100K_buyStocks.xml 100  160  urn:buyStocks.110 $SERVICE_PREFIX/DirectProxy
$LOOP 1 $REQ_DIR/100K_buyStocks.xml 100  320  urn:buyStocks.110 $SERVICE_PREFIX/DirectProxy
$LOOP 1 $REQ_DIR/100K_buyStocks.xml 10   640  urn:buyStocks.110 $SERVICE_PREFIX/DirectProxy

#------------------------------------------------------------------------------------------------------
#CBR Proxy
echo "CBR 500B"
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 1000 20   urn:buyStocks.2 $SERVICE_PREFIX/CBRProxy
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 1000 40   urn:buyStocks.2 $SERVICE_PREFIX/CBRProxy
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 100  80   urn:buyStocks.2 $SERVICE_PREFIX/CBRProxy
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 100  160  urn:buyStocks.2 $SERVICE_PREFIX/CBRProxy
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 100  320  urn:buyStocks.2 $SERVICE_PREFIX/CBRProxy
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 10   640  urn:buyStocks.2 $SERVICE_PREFIX/CBRProxy
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 10   1280 urn:buyStocks.2 $SERVICE_PREFIX/CBRProxy
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 10   2560 urn:buyStocks.2 $SERVICE_PREFIX/CBRProxy

echo "CBR 1K"
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 1000 20   urn:buyStocks.2 $SERVICE_PREFIX/CBRProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 1000 40   urn:buyStocks.2 $SERVICE_PREFIX/CBRProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 100  80   urn:buyStocks.2 $SERVICE_PREFIX/CBRProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 100  160  urn:buyStocks.2 $SERVICE_PREFIX/CBRProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 100  320  urn:buyStocks.2 $SERVICE_PREFIX/CBRProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 10   640  urn:buyStocks.2 $SERVICE_PREFIX/CBRProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 10   1280 urn:buyStocks.2 $SERVICE_PREFIX/CBRProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 10   2560 urn:buyStocks.2 $SERVICE_PREFIX/CBRProxy

echo "CBR 5K"
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 1000 20   urn:buyStocks.6 $SERVICE_PREFIX/CBRProxy
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 1000 40   urn:buyStocks.6 $SERVICE_PREFIX/CBRProxy
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 100  80   urn:buyStocks.6 $SERVICE_PREFIX/CBRProxy
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 100  160  urn:buyStocks.6 $SERVICE_PREFIX/CBRProxy
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 100  320  urn:buyStocks.6 $SERVICE_PREFIX/CBRProxy
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 10   640  urn:buyStocks.6 $SERVICE_PREFIX/CBRProxy
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 10   1280 urn:buyStocks.6 $SERVICE_PREFIX/CBRProxy
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 10   2560 urn:buyStocks.6 $SERVICE_PREFIX/CBRProxy

echo "CBR 10K"
$LOOP 1 $REQ_DIR/10K_buyStocks.xml 1000 20   urn:buyStocks.12 $SERVICE_PREFIX/CBRProxy
$LOOP 1 $REQ_DIR/10K_buyStocks.xml 1000 40   urn:buyStocks.12 $SERVICE_PREFIX/CBRProxy
$LOOP 1 $REQ_DIR/10K_buyStocks.xml 100  80   urn:buyStocks.12 $SERVICE_PREFIX/CBRProxy
$LOOP 1 $REQ_DIR/10K_buyStocks.xml 100  160  urn:buyStocks.12 $SERVICE_PREFIX/CBRProxy
$LOOP 1 $REQ_DIR/10K_buyStocks.xml 100  320  urn:buyStocks.12 $SERVICE_PREFIX/CBRProxy
$LOOP 1 $REQ_DIR/10K_buyStocks.xml 10   640  urn:buyStocks.12 $SERVICE_PREFIX/CBRProxy

#------------------------------------------------------------------------------------------------------
#CBR SOAP Header Proxy

echo "CBR-SOAP 500B"
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 1000 20   urn:buyStocks.2 $SERVICE_PREFIX/CBRSOAPHeaderProxy
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 1000 40   urn:buyStocks.2 $SERVICE_PREFIX/CBRSOAPHeaderProxy
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 100  80   urn:buyStocks.2 $SERVICE_PREFIX/CBRSOAPHeaderProxy
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 100  160  urn:buyStocks.2 $SERVICE_PREFIX/CBRSOAPHeaderProxy
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 100  320  urn:buyStocks.2 $SERVICE_PREFIX/CBRSOAPHeaderProxy
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 10   640  urn:buyStocks.2 $SERVICE_PREFIX/CBRSOAPHeaderProxy
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 10   1280 urn:buyStocks.2 $SERVICE_PREFIX/CBRSOAPHeaderProxy
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 10   2560 urn:buyStocks.2 $SERVICE_PREFIX/CBRSOAPHeaderProxy

echo "CBR-SOAP 1K"
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 1000 20   urn:buyStocks.2 $SERVICE_PREFIX/CBRSOAPHeaderProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 1000 40   urn:buyStocks.2 $SERVICE_PREFIX/CBRSOAPHeaderProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 100  80   urn:buyStocks.2 $SERVICE_PREFIX/CBRSOAPHeaderProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 100  160  urn:buyStocks.2 $SERVICE_PREFIX/CBRSOAPHeaderProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 100  320  urn:buyStocks.2 $SERVICE_PREFIX/CBRSOAPHeaderProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 10   640  urn:buyStocks.2 $SERVICE_PREFIX/CBRSOAPHeaderProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 10   1280 urn:buyStocks.2 $SERVICE_PREFIX/CBRSOAPHeaderProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 10   2560 urn:buyStocks.2 $SERVICE_PREFIX/CBRSOAPHeaderProxy

echo "CBR-SOAP 5K"
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 1000 20   urn:buyStocks.6 $SERVICE_PREFIX/CBRSOAPHeaderProxy
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 1000 40   urn:buyStocks.6 $SERVICE_PREFIX/CBRSOAPHeaderProxy
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 100  80   urn:buyStocks.6 $SERVICE_PREFIX/CBRSOAPHeaderProxy
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 100  160  urn:buyStocks.6 $SERVICE_PREFIX/CBRSOAPHeaderProxy
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 100  320  urn:buyStocks.6 $SERVICE_PREFIX/CBRSOAPHeaderProxy
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 10   640  urn:buyStocks.6 $SERVICE_PREFIX/CBRSOAPHeaderProxy
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 10   1280 urn:buyStocks.6 $SERVICE_PREFIX/CBRSOAPHeaderProxy
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 10   2560 urn:buyStocks.6 $SERVICE_PREFIX/CBRSOAPHeaderProxy

echo "CBR-SOAP 10K"
$LOOP 1 $REQ_DIR/10K_buyStocks.xml 1000 20   urn:buyStocks.12 $SERVICE_PREFIX/CBRSOAPHeaderProxy
$LOOP 1 $REQ_DIR/10K_buyStocks.xml 1000 40   urn:buyStocks.12 $SERVICE_PREFIX/CBRSOAPHeaderProxy
$LOOP 1 $REQ_DIR/10K_buyStocks.xml 100  80   urn:buyStocks.12 $SERVICE_PREFIX/CBRSOAPHeaderProxy
$LOOP 1 $REQ_DIR/10K_buyStocks.xml 100  160  urn:buyStocks.12 $SERVICE_PREFIX/CBRSOAPHeaderProxy
$LOOP 1 $REQ_DIR/10K_buyStocks.xml 100  320  urn:buyStocks.12 $SERVICE_PREFIX/CBRSOAPHeaderProxy
$LOOP 1 $REQ_DIR/10K_buyStocks.xml 10   640  urn:buyStocks.12 $SERVICE_PREFIX/CBRSOAPHeaderProxy

#------------------------------------------------------------------------------------------------------
#CBR Transport Header Proxy

echo "CBR-Transport 500B"
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 1000 20   urn:buyStocks.2 $SERVICE_PREFIX/CBRTransportHeaderProxy
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 1000 40   urn:buyStocks.2 $SERVICE_PREFIX/CBRTransportHeaderProxy
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 100  80   urn:buyStocks.2 $SERVICE_PREFIX/CBRTransportHeaderProxy
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 100  160  urn:buyStocks.2 $SERVICE_PREFIX/CBRTransportHeaderProxy
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 100  320  urn:buyStocks.2 $SERVICE_PREFIX/CBRTransportHeaderProxy
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 10   640  urn:buyStocks.2 $SERVICE_PREFIX/CBRTransportHeaderProxy
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 10   1280 urn:buyStocks.2 $SERVICE_PREFIX/CBRTransportHeaderProxy
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 10   2560 urn:buyStocks.2 $SERVICE_PREFIX/CBRTransportHeaderProxy

echo "CBR-Transport 1K"
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 1000 20   urn:buyStocks.2 $SERVICE_PREFIX/CBRTransportHeaderProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 1000 40   urn:buyStocks.2 $SERVICE_PREFIX/CBRTransportHeaderProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 100  80   urn:buyStocks.2 $SERVICE_PREFIX/CBRTransportHeaderProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 100  160  urn:buyStocks.2 $SERVICE_PREFIX/CBRTransportHeaderProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 100  320  urn:buyStocks.2 $SERVICE_PREFIX/CBRTransportHeaderProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 10   640  urn:buyStocks.2 $SERVICE_PREFIX/CBRTransportHeaderProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 10   1280 urn:buyStocks.2 $SERVICE_PREFIX/CBRTransportHeaderProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 10   2560 urn:buyStocks.2 $SERVICE_PREFIX/CBRTransportHeaderProxy

echo "CBR-Transport 5K"
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 1000 20   urn:buyStocks.6 $SERVICE_PREFIX/CBRTransportHeaderProxy
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 1000 40   urn:buyStocks.6 $SERVICE_PREFIX/CBRTransportHeaderProxy
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 100  80   urn:buyStocks.6 $SERVICE_PREFIX/CBRTransportHeaderProxy
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 100  160  urn:buyStocks.6 $SERVICE_PREFIX/CBRTransportHeaderProxy
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 100  320  urn:buyStocks.6 $SERVICE_PREFIX/CBRTransportHeaderProxy
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 10   640  urn:buyStocks.6 $SERVICE_PREFIX/CBRTransportHeaderProxy
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 10   1280 urn:buyStocks.6 $SERVICE_PREFIX/CBRTransportHeaderProxy
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 10   2560 urn:buyStocks.6 $SERVICE_PREFIX/CBRTransportHeaderProxy

echo "CBR-Transport 10K"
$LOOP 1 $REQ_DIR/10K_buyStocks.xml 1000 20   urn:buyStocks.12 $SERVICE_PREFIX/CBRTransportHeaderProxy
$LOOP 1 $REQ_DIR/10K_buyStocks.xml 1000 40   urn:buyStocks.12 $SERVICE_PREFIX/CBRTransportHeaderProxy
$LOOP 1 $REQ_DIR/10K_buyStocks.xml 100  80   urn:buyStocks.12 $SERVICE_PREFIX/CBRTransportHeaderProxy
$LOOP 1 $REQ_DIR/10K_buyStocks.xml 100  160  urn:buyStocks.12 $SERVICE_PREFIX/CBRTransportHeaderProxy
$LOOP 1 $REQ_DIR/10K_buyStocks.xml 100  320  urn:buyStocks.12 $SERVICE_PREFIX/CBRTransportHeaderProxy
$LOOP 1 $REQ_DIR/10K_buyStocks.xml 10   640  urn:buyStocks.12 $SERVICE_PREFIX/CBRTransportHeaderProxy

#----------------------------------------------------------------------------------------------------
#XSLT Proxy

echo "XSLT 500B"
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 1000 20   urn:buyStocks.2 $SERVICE_PREFIX/XSLTProxy
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 1000 40   urn:buyStocks.2 $SERVICE_PREFIX/XSLTProxy
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 100  80   urn:buyStocks.2 $SERVICE_PREFIX/XSLTProxy
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 100  160  urn:buyStocks.2 $SERVICE_PREFIX/XSLTProxy
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 100  320  urn:buyStocks.2 $SERVICE_PREFIX/XSLTProxy
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 10   640  urn:buyStocks.2 $SERVICE_PREFIX/XSLTProxy
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 10   1280 urn:buyStocks.2 $SERVICE_PREFIX/XSLTProxy
$LOOP 1 $REQ_DIR/500B_buyStocks.xml 10   2560 urn:buyStocks.2 $SERVICE_PREFIX/XSLTProxy

echo "XSLT 1K"
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 1000 20   urn:buyStocks.2 $SERVICE_PREFIX/XSLTProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 1000 40   urn:buyStocks.2 $SERVICE_PREFIX/XSLTProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 100  80   urn:buyStocks.2 $SERVICE_PREFIX/XSLTProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 100  160  urn:buyStocks.2 $SERVICE_PREFIX/XSLTProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 100  320  urn:buyStocks.2 $SERVICE_PREFIX/XSLTProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 10   640  urn:buyStocks.2 $SERVICE_PREFIX/XSLTProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 10   1280 urn:buyStocks.2 $SERVICE_PREFIX/XSLTProxy
$LOOP 1 $REQ_DIR/1K_buyStocks.xml 10   2560 urn:buyStocks.2 $SERVICE_PREFIX/XSLTProxy

echo "XSLT 5K"
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 1000 20   urn:buyStocks.6 $SERVICE_PREFIX/XSLTProxy
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 1000 40   urn:buyStocks.6 $SERVICE_PREFIX/XSLTProxy
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 100  80   urn:buyStocks.6 $SERVICE_PREFIX/XSLTProxy
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 100  160  urn:buyStocks.6 $SERVICE_PREFIX/XSLTProxy
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 100  320  urn:buyStocks.6 $SERVICE_PREFIX/XSLTProxy
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 10   640  urn:buyStocks.6 $SERVICE_PREFIX/XSLTProxy
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 10   1280 urn:buyStocks.6 $SERVICE_PREFIX/XSLTProxy
$LOOP 1 $REQ_DIR/5K_buyStocks.xml 10   2560 urn:buyStocks.6 $SERVICE_PREFIX/XSLTProxy

echo "XSLT 10K"
$LOOP 1 $REQ_DIR/10K_buyStocks.xml 1000 20   urn:buyStocks.12 $SERVICE_PREFIX/XSLTProxy
$LOOP 1 $REQ_DIR/10K_buyStocks.xml 1000 40   urn:buyStocks.12 $SERVICE_PREFIX/XSLTProxy
$LOOP 1 $REQ_DIR/10K_buyStocks.xml 100  80   urn:buyStocks.12 $SERVICE_PREFIX/XSLTProxy
$LOOP 1 $REQ_DIR/10K_buyStocks.xml 100  160  urn:buyStocks.12 $SERVICE_PREFIX/XSLTProxy
$LOOP 1 $REQ_DIR/10K_buyStocks.xml 100  320  urn:buyStocks.12 $SERVICE_PREFIX/XSLTProxy
$LOOP 1 $REQ_DIR/10K_buyStocks.xml 10   640  urn:buyStocks.12 $SERVICE_PREFIX/XSLTProxy


#----------------------------------------------------------------------------------------------------
#Secure Proxy
echo "Secure 500B"
$LOOP 1 $REQ_DIR/500B_buyStocks_secure.xml 1000 20   urn:buyStocks.2 $SERVICE_PREFIX/SecureProxy
$LOOP 1 $REQ_DIR/500B_buyStocks_secure.xml 1000 40   urn:buyStocks.2 $SERVICE_PREFIX/SecureProxy
$LOOP 1 $REQ_DIR/500B_buyStocks_secure.xml 100  80   urn:buyStocks.2 $SERVICE_PREFIX/SecureProxy
$LOOP 1 $REQ_DIR/500B_buyStocks_secure.xml 100  160  urn:buyStocks.2 $SERVICE_PREFIX/SecureProxy
$LOOP 1 $REQ_DIR/500B_buyStocks_secure.xml 100  320  urn:buyStocks.2 $SERVICE_PREFIX/SecureProxy
$LOOP 1 $REQ_DIR/500B_buyStocks_secure.xml 10   640  urn:buyStocks.2 $SERVICE_PREFIX/SecureProxy
$LOOP 1 $REQ_DIR/500B_buyStocks_secure.xml 10   1280 urn:buyStocks.2 $SERVICE_PREFIX/SecureProxy
$LOOP 1 $REQ_DIR/500B_buyStocks_secure.xml 10   2560 urn:buyStocks.2 $SERVICE_PREFIX/SecureProxy

echo "Secure 1K"
$LOOP 1 $REQ_DIR/1K_buyStocks_secure.xml 1000 20   urn:buyStocks.2 $SERVICE_PREFIX/SecureProxy
$LOOP 1 $REQ_DIR/1K_buyStocks_secure.xml 1000 40   urn:buyStocks.2 $SERVICE_PREFIX/SecureProxy
$LOOP 1 $REQ_DIR/1K_buyStocks_secure.xml 100  80   urn:buyStocks.2 $SERVICE_PREFIX/SecureProxy
$LOOP 1 $REQ_DIR/1K_buyStocks_secure.xml 100  160  urn:buyStocks.2 $SERVICE_PREFIX/SecureProxy
$LOOP 1 $REQ_DIR/1K_buyStocks_secure.xml 100  320  urn:buyStocks.2 $SERVICE_PREFIX/SecureProxy
$LOOP 1 $REQ_DIR/1K_buyStocks_secure.xml 10   640  urn:buyStocks.2 $SERVICE_PREFIX/SecureProxy
$LOOP 1 $REQ_DIR/1K_buyStocks_secure.xml 10   1280 urn:buyStocks.2 $SERVICE_PREFIX/SecureProxy
$LOOP 1 $REQ_DIR/1K_buyStocks_secure.xml 10   2560 urn:buyStocks.2 $SERVICE_PREFIX/SecureProxy

echo "Secure 5K"
$LOOP 1 $REQ_DIR/5K_buyStocks_secure.xml 1000 20   urn:buyStocks.6 $SERVICE_PREFIX/SecureProxy
$LOOP 1 $REQ_DIR/5K_buyStocks_secure.xml 1000 40   urn:buyStocks.6 $SERVICE_PREFIX/SecureProxy
$LOOP 1 $REQ_DIR/5K_buyStocks_secure.xml 100  80   urn:buyStocks.6 $SERVICE_PREFIX/SecureProxy
$LOOP 1 $REQ_DIR/5K_buyStocks_secure.xml 100  160  urn:buyStocks.6 $SERVICE_PREFIX/SecureProxy
$LOOP 1 $REQ_DIR/5K_buyStocks_secure.xml 100  320  urn:buyStocks.6 $SERVICE_PREFIX/SecureProxy
$LOOP 1 $REQ_DIR/5K_buyStocks_secure.xml 10   640  urn:buyStocks.6 $SERVICE_PREFIX/SecureProxy

echo "Completed"

