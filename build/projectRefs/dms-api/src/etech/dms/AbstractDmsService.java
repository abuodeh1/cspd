package etech.dms;

import etech.resource.pool.PoolFactory;
import etech.resource.pool.PoolService;

public abstract class AbstractDmsService<Folder, DataDefinition, Field> implements DmsService<Folder, DataDefinition, Field> {

	private PoolService poolService;
	
	public void complete() {
		
		if(PoolFactory.isSingleResource(poolService)) {
		
			try {
				
				poolService.close();
				
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
	}

	public PoolService getPoolService() {
		return poolService;
	}

	public void setPoolService(PoolService poolService) {
		this.poolService = poolService;
	}

}
