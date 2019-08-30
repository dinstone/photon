package com.dinstone.photon.handler;

import com.dinstone.photon.message.Notice;
import com.dinstone.photon.processor.MessageProcessor;

public class NoticeHandler implements MessageHandler<Notice> {

    @Override
    public void handle(MessageContext context, MessageProcessor processor, Notice msg) {

        try {
            processor.process(context, msg);
        } catch (Exception e) {
            // ignore
        }
    }

}
