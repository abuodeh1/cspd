package etech.resource.pool;

public interface PoolService<T> {

	void close() throws Exception; 
	
	<T> void close(T t); 
	
	<T> T get();
}
