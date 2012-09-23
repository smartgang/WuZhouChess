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
	private boolean isPausing;//��ͣʱʹ�ã���Ϊ�޷�����ֹͣ�Ϳ�ʼ��������ʱֻ�������������λ
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
	//��ͣ
	public void pauseCounter()
	{
		isPausing=true;
	}
	//���ü���ֵ
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
            	while(isPausing)sleep(500);//��ͣ��ÿ0.5����һ��
                sleep(1000);
                counter++;
                //ÿһ�뽫���ɵļ���ֵ����ȥһ��
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
	//ֹͣ
	public void stopCounter()
	{
		isRunning=false;
		isPausing=false;
		counter=0;
	}
	
}
