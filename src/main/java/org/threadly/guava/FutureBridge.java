package org.threadly.guava;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.threadly.concurrent.SameThreadSubmitterExecutor;
import org.threadly.concurrent.future.FutureCallback;
import org.threadly.concurrent.future.ListenableFuture;

/**
 * <p>Used to bridge between threadly's Future tools and guava's.  For example 
 * you can convert between threadly's {@link org.threadly.concurrent.future.ListenableFuture} 
 * and guava's com.google.common.util.concurrent.ListenableFuture.  Or convert 
 * between threadly's {@link org.threadly.concurrent.future.FutureCallback} and guava's 
 * com.google.common.util.concurrent.FutureCallback.</p>
 * 
 * @author jent - Mike Jensen
 */
public class FutureBridge {
  private FutureBridge() {
    // don't allow construction
  }
  
  /**
   * Converts a guava ListenableFuture into a threadly implemented one.
   * 
   * @param guavaListenableFuture the source guava ListenableFuture
   * @param <T> Type returned by the future's .get() call
   * @return a threadly ListenableFuture implementation 
   */
  public static <T> ListenableFuture<T> transformIntoThreadly(com.google.common.util.concurrent.ListenableFuture<T> guavaListenableFuture) {
    return new ThreadlyFuture<T>(guavaListenableFuture);
  }

  /**
   * Converts a threadly ListenableFuture into a guava implemented one.
   * 
   * @param threadlyListenableFuture the source threadly ListenableFuture
   * @param <T> Type returned by the future's .get() call
   * @return a guava ListenableFuture implementation 
   */
  public static <T> com.google.common.util.concurrent.ListenableFuture<T> transformIntoGuava(ListenableFuture<? extends T> threadlyListenableFuture) {
    return new GuavaFuture<T>(threadlyListenableFuture);
  }

  /**
   * Converts a guava FutureCallback into a threadly implemented one.
   * 
   * @param guavaFutureCallback the source guava FutureCallback
   * @param <T> Type of result returned to callback
   * @return a threadly FutureCallback implementation 
   */
  public static <T> FutureCallback<T> transformIntoThreadly(com.google.common.util.concurrent.FutureCallback<? super T> guavaFutureCallback) {
    return new ThreadlyFutureCallback<T>(guavaFutureCallback);
  }

  /**
   * Converts a threadly FutureCallback into a guava implemented one.
   * 
   * @param threadlyFutureCallback the source threadly FutureCallback
   * @param <T> Type of result returned to callback
   * @return a guava FutureCallback implementation 
   */
  public static <T> com.google.common.util.concurrent.FutureCallback<T> transformIntoGuava(FutureCallback<? super T> threadlyFutureCallback) {
    return new GuavaFutureCallback<T>(threadlyFutureCallback);
  }
  
  /**
   * <p>Threadly implementation which defers to a guava future.</p>
   * 
   * @author jent - Mike Jensen
   * @param <T> Type returned by the future's .get() call
   */
  protected static class ThreadlyFuture<T> implements ListenableFuture<T> {
    protected final com.google.common.util.concurrent.ListenableFuture<T> guavaListenableFuture;
    
    public ThreadlyFuture(com.google.common.util.concurrent.ListenableFuture<T> guavaListenableFuture) {
      if (guavaListenableFuture == null) {
        throw new IllegalArgumentException("Must supply future");
      }
      
      this.guavaListenableFuture = guavaListenableFuture;
    }
    
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
      return guavaListenableFuture.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
      return guavaListenableFuture.isCancelled();
    }

    @Override
    public boolean isDone() {
      return guavaListenableFuture.isDone();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
      return guavaListenableFuture.get();
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, 
                                                     ExecutionException,
                                                     TimeoutException {
      return guavaListenableFuture.get(timeout, unit);
    }

    @Override
    public void addListener(Runnable listener) {
      guavaListenableFuture.addListener(listener, SameThreadSubmitterExecutor.instance());
    }

    @Override
    public void addListener(Runnable listener, Executor executor) {
      guavaListenableFuture.addListener(listener, executor);
    }

    @Override
    public void addCallback(FutureCallback<? super T> callback) {
      com.google.common.util.concurrent.Futures.addCallback(guavaListenableFuture, 
                                                            transformIntoGuava(callback));
    }

    @Override
    public void addCallback(FutureCallback<? super T> callback, Executor executor) {
      com.google.common.util.concurrent.Futures.addCallback(guavaListenableFuture, 
                                                            transformIntoGuava(callback), 
                                                            executor);
    }
  }
  
  /**
   * <p>Guava implementation which defers to a threadly future.</p>
   * 
   * @author jent - Mike Jensen
   * @param <T> Type returned by the future's .get() call
   */
  protected static class GuavaFuture<T> implements com.google.common.util.concurrent.ListenableFuture<T> {
    protected final ListenableFuture<? extends T> threadlyListenableFuture;
    
    public GuavaFuture(ListenableFuture<? extends T> threadlyListenableFuture) {
      if (threadlyListenableFuture == null) {
        throw new IllegalArgumentException("Must supply future");
      }
      
      this.threadlyListenableFuture = threadlyListenableFuture;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
      return threadlyListenableFuture.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
      return threadlyListenableFuture.isCancelled();
    }

    @Override
    public boolean isDone() {
      return threadlyListenableFuture.isDone();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
      return threadlyListenableFuture.get();
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, 
                                                     ExecutionException,
                                                     TimeoutException {
      return threadlyListenableFuture.get(timeout, unit);
    }

    @Override
    public void addListener(Runnable listener, Executor executor) {
      threadlyListenableFuture.addListener(listener, executor);
    }
  }
  
  /**
   * <p>Threadly implementation which defers to a guava callback.</p>
   * 
   * @author jent - Mike Jensen
   * @param <T> Type of result returned to callback
   */
  protected static class ThreadlyFutureCallback<T> implements FutureCallback<T> {
    protected final com.google.common.util.concurrent.FutureCallback<? super T> guavaFutureCallback;
    
    public ThreadlyFutureCallback(com.google.common.util.concurrent.FutureCallback<? super T> guavaFutureCallback) {
      if (guavaFutureCallback == null) {
        throw new IllegalArgumentException("Must supply callback");
      }
      
      this.guavaFutureCallback = guavaFutureCallback;
    }
    
    @Override
    public void handleResult(T result) {
      guavaFutureCallback.onSuccess(result);
    }

    @Override
    public void handleFailure(Throwable t) {
      guavaFutureCallback.onFailure(t);
    }
  }
  
  /**
   * <p>Guava implementation which defers to a threadly callback.</p>
   * 
   * @author jent - Mike Jensen
   * @param <T> Type of result returned to callback
   */
  protected static class GuavaFutureCallback<T> implements com.google.common.util.concurrent.FutureCallback<T> {
    protected final FutureCallback<? super T> threadlyFutureCallback;
    
    public GuavaFutureCallback(FutureCallback<? super T> threadlyFutureCallback) {
      if (threadlyFutureCallback == null) {
        throw new IllegalArgumentException("Must supply callback");
      }
      
      this.threadlyFutureCallback = threadlyFutureCallback;
    }
    
    @Override
    public void onSuccess(T result) {
      threadlyFutureCallback.handleResult(result);
    }

    @Override
    public void onFailure(Throwable t) {
      threadlyFutureCallback.handleFailure(t);
    }
  }
}
