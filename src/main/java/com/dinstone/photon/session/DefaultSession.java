package com.dinstone.photon.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.dinstone.loghub.Logger;
import com.dinstone.loghub.LoggerFactory;
import com.dinstone.photon.AttributeHelper;
import com.dinstone.photon.crypto.Crypto;
import com.dinstone.photon.message.Message;
import com.dinstone.photon.message.Notice;
import com.dinstone.photon.message.Request;
import com.dinstone.photon.message.Response;
import com.dinstone.photon.message.Status;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.GenericFutureListener;

public class DefaultSession implements Session {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultSession.class);

    private Map<Integer, ResponseFuture> futures = new ConcurrentHashMap<Integer, ResponseFuture>();

    private String sessionId;

    private Channel channel;

    public DefaultSession(Channel channel) {
        this.channel = channel;
        this.sessionId = channel.id().asLongText();
    }

    public Crypto getCrypto() {
        return AttributeHelper.getCipher(channel);
    }

    @Override
    public String sessionId() {
        return sessionId;
    }

    @Override
    public ChannelFuture write(Message msg) {
        return channel.writeAndFlush(msg);
    }

    @Override
    public boolean isActive() {
        return channel.isActive();
    }

    @Override
    public void oneway(Notice notice) {
        ChannelFuture cf = channel.writeAndFlush(notice);
        cf.addListener(new GenericFutureListener<ChannelFuture>() {

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    LOG.warn("send notice error", future.cause());
                }
            }

        });
    }

    @Override
    public Response sync(final Request request) throws InterruptedException, TimeoutException {
        final ResponseFuture responseFuture = new ResponseFuture(request.getId());
        addFuture(responseFuture);

        ChannelFuture cf = channel.writeAndFlush(request);
        cf.addListener(new GenericFutureListener<ChannelFuture>() {

            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (!future.isSuccess()) {
                    Response result = new Response();
                    result.setId(request.getId());
                    result.setStatus(Status.ERROR);
                    responseFuture.setResult(result);

                    removeFuture(request.getId());
                    LOG.warn("send request error", future.cause());
                }
            }

        });
        return responseFuture.get(request.getTimeout(), TimeUnit.MILLISECONDS);
    }

    @Override
    public ResponseFuture removeFuture(int messageId) {
        return futures.remove(messageId);
    }

    @Override
    public void addFuture(ResponseFuture future) {
        futures.put(future.getFutureId(), future);
    }

}
