package etech.dms.util;

import etech.resource.pool.PoolService;

public abstract class DocumentUtility<T> extends AbstractUtility implements DocumentUtils<T> {

	public DocumentUtility(PoolService<?> poolService) {
		super(poolService);
		// TODO Auto-generated constructor stub
	}

}
