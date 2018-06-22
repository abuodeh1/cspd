package etech.dms.util;

import etech.resource.pool.PoolService;

public abstract class DataDefinitionUtility<T> extends AbstractUtility implements DataDefinitionUtils<T> {

	public DataDefinitionUtility(PoolService<?> poolService) {
		super(poolService);
	}

}
