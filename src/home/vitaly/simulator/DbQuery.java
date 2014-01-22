/**************************************************************************************
 * Copyright (C) 2014 Vitaly&Pavel team. All rights reserved.                         *
 *                                                                                    *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package home.vitaly.simulator;

import home.vitaly.datamodel.Transaction;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class DbQuery {

	int 	min		=0;
	int 	PG		=100;
	int 	nRows 	=0;  // 0= без ограничений

	private Session session;
	Query q, q2;
	private List<Transaction> trl;
	private Transaction tr;
	int index=0;
	int max=min+PG;
	int maxRow = min+nRows;
	
	public DbQuery() {
		session =new Configuration().configure().buildSessionFactory().openSession();
		q = session.createQuery( "from TransactionImpl where id > :minval and id < :maxval" );
		q2 = session.createQuery( "from TransactionImpl where ttime = :timeStamp" );
	}
	
	public Transaction getTr() {
		return( tr) ; 
		}

	@SuppressWarnings("unchecked")
	public boolean next() {
		boolean ret=true;

		if(nRows!=0 && index+min>maxRow) return false;  // достиг верхнего порога

		if(trl==null) {
			q.setInteger("minval", min);
			q.setInteger("maxval",max);
			trl = q.list();
		tr=trl.get(index++);
			session.clear();
		}
		else if(index< trl.size()) { tr=trl.get(index++); }
		else {
			min+=PG;
			max+=PG;
			index=0;
			q.setInteger("minval", min);
			q.setInteger("maxval",max);
			trl = q.list();
			session.clear();
			if(trl.size()>0) { tr=trl.get(index++); }
			else { ret=false; tr=null; }
		}
	return ret;
	}

	public List<Transaction> trPerSec(String timeStamp) {
		q2.setString("timeStamp", timeStamp);
		@SuppressWarnings("unchecked")
		List<Transaction> trlst=q2.list();
		session.clear();
		return trlst;
	}
}
