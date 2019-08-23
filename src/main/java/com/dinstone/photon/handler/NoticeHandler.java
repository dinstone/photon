package com.dinstone.photon.handler;

import com.dinstone.photon.message.Notice;
import com.dinstone.photon.processor.MessageProcessor;

public class NoticeHandler implements MessageHandler<Notice> {

    @Override
    public void handle(MessageContext context, MessageProcessor processor, Notice msg) {

        processor.process(context, msg);
    }

}
