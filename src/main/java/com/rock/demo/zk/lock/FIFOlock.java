package com.rock.demo.zk.lock;

import com.rock.demo.zk.leaderelection.TestClient;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;

import java.util.Arrays;
import java.util.List;

/*
 加锁：
ZooKeeper 将按照如下方式实现加锁的操作：
） ZooKeeper 调用 create （）方法来创建一个路径格式为“ _locknode_/lock- ”的节点，此节点类型为sequence （连续）和 ephemeral （临时）。也就是说，创建的节点为临时节点，并且所有的节点连续编号，即“ lock-i ”的格式。
）在创建的锁节点上调用 getChildren （）方法，来获取锁目录下的最小编号节点，并且不设置 watch 。
）步骤 2 中获取的节点恰好是步骤 1 中客户端创建的节点，那么此客户端获得此种类型的锁，然后退出操作。
）客户端在锁目录上调用 exists （）方法，并且设置 watch 来监视锁目录下比自己小一个的连续临时节点的状态。
）如果监视节点状态发生变化，则跳转到第 2 步，继续进行后续的操作，直到退出锁竞争。

解锁：
ZooKeeper 解锁操作非常简单，客户端只需要将加锁操作步骤 1 中创建的临时节点删除即可
 */
public class FIFOlock extends TestClient {
    public static final Logger logger = Logger.getLogger(FIFOlock.class);
    String myZnode;

    public FIFOlock(String connectString, String root) {
        super(connectString);
        this.root = root;
        if (zk != null) {
            try {
                //创建锁节点，并不设置观察
                Stat s = zk.exists(root, false);
                if (s == null) {
                    zk.create(root, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                }
            } catch (KeeperException e) {
                logger.error(e);
            } catch (InterruptedException e) {
                logger.error(e);
            }
        }
    }

    void getLock() throws KeeperException, InterruptedException {
        List<String> list = zk.getChildren(root, false);
        String[] nodes = list.toArray(new String[list.size()]);
        //对锁目录下 的所有子节点排序
        Arrays.sort(nodes);
        //判断该zkclient创建的临时顺序节点是否为集群中最小的节点
        if (myZnode.equals(root + "/" + nodes[0])) {
            doAction();
        } else {
            waitForLock(nodes[0]);
        }
    }

    //创建zk客户端的临时瞬时节点，并尝试获取锁
    void check() throws InterruptedException, KeeperException {
        myZnode = zk.create(root + "/lock_", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("create:" + myZnode);
        getLock();
    }

    void waitForLock(String lower) throws InterruptedException, KeeperException {
        Stat stat = zk.exists(root + "/" + lower, true);
        if (stat != null) {   //发现最小的目录节点还未被移除，则等待
            mutex.wait();
        } else {
            getLock();
        }
    }

    @Override //发现有节点移除，该等待状态的客户端被notify
    public void process(WatchedEvent event) {
        if (event.getType() == Event.EventType.NodeDeleted) {
            System.out.println("得到通知");
            super.process(event);
//            doAction();
            try {
                getLock();
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 执行其他任务
     */
    private void doAction() {
        System.out.println("同步队列已经得到同步，可以开始执行后面的任务了");
    }

    public static void main(String[] args) {
        String connectString = "localhost:" + 2181;

        FIFOlock lk = new FIFOlock(connectString, "/locks");
        try {
            lk.check();
        } catch (InterruptedException e) {
            logger.error(e);
        } catch (KeeperException e) {
            logger.error(e);
        }
    }


}
