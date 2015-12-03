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

echo "Completed"

