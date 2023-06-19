package com.learning.bliss.demo.base.forkJoin;


import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class ForkJoinDemo {
    static ForkJoinPool pool = ForkJoinPool.commonPool();
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int[] numbers = new int[100];
        for (int i = 0; i < 100; i++) {
            numbers[i] = i + 1;
        }
        System.out.println(Arrays.toString(numbers));
        SumTask sumTask = new SumTask(numbers, 1, numbers.length);
        ForkJoinTask<Integer> forkJoinTask = pool.submit(sumTask);
        System.out.println(forkJoinTask.get());
    }

    /**
     * 任务Task类
     * RecursiveTask [rɪˈkɜːsɪv]-有返回结果的递归任务{@link java.util.concurrent.RecursiveTask}
     * RecursiveAction-无返回结果的递归任务{@link java.util.concurrent.RecursiveAction}
     */
    private static class SumTask extends RecursiveTask<Integer> {
        private int[] numbers;
        private int from;
        private int to;

        public SumTask(int[] numbers, int from, int to){
            this.numbers = numbers;
            this.from = from;
            this.to = to;
        }
        protected Integer compute() {
            if (to - from <=  10) {
                int sum = 0;
                while (to - from >= 0) {
                    sum = sum + numbers[from-1];
                    from ++;
                }
                return sum;
            } else {
                int middle = (from + to) >>> 1;
                SumTask taskLeft = new SumTask(numbers, from, middle);
                taskLeft.fork();//回调compute()方法，再次拆分
                
                SumTask taskRight = new SumTask(numbers, middle + 1, to);
                taskRight.fork();
                return taskLeft.join() + taskRight.join();
            }
        }
    }

}
