/*
 * Copyright (C) ${year} dinstone<dinstone@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dinstone.photon.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

import com.dinstone.photon.message.Request;
import com.dinstone.photon.message.Response;
import com.dinstone.photon.message.Status;
import com.dinstone.photon.processor.MessageProcessor;

public class RequestHandler implements MessageHandler<Request> {

    @Override
    public void handle(MessageProcessor processor, MessageContext context, Request request) {

        try {
            processor.process(context, request);
        } catch (Throwable e) {
            if (e instanceof InvocationTargetException) {
                e = getTargetException((InvocationTargetException) e);
            }

            Response response = new Response();
            response.setId(request.getId());
            response.setHeaders(request.getHeaders());
            response.setStatus(Status.ERROR);
            response.setException(e);

            context.getConnection().write(response);
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
