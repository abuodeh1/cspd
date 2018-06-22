package etech.dms.util;

import etech.resource.pool.PoolService;

public abstract class AbstractUtility implements Utility {

	public PoolService<?> poolService;
	
	public AbstractUtility(PoolService<?> poolService) {
		this.poolService = poolService;
	}
	
	public PoolService<?> getPoolService() {
		return poolService;
	}
}
