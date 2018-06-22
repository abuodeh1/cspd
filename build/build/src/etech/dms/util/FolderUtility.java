package etech.dms.util;

import etech.resource.pool.PoolService;

public abstract class FolderUtility<T, D, F> extends AbstractUtility implements FolderUtils<T, D, F>{

	public FolderUtility(PoolService<?> poolService) {
		super(poolService);
	}
	
}
