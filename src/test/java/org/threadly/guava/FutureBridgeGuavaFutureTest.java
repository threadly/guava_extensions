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
import org.threadly.concurrent.future.ListenableFuture;
import org.threadly.guava.FutureBridge.GuavaFuture;
import org.threadly.test.concurrent.TestRunnable;
import org.threadly.test.concurrent.TestableScheduler;

@SuppressWarnings("javadoc")
public class FutureBridgeGuavaFutureTest {
  private ListenableFuture<Object> threadlyFuture;
  private GuavaFuture<Object> guavaFuture;
  
  @SuppressWarnings("unchecked")
  @Before
  public void setup() {
    threadlyFuture = mock(ListenableFuture.class);
    guavaFuture = new GuavaFuture<Object>(threadlyFuture);
  }
  
  @After
  public void tearDown() {
    threadlyFuture = null;
    guavaFuture = null;
  }
  
  @Test
  public void constructorTest() {
    assertTrue(guavaFuture.threadlyListenableFuture == threadlyFuture);
  }
  
  @SuppressWarnings({ "rawtypes", "unused", "unchecked" })
  @Test (expected = IllegalArgumentException.class)
  public void constructorFail() {
    new GuavaFuture(null);
  }
  
  @Test
  public void cancelTest() {
    guavaFuture.cancel(true);
    
    verify(threadlyFuture, times(1)).cancel(true);
  }
  
  @Test
  public void isCancelledTest() {
    guavaFuture.isCancelled();
    
    verify(threadlyFuture, times(1)).isCancelled();
  }
  
  @Test
  public void isDoneTest() {
    guavaFuture.isDone();
    
    verify(threadlyFuture, times(1)).isDone();
  }
  
  @Test
  public void getTest() throws InterruptedException, ExecutionException {
    Object getResult = new Object();
    when(threadlyFuture.get()).thenReturn(getResult);
    
    assertTrue(guavaFuture.get() == getResult);
    verify(threadlyFuture, times(1)).get();
  }
  
  @Test
  public void getWithTimeoutTest() throws InterruptedException, ExecutionException, TimeoutException {
    Object getResult = new Object();
    when(threadlyFuture.get(anyLong(), any(TimeUnit.class))).thenReturn(getResult);
    
    assertTrue(guavaFuture.get(100, TimeUnit.MILLISECONDS) == getResult);
    verify(threadlyFuture, times(1)).get(100, TimeUnit.MILLISECONDS);
  }
  
  @Test
  public void addListenerWithExecutorTest() {
    Runnable listener = new TestRunnable();
    Executor executor = new TestableScheduler();
    guavaFuture.addListener(listener, executor);
    
    verify(threadlyFuture, times(1)).addListener(listener, executor);
  }
}
