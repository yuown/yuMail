package yuown.bulk.pool;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class DelegatingCompletableExecutorService extends DelegatingExecutorService implements CompletableExecutorService {

	public DelegatingCompletableExecutorService(ExecutorService executorService) {
		super(executorService);
	}

	@Override
	public <T> CompletableFuture<T> submit(Callable<T> task) {
		final CompletableFuture<T> cf = new CompletableFuture<>();
		delegate.submit(() -> {
			try {
				cf.complete(task.call());
			} catch (CancellationException e) {
				cf.cancel(true);
			} catch (Exception e) {
				cf.completeExceptionally(e);
			}
		});
		return cf;
	}

	@Override
	public <T> CompletableFuture<T> submit(Runnable task, T result) {
		return submit(callable(task, result));
	}

	@Override
	public CompletableFuture<?> submit(Runnable task) {
		return submit(callable(task));
	}

}
