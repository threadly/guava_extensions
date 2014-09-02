package org.threadly.guava;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.threadly.concurrent.SameThreadSubmitterExecutor;
import org.threadly.guava.FutureBridge.ThreadlyFuture;
import org.threadly.test.concurrent.TestRunnable;
import org.threadly.test.concurrent.TestableScheduler;

@SuppressWarnings("javadoc")
public class FutureBridgeThreadlyFutureTest {
  private com.google.common.util.concurrent.ListenableFuture<Object> guavaFuture;
  private ThreadlyFuture<Object> threadlyFuture;
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Before
  public void setup() {
    guavaFuture = mock(com.google.common.util.concurrent.ListenableFuture.class);
    threadlyFuture = new ThreadlyFuture(guavaFuture);
  }
  
  @After
  public void tearDown() {
    guavaFuture = null;
    threadlyFuture = null;
  }
  
  @Test
  public void constructorTest() {
    assertTrue(threadlyFuture.guavaListenableFuture == guavaFuture);
  }
  
  @SuppressWarnings({ "rawtypes", "unused", "unchecked" })
  @Test (expected = IllegalArgumentException.class)
  public void constructorFail() {
    new ThreadlyFuture(null);
  }
  
  @Test
  public void cancelTest() {
    threadlyFuture.cancel(true);
    
    verify(guavaFuture, times(1)).cancel(true);
  }
  
  @Test
  public void isCancelledTest() {
    threadlyFuture.isCancelled();
    
    verify(guavaFuture, times(1)).isCancelled();
  }
  
  @Test
  public void isDoneTest() {
    threadlyFuture.isDone();
    
    verify(guavaFuture, times(1)).isDone();
  }
  
  @Test
  public void getTest() throws InterruptedException, ExecutionException {
    Object getResult = new Object();
    when(guavaFuture.get()).thenReturn(getResult);
    
    assertTrue(threadlyFuture.get() == getResult);
    verify(guavaFuture, times(1)).get();
  }
  
  @Test
  public void getWithTimeoutTest() throws InterruptedException, ExecutionException, TimeoutException {
    Object getResult = new Object();
    when(guavaFuture.get(anyLong(), any(TimeUnit.class))).thenReturn(getResult);
    
    assertTrue(threadlyFuture.get(100, TimeUnit.MILLISECONDS) == getResult);
    verify(guavaFuture, times(1)).get(100, TimeUnit.MILLISECONDS);
  }
  
  @Test
  public void addListenerTest() {
    Runnable listener = new TestRunnable();
    threadlyFuture.addListener(listener);
    
    verify(guavaFuture, times(1)).addListener(listener, SameThreadSubmitterExecutor.instance());
  }
  
  @Test
  public void addListenerWithExecutorTest() {
    Runnable listener = new TestRunnable();
    Executor executor = new TestableScheduler();
    threadlyFuture.addListener(listener, executor);
    
    verify(guavaFuture, times(1)).addListener(listener, executor);
  }
  
  // TODO - add unit tests for adding callback
}
