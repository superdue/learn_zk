package com.rock.demo.zk.datamoniter;

import org.apache.zookeeper.KeeperException;

enum EnumDemoFirst {

    RED(-2, "hongse"), GREEN(2, "lvse"), OK(9);

    private int code;
    private String msg;


    private EnumDemoFirst(int ordinal, String name) {
        this.code = ordinal;
        this.msg = name;
    }


    EnumDemoFirst(int i) {
        this.code = code;
    }

    public int intValue() {
        return this.code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


}

public class test {
    public static void main(String[] args) {
        KeeperException.Code tat = KeeperException.Code.get(-101);
        System.out.println(tat);
//        EnumDemoFirst[] values = EnumDemoFirst.values();
//        System.out.println(values[2]);
//        for (EnumDemoFirst enumDemoFirst : values) {
//            System.out.println(enumDemoFirst + "--" + enumDemoFirst.getCode() + "--" + enumDemoFirst.getMsg());
//            System.out.println("=============");
//        }
    }
}
