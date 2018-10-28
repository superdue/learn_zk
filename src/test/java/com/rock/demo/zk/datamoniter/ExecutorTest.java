package com.rock.demo.zk.datamoniter;

import org.junit.Test;

public class ExecutorTest {

    @Test
    public void testTestin() {
        Executor.testin("127.0.0.1:2181", "/zk_test/zk01", "/tmp/zk01.log", "sh", "-c", "while true ;do echo OK>>/tmp/zk_test.log;sleep 1;done;");
    }

}
