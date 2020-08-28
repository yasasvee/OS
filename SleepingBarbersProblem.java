package com.cds.assignment1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Yasasvee Atreya V.
 */

/**
 * We have N number of Barbers and X Number of Customers with Y number of waiting chairs.
 * A barber sleeps until a customer arrives.
 * Then performs a haircut on the arrived customer
 * Keeps performing haircuts untill all the customers are exhausted.
 * There can be only the number of customers as the free chairs.
 * One barber performs haircut on one customer only
 */
public class SleepingBarbersProblem extends Thread{
    static final int AVAILABLE_CHAIRS = 5;
    static final int BARBERS = 1;
    static final int WAIT_TIME = 1000;

    /**
     * A Barber can Perform a Haircut based on his availablility
     * Barbers availability is indicaded by availableBarbers semaphore and similarly customer availability
     */
    static class Barber extends Thread{
        private final int HAIRCUT_TIME = 20;
        private Semaphore availableBarbers;
        private Semaphore availableCustomers;

        public Barber(Semaphore availableBarbers, Semaphore availableCustomers){
            this.availableBarbers = availableBarbers;
            this.availableCustomers = availableCustomers;
        }

        public void run(){
            while(true){
                try {
                    availableCustomers.acquire();
                    cutHair();
                    System.out.println("~~~ HAIRCUT DONE, CUSTOMER LEAVES~~~");
                    availableBarbers.release();
                } catch (Exception e){
                    e.printStackTrace(System.out);
                }
            }
        }

        private void cutHair(){
            try{
                System.out.println("~~~ CUSTOMER HAVING A HAIRCUT ~~~");
                Thread.sleep(HAIRCUT_TIME);
            }catch (Exception e){
                e.printStackTrace(System.out);
            }
        }
    }

    /**
     * A Customer Can come and wake up a barber, i.e. acquire a barber for a haircut
     * Customer can come and fill up the available chairs to get a haircut
     * Once haircut is done, he releases the barber and walks out
     */
    static class Customer extends Thread{
        private AtomicInteger chairs;
        private Semaphore availableBarbers;
        private Semaphore customersAvailable;

        public Customer(AtomicInteger chairs, Semaphore availableBarbers, Semaphore customersAvailable){
            this.chairs = chairs;
            this.availableBarbers = availableBarbers;
            this.customersAvailable = customersAvailable;
        }

        public void run(){
            try {
                customersAvailable.release();
                if(availableBarbers.hasQueuedThreads()){
                    chairs.decrementAndGet();
                    availableBarbers.acquire();
                    chairs.incrementAndGet();
                }
                else{
                    availableBarbers.acquire();
                }
            } catch (Exception e){
                e.printStackTrace(System.out);
            }
        }
    }

    public static void main(String[] args){
        AtomicInteger chairs = new AtomicInteger(AVAILABLE_CHAIRS);
        final Semaphore barbers = new Semaphore(BARBERS, true);
        final Semaphore customers = new Semaphore(0, true);
        ExecutorService openShop = Executors.newFixedThreadPool(BARBERS);
        Barber[]  employees = new Barber[BARBERS];

        System.out.println("!!! OPENING SHOP !!!");
        for(int i = 0; i < BARBERS; i++) {
            System.out.println("!!! "+ BARBERS + " BARBER in SHOP TODAY !!!");
            employees[i]= new Barber(barbers, customers);
            openShop.execute(employees[i]);
        }
        while(true)
        {
            if(chairs.get() >= 0){
                System.out.println("~~~ CUSTOMER HAS ARRIVED ~~~");
                new Thread(new Customer(chairs, barbers, customers)).start();
            }
            else{
                System.out.println("~~~ NO AVAILABLE FREE CHAIRS ~~~");
                try {
                    Thread.sleep(WAIT_TIME);
                }catch (Exception e){
                    e.printStackTrace(System.out);
                }
            }
        }
    }

}
