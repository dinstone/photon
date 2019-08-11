/*
 * Copyright (C) 2013~2017 dinstone<dinstone@163.com>
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
package com.dinstone.photon.session;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.dinstone.photon.message.Response;
import com.dinstone.photon.message.Status;

/**
 * @author guojf
 * @version 1.0.0.2013-5-2
 */
public class ResponseFuture {

    private Lock lock = new ReentrantLock();

    private Condition ready = lock.newCondition();

    private boolean done;

    private Response response;

    /**
     *
     */
    public ResponseFuture() {
        super();
    }

    public Response get() throws InterruptedException {
        lock.lock();
        try {
            while (!done) {
                ready.await();
            }
            return getValue();
        } finally {
            lock.unlock();
        }

    }

    public Response get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
        lock.lock();
        try {
            if (!done) {
                boolean success = ready.await(timeout, unit);
                if (!success) {
                    throw new TimeoutException("operation timeout (" + timeout + " " + unit + ")");
                }
            }
            return getValue();
        } finally {
            lock.unlock();
        }
    }

    public void setResult(Response result) {
        setValue(result);
    }

    private Response getValue() {
        if (response.getStatus() != Status.SUCCESS) {
            throw new RuntimeException(response.getStatus().name());
        } else {
            return response;
        }
    }

    /**
     * @param result
     */
    private void setValue(Response result) {
        lock.lock();
        try {
            if (done) {
                return;
            }

            this.response = result;
            done = true;
            this.ready.signalAll();
        } finally {
            lock.unlock();
        }

    }

}
