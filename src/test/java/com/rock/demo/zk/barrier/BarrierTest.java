package com.rock.demo.zk.barrier;

import org.apache.zookeeper.KeeperException;

import java.util.Random;

public class BarrierTest {
    public static void main(String[] args) {
        testBarrier("127.0.0.1:2181", new Integer(3));
    }

    public static void testBarrier(String addr, Integer size) {
        Barrier b = new Barrier(addr, "/b1", size);

        try {
            boolean flag = b.enter();
            System.out.println("Entered barrier: " + 3);
            if (!flag) System.out.println("Error when entering the barrier");
        } catch (KeeperException e) {
            System.out.println("Entered KeeperException: " + 3);
        } catch (InterruptedException e) {
            System.out.println("Entered InterruptedException: " + 3);
        }

        // Generate random integer
        Random rand = new Random();
        int r = rand.nextInt(100);
        // Loop for rand iterations
        for (int i = 0; i < r; i++) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {

            }
        }
        try {
            b.leave();
        } catch (KeeperException e) {

        } catch (InterruptedException e) {

        }
        System.out.println("Left barrier");
    }
}

