package yuown.bulk.pool;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public interface CompletableExecutorService extends ExecutorService {

	@Override
	<T> CompletableFuture<T> submit(Callable<T> task);

	@Override
	<T> CompletableFuture<T> submit(Runnable task, T result);

	@Override
	CompletableFuture<?> submit(Runnable task);
}
