package com.dinstone.photon.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

import com.dinstone.photon.message.Request;
import com.dinstone.photon.processor.MessageProcessor;

public class RequestHandler implements MessageHandler<Request> {

    @Override
    public void handle(MessageContext context, MessageProcessor processor, Request msg) {

        try {
            processor.process(context, msg);
        } catch (Throwable e) {
            Throwable t = null;
            if (e instanceof InvocationTargetException) {
                t = getTargetException((InvocationTargetException) e);
            }
            
        }

    }

    private Throwable getTargetException(InvocationTargetException e) {
        Throwable t = e.getTargetException();
        if (t instanceof UndeclaredThrowableException) {
            UndeclaredThrowableException ut = (UndeclaredThrowableException) t;
            t = ut.getCause();
            if (t instanceof InvocationTargetException) {
                return getTargetException((InvocationTargetException) t);
            }
        }
        return t;
    }

}
