package job;

import play.jobs.Job;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * User: 刘建力(liujianli@gtadata.com))
 * Date: 13-3-1
 * Time: 下午12:04
 * 功能描述:
 */
//@Every("1s")
public class GetEntrustSignalMessage extends Job {
    private AtomicBoolean RUNNING = new AtomicBoolean(true);

    @Override
    public void doJob() throws Exception {

      /*  //while (RUNNING.get()){
        while (true) {
            System.out.println("===========start get signal");
            String message = "13245678" + System.currentTimeMillis();//从redis获取消息
            if (message != null) {
                EntrustSignalMessageConsumer.getInstance().consumer(message);
            }
            try {
                Thread.sleep(2000L);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }


}
