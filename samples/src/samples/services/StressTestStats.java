/*
 * Copyright (c) 2010-2014 AdroitLogic Private Ltd. (http://adroitlogic.org). All Rights Reserved.
 */

package samples.services;

public class StressTestStats {

    private long succeededCount = 0;
    private long failureCount = 0;
    private long totalCount = 0;

    public StressTestStats() {
        super();
    }

    public synchronized void incTotalCount(boolean success) {
        this.totalCount++;
        if (success) {
            this.succeededCount++;
        } else {
            this.failureCount++;
        }
        if (totalCount % 10000 == 0) {
            System.out.println("Total Message Count     : " + totalCount);
            System.out.println("Succeeded Message Count : " + succeededCount);
            System.out.println("Failure Message Count   : " + failureCount + "\n");
        }
    }

}
