package org.threadly.guava;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.threadly.concurrent.future.FutureCallback;
import org.threadly.guava.FutureBridge.GuavaFutureCallback;

@SuppressWarnings("javadoc")
public class FutureBridgeGuavaFutureCallbackTest {
  private FutureCallback<Object> threadlyCallback;
  private GuavaFutureCallback<Object> guavaCallback;
  
  @SuppressWarnings("unchecked")
  @Before
  public void setup() {
    threadlyCallback = mock(FutureCallback.class);
    guavaCallback = new GuavaFutureCallback<Object>(threadlyCallback);
  }
  
  @After
  public void tearDown() {
    guavaCallback = null;
    guavaCallback = null;
  }
  
  @Test
  public void constructorTest() {
    assertTrue(guavaCallback.threadlyFutureCallback == threadlyCallback);
  }
  
  @SuppressWarnings({ "rawtypes", "unused", "unchecked" })
  @Test (expected = IllegalArgumentException.class)
  public void constructorFail() {
    new GuavaFutureCallback(null);
  }
  
  @Test
  public void onSuccessTest() {
    Object result = new Object();
    guavaCallback.onSuccess(result);
    
    verify(threadlyCallback, times(1)).handleResult(result);
  }
  
  @Test
  public void onFailureTest() {
    Exception failure = new Exception();
    guavaCallback.onFailure(failure);
    
    verify(threadlyCallback, times(1)).handleFailure(failure);
  }
}
