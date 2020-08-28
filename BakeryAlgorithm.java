package com.cds.assignment1;

/**
 * Bakery Algorithm has a Bakery and a token system
 * Each customer has to be given a numbered token
 * Each customer waits for his token and then chooses for a desired time
 */
public class BakeryAlgorithm {
    static final int DELIVERABLE_COUNT = 5;
    private static volatile boolean[] choosing = new boolean[DELIVERABLE_COUNT];
    private static volatile int[] ticket = new int[DELIVERABLE_COUNT];
    private static int CUSTOMER_WAIT_TIME = 100;

    /**
     * A bakery offers tickets and serves customers
     * based on his token number
     */
    static class Bakery extends Thread {
        public int customerId;

        public Bakery(int cid) {
            this.customerId = cid;
        }

        public int getMaxTicketValue() {
            int max = ticket[0];
            for (int i = 1; i < ticket.length; i++) {
                if (ticket[i] > max)
                    max = ticket[i];
            }
            return max;
        }

        public void lock(int id) {
            choosing[id] = true;
            int maxTicketValue = getMaxTicketValue();
            ticket[id] = maxTicketValue;
            choosing[id] = false;

            for(int i = 0; i<DELIVERABLE_COUNT; i++){
                if(i == customerId){
                    System.out.println("~~~ CURRENT CUSTOMER's TURN ~~~");
                }
                while(choosing[i]){
                    System.out.println("~~~ CUSTOMER "+ id +" IS CHOOSING~~~");
                    try {
                        Thread.sleep(CUSTOMER_WAIT_TIME);
                    }catch (Exception e){
                        e.printStackTrace(System.out);
                    }
                }

                while (ticket[i] != 0 && ticket[i] >= ticket[customerId]){
                    System.out.println("~~~ CUSTOMER "+ id +" IS WAITING FOR HIS TURN~~~");
                    try {
                        Thread.sleep(CUSTOMER_WAIT_TIME);
                    }catch (Exception e){
                        e.printStackTrace(System.out);
                    }
                }
            }
        }

        private void unlock(int id) {
            ticket[id] = 0;
        }

        public void run(){
            try {
                lock(customerId);
                Thread.sleep(CUSTOMER_WAIT_TIME);
                unlock(customerId);
            }catch (Exception e){
                e.printStackTrace(System.out);
            }
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < DELIVERABLE_COUNT; i++) {
            choosing[i] = false;
            ticket[i] = 0;
        }

        Bakery[] customers = new Bakery[DELIVERABLE_COUNT];

        for (int i = 0; i < customers.length; i++) {
            customers[i] = new Bakery(i);
            customers[i].start();
        }

        for (int i = 0; i < customers.length; i++) {
            try {
                customers[i].join();
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
        }
    }
}
