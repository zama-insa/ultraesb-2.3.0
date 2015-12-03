import org.adroitlogic.ultraesb.api.*;

public class myseq4 implements JavaClassSequence {
    private long startTime;
    private int count;

    public void execute(Message msg, Mediation mediation) throws Exception {
        System.out.println("Message target :  " + msg.getDestinationURL());
        if ("gold".equals(msg.getFirstTransportHeader("ClientID"))) {
            mediation.sendToEndpoint(msg, "test1");
        } else {
            mediation.sendToEndpoint(msg, "test2");
        }
        count++;
    }

    public void init(Configuration config) {
        startTime = System.currentTimeMillis();
    }

    public void destroy() {
        System.out.println("Execution time:" + (System.currentTimeMillis() - startTime) +
            "ms Processed:" + count + "messages");
    }
}