/**************************************************************************************
 * Copyright (C) 2014 Vitaly&Pavel team. All rights reserved.                         *
 *                                                                                    *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package home.vitaly.simulator;

import home.vitaly.datamodel.Transaction;

import javax.jms.JMSException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class RunSimulator {

	DbQuery dbq = new DbQuery();
	EnqueueMessage mq = new EnqueueMessage();
	
	public static void main(String[] args) throws JMSException {
        
		if(args.length<1) System.out.println("First argument must be S -max or T -real time event speed");
		RunSimulator m = new RunSimulator();
		if(args.length==0 || args[0].equals("S")) { m.letsGoMaxSpeed(); }
		else  { m.letsGoRealTime(); }
	}		
	
public void letsGoMaxSpeed() throws JMSException {		
		int tik=0;
		mq.open();
		while(dbq.next()) {
			tik++;
			Transaction tr = dbq.getTr();
			System.out.print(tik + "\r");
			mq.enqueueTransaction(tr);
		}
		mq.close();
	}

public void letsGoRealTime() throws JMSException {
	mq.open();
	for(long tik=10*60*60; tik < 24*60*60; tik++)
	{
		String timeStamp = getTimeStamp(tik);
		List<Transaction> trlst = dbq.trPerSec(timeStamp);
		System.out.print("\r"+timeStamp+" ");
		for(Transaction tr : trlst ) {
			mq.enqueueTransaction(tr);
//			System.out.print("*");
            System.out.println("TR:"+tr);
        }
//		try 
//	    {
//	        Thread.sleep(1);
//	    } catch (InterruptedException e) 
//	    	{
//	    	mq.close();
//	    	System.exit(-1); 
//	    	}
		System.out.print("\n");
	}

}

private String getTimeStamp(Long seconds) {
	Date date = new Date(seconds * 1000);
	SimpleDateFormat sdf = new SimpleDateFormat("HHmmss", Locale.ENGLISH);
	sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
//	String formattedDate = sdf.format(date);
//	System.out.println(formattedDate); 
	return sdf.format(date);
	}
}

