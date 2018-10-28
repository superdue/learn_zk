package com.rock.demo.zk.barrier;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class SyncPrimitive implements Watcher {

    private CountDownLatch connectedSemaphore = new CountDownLatch(1);
    static ZooKeeper zk = null;
    static Integer mutex;

    String root;

    public void process(WatchedEvent event) {
        System.out.println("Process: " + event.getState());
        if (Event.KeeperState.SyncConnected == event.getState()) {
            connectedSemaphore.countDown();
        }

        synchronized (mutex) {
            //System.out.println("Process: " + event.getType());

            mutex.notify();
        }
    }

    SyncPrimitive(String address) {
        if (zk == null) {
            try {
                System.out.println("Starting ZK:");
                zk = new ZooKeeper(address, 1000, this);
                connectedSemaphore.await();
                mutex = new Integer(-1);
                System.out.println("Finished starting ZK: " + zk);
            } catch (IOException e) {
                System.out.println("create:" + e.toString());
                zk = null;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //else mutex = new Integer(-1);
    }

}