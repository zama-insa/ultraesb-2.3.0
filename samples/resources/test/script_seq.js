    println("Message target :  " + msg.getDestinationURL());
    if ("gold".equals(msg.getFirstTransportHeader("ClientID"))) {
       mediation.sendToEndpoint(msg, "test1");
    } else {
       mediation.sendToEndpoint(msg, "test2");
    }