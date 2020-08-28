package com.cds.assignment1;

import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Yasasvee Atreya V.
 */

/**
 * We have N number of philosophers and N number of Chop sticks
 * The Chop sticks are placed alternatively to the left and right of each circularly seated philosophers.
 * Each Philosopher tries to eat from a bowl of Ramen
 * A philosopher can only eat if the chop stick to his left and to his right are available, else he waits(thinks)
 */
public class DiningPhilosophersProblem {
    static int TOTAL_PHILOSOPHERS = 5;
    static Philosopher PHILOSOPHERS[] = new Philosopher[TOTAL_PHILOSOPHERS];
    static Chopsticks CHOP_STICKS[] = new Chopsticks[TOTAL_PHILOSOPHERS];

    /**
     * A Chopstick can be free on the table
     * Or it can be grabbed and released after eating.
     * Its current state is marked by the chipstick semaphore which prevents an already grabbed chopstick from being grabbed again.
     */
    static class Chopsticks {
        public Semaphore chopstick = new Semaphore(1);
        void take(int philosopherId) {
            try{
                System.out.println("~~~ Philospher " + philosopherId + " has taken Chop Stick ~~~");
                chopstick.acquire();
            }catch(Exception e){
                System.out.println("~~~UNABLE TO GRAB THE CHOP STICK~~~");
                e.printStackTrace(System.out);
            }
        }

        void release(int philosopherId){
            System.out.println("~~~ Philospher " + philosopherId + " has taken Chop Stick ~~~");
            chopstick.release();
        }

        boolean onTable(){
            return chopstick.availablePermits()>0;
        }
    }

    /**
     * A philosopher tries to eat.
     * For that he grabs left chopstick, then right chopstick and goes on to eat.
     * If any of the chop sticks are not available he has to wait(think).
     *
     * For ideal and equal eat times, alternative philosophers should be able to eat and think.
     * If all the chop sticks are grabbed that results in a dead lock
     */
    static class Philosopher extends Thread {
        public int philosopherId;
        public Chopsticks leftChopStick;
        public Chopsticks rightChopStick;

        Philosopher(int philosopherId, Chopsticks leftChopStick, Chopsticks rightChopStick){
            this.philosopherId = philosopherId;
            this.leftChopStick= leftChopStick;
            this.rightChopStick = rightChopStick;
        }
//        private void think(){
//            System.out.println("Philosopher " + philosopherId + " is thinking.\n");
//            System.out.flush();
//            Thread.sleep(numGenerator.nextInt(10));
//        }
        public void run (){
            System.out.println("~~~ Philospher " + philosopherId + " is trying to eat ~~~");
            while(true){
                leftChopStick.take(philosopherId);
                rightChopStick.take(philosopherId);
                eat(philosopherId);
                leftChopStick.release(philosopherId);
                rightChopStick.release(philosopherId);
            }
        }
        public void eat (int philosopherId){
            try {
                int sleepTime = ThreadLocalRandom.current().nextInt(0, 1000);
                System.out.println("Philosopher " + philosopherId + " is eating for " + sleepTime);
                Thread.sleep(sleepTime);
            }catch (Exception e){
                System.out.println("~~~ Philosopher " + philosopherId + "was unable to eat ~~~");
                e.printStackTrace(System.out);
            }
        }

    }
    public static void main(String[] args) {
        System.out.println("~~~ Dining philosophers problem with " + TOTAL_PHILOSOPHERS + " Philosphers ~~~");
        int eachChopStick = 0;
        while(eachChopStick<TOTAL_PHILOSOPHERS){
            CHOP_STICKS[eachChopStick] = new Chopsticks();
            eachChopStick++;
        }

        int eachPhilosopher = 0;
        while(eachPhilosopher<TOTAL_PHILOSOPHERS){
            PHILOSOPHERS[eachPhilosopher] = new Philosopher(eachPhilosopher, CHOP_STICKS[eachPhilosopher], CHOP_STICKS[(eachPhilosopher+1) % TOTAL_PHILOSOPHERS]);
            PHILOSOPHERS[eachPhilosopher].start();
            eachPhilosopher++;
        }

        while (true){
            try{
                boolean deadlock = true;
                for(Chopsticks c: CHOP_STICKS){
                    if(c.onTable()){
                        deadlock = false;
                        break;
                    }
                }
                if(deadlock){
                    Thread.sleep(1000); //Optimal value should be determined!?
                    System.out.println("!!! DEADLOCK !!!");
                    break;
                }
            }catch (Exception e){
                e.printStackTrace(System.out);
            }
        }
        System.exit(0);
    }
}
