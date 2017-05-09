package io.sugo.user.group.utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 
 * @Description: 用BlockingQueue来实现Producer和Consumer,以控制程序的 阻塞 与 运行
 * @Author janpychou@qq.com
 * @CreateDate:   [May 13, 2015 10:10:58 AM]   
 *
 */
public class RunController {

    static BlockingQueue<String> apiServerQueue = new ArrayBlockingQueue<String>(1); 
    
    
    public static void blockApiServer() throws InterruptedException{
        apiServerQueue.take();
    }
    
    public static void unblockApiServer() throws InterruptedException{
        apiServerQueue.put("unblock");
    }
}
