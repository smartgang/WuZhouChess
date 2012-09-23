/**
 * 
 */
package com.example.classes;
import android.os.Handler;
import android.os.Message;

/**
 * @author SmartGang
 *
 */
public class TimeCounter extends Thread {

	private int counter;
	private Handler handler;
	private boolean isPausing;//暂停时使用，因为无法控制停止和开始，所以暂时只能增加这个控制位
	private boolean isRunning;
	
	
	/**
	 * @param handler
	 */
	public TimeCounter(Handler handler) {
		super();
		this.handler = handler;
		isRunning=false;
		isPausing=false;
		counter=0;
	}
	/**
	 * @return the couter
	 */
	public int getCouter() {
		return counter;
	}
	/**
	 * @return the isRunning
	 */
	public boolean isRunning() {
		return isRunning;
	}
	//暂停
	public void pauseCounter()
	{
		isPausing=true;
	}
	//重置计数值
	public void resetCounter()
	{
		counter=0;
	}
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		while(isRunning)
		{			
            try {
            	while(isPausing)sleep(500);//暂停后每0.5秒检测一次
                sleep(1000);
                counter++;
                //每一秒将当成的计数值发出去一次
                Message msg=new Message();
                msg.obj=counter;
                msg.what=counter;
                handler.sendMessage(msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
		}
	}
	public void startCounter()
	{
		isRunning=true;
		isPausing=false;
	}
	//停止
	public void stopCounter()
	{
		isRunning=false;
		isPausing=false;
		counter=0;
	}
	
}
