package etech.dms.util;

import etech.resource.pool.PoolService;

public abstract class CabinetUtility<T> extends AbstractUtility implements CabinetUtils<T> {

	public CabinetUtility(PoolService<?> poolService) {
		super(poolService);
		// TODO Auto-generated constructor stub
	}

}
