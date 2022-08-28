package com.learning.bliss.base.io.bio;

import java.io.*;

/**
 * 序列化和反序列化
 *
 * @Author: xuexc
 * @Date: 2021/6/27 16:51
 * @Version 0.1
 */
public class SerializableDemo {
    public static void main(String[] args) throws Exception {
        /*******************************************************************/
        /*****************序列化一个对象 – ObjectOutputStream****************/
        /*******************************************************************/
        File file = new File("io/serializable.txt");
        /*ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
        oos.writeObject(new Person("张三", 20));
        oos.close();*/
        /*******************************************************************/
        /****************反序列化一个对象 – ObjectOutputStream****************/
        /*******************************************************************/
        /*ObjectInputStream input = new ObjectInputStream(new FileInputStream(file));
        Object obj = input.readObject();
        input.close();
        System.out.println(obj);*/
        /*******************************************************************/
        /******************** 自定义序列化– Externalizable*******************/
        /*******************************************************************/
        /*ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
        out.writeObject(new Car("探岳", 20));
        out.close();
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
        System.out.println(in.readObject());
        in.close();*/
        /*******************************************************************/
        /************************** transient关键字**************************/
        /*******************************************************************/
        //Serializable接口实现的操作其实是吧一个对象中的全部属性进行序列化，当然也可以使用我们上使用是Externalizable接口以实现
        // 部分属性的序列化，但是这样的操作比较麻烦，当我们使用Serializable接口实现序列化操作的时候，如果一个对象的某一个属性不
        // 想被序列化保存下来，那么我们可以使用transient关键字进行说明

        ObjectOutputStream out= new ObjectOutputStream(new FileOutputStream(file));
        out.writeObject(new Fruit("香蕉", 3));
        out.close();
        ObjectInputStream input = new ObjectInputStream(new FileInputStream(
                file));
        Object obj = input.readObject();
        input.close();
        System.out.println(obj);
    }

    /**
     * 实现具有序列化能力的类
     */
    public static class Person implements Serializable {
        public Person() {
        }

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return "姓名：" + name + "  年龄：" + age;
        }

        private String name;
        private int age;
    }

    /**
     * 实现具有序列化能力的类
     */
    public static class Car implements Externalizable {
        public Car() {
        }

        public Car(String name, int money) {
            this.name = name;
            this.money = money;
        }

        @Override
        public String toString() {
            return "品牌：" + name + "  价格：" + money;
        }

        private String name;
        private int money;

        //复写这个方法，根据需要可以保存的属性或者具体内容，在序列化的时候使用
        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(this.name);
            out.writeInt(money);
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            this.name = (String) in.readObject();
            this.money = in.readInt();
        }
    }

    /**
     * 实现具有序列化能力的类
     */
    public static class Fruit implements Serializable {
        public Fruit() {
        }

        public Fruit(String name, int num) {
            this.name = name;
            this.num = num;
        }

        @Override
        public String toString() {
            return "名字：" + name + "  存量：" + num;
        }

        private transient String name;
        private int num;
    }
}
