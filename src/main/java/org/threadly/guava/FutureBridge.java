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
   * @return a threadly ListenableFuture implementation 
   */
  public static <T> ListenableFuture<T> transformIntoThreadly(com.google.common.util.concurrent.ListenableFuture<T> guavaListenableFuture) {
    return new ThreadlyFuture<T>(guavaListenableFuture);
  }

  /**
   * Converts a threadly ListenableFuture into a guava implemented one.
   * 
   * @param threadlyListenableFuture the source threadly ListenableFuture
   * @return a guava ListenableFuture implementation 
   */
  public static <T> com.google.common.util.concurrent.ListenableFuture<T> transformIntoGuava(ListenableFuture<T> threadlyListenableFuture) {
    return new GuavaFuture<T>(threadlyListenableFuture);
  }

  /**
   * Converts a guava FutureCallback into a threadly implemented one.
   * 
   * @param guavaFutureCallback the source guava FutureCallback
   * @return a threadly FutureCallback implementation 
   */
  public static <T> FutureCallback<T> transformIntoThreadly(com.google.common.util.concurrent.FutureCallback<T> guavaFutureCallback) {
    return new ThreadlyFutureCallback<T>(guavaFutureCallback);
  }

  /**
   * Converts a threadly FutureCallback into a guava implemented one.
   * 
   * @param threadlyFutureCallback the source threadly FutureCallback
   * @return a guava FutureCallback implementation 
   */
  public static <T> com.google.common.util.concurrent.FutureCallback<T> transformIntoGuava(FutureCallback<T> threadlyFutureCallback) {
    return new GuavaFutureCallback<T>(threadlyFutureCallback);
  }
  
  protected static class ThreadlyFuture<T> implements ListenableFuture<T> {
    private final com.google.common.util.concurrent.ListenableFuture<T> guavaListenableFuture;
    
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
  
  protected static class GuavaFuture<T> implements com.google.common.util.concurrent.ListenableFuture<T> {
    private final ListenableFuture<T> threadlyListenableFuture;
    
    public GuavaFuture(ListenableFuture<T> threadlyListenableFuture) {
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
  
  protected static class ThreadlyFutureCallback<T> implements FutureCallback<T> {
    private final com.google.common.util.concurrent.FutureCallback<T> guavaFutureCallback;
    
    public ThreadlyFutureCallback(com.google.common.util.concurrent.FutureCallback<T> guavaFutureCallback) {
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
  
  protected static class GuavaFutureCallback<T> implements com.google.common.util.concurrent.FutureCallback<T> {
    private final FutureCallback<T> threadlyFutureCallback;
    
    public GuavaFutureCallback(FutureCallback<T> threadlyFutureCallback) {
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
