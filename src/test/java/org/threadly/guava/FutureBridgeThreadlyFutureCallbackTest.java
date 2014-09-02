package org.threadly.guava;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.threadly.guava.FutureBridge.ThreadlyFutureCallback;

@SuppressWarnings("javadoc")
public class FutureBridgeThreadlyFutureCallbackTest {
  private com.google.common.util.concurrent.FutureCallback<Object> guavaCallback;
  private ThreadlyFutureCallback<Object> threadlyCallback;
  
  @SuppressWarnings("unchecked")
  @Before
  public void setup() {
    guavaCallback = mock(com.google.common.util.concurrent.FutureCallback.class);
    threadlyCallback = new ThreadlyFutureCallback<Object>(guavaCallback);
  }
  
  @After
  public void tearDown() {
    guavaCallback = null;
    threadlyCallback = null;
  }
  
  @Test
  public void constructorTest() {
    assertTrue(threadlyCallback.guavaFutureCallback == guavaCallback);
  }
  
  @SuppressWarnings({ "rawtypes", "unused", "unchecked" })
  @Test (expected = IllegalArgumentException.class)
  public void constructorFail() {
    new ThreadlyFutureCallback(null);
  }
  
  @Test
  public void handleResultTest() {
    Object result = new Object();
    threadlyCallback.handleResult(result);
    
    verify(guavaCallback, times(1)).onSuccess(result);
  }
  
  @Test
  public void handleFailureTest() {
    Exception failure = new Exception();
    threadlyCallback.handleFailure(failure);
    
    verify(guavaCallback, times(1)).onFailure(failure);
  }
}
