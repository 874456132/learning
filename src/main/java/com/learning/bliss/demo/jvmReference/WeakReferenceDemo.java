package com.learning.bliss.demo.jvmReference;

import java.lang.ref.WeakReference;

/**
 * TODO
 *
 * @Author xuexc
 * @Date 2021/7/22 19:12
 * @Version 1.0
 */
public class WeakReferenceDemo {


    public static void main(String[] args) throws InterruptedException {
        //不能这样写,这个是强引用,栈上强引用指向对象
        //Student student = new Student("小红");
        //Teacher teacher = new Teacher(student);
        Teacher teacher = new Teacher(new Student("小红"));
        System.out.println(teacher.get());
        //启动参数可以加上gc日志-XX:+PrintGCDetails,确保gc触发
        System.gc();
        Thread.sleep(1000);
        System.out.println("gc完成");
        System.out.println(teacher.get());
    }

    public static class Student {
        private String name;

        Student(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Student{" +
                    "name='" + name + '\'' +
                    '}';
        }

        @Override
        protected void finalize() throws Throwable {
            super.finalize();
            System.out.println("回收Student对象");
        }
    }

    public static class Teacher extends WeakReference<Student> {

        /**
         * Creates a new weak reference that refers to the given object.  The new
         * reference is not registered with any queue.
         *
         * @param referent object the new weak reference will refer to
         */
        public Teacher(Student referent) {
            super(referent);
        }
    }
}
