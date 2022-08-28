package com.learning.bliss.base.future;

import com.alibaba.fastjson.JSONObject;

import java.util.concurrent.*;

public class FutureDemo {
    static ExecutorService executorService = Executors.newSingleThreadExecutor();
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FutureTask<JSONObject> futureTask = new FutureTask<JSONObject>(FutureDemo::call);
        new Thread(futureTask).start();//交给线程执行，因为Thread只支持包装Runnable Thread(Runnable target)
        JSONObject result = futureTask.get();//获取执行结果
        System.out.println(result);
        //Callable是如何被执行的
        Future<Integer> f = executorService.submit(() -> 1);
        System.out.println(f.get());
        executorService.shutdown();
    }
    private static JSONObject call() {
        JSONObject json = new JSONObject(2);
        json.put("张三", "男");
        json.put("李四", "女");
        return json;
    }
}
