package com.dinstone.photon.handler;

import com.dinstone.photon.message.Notice;

public class NoticeHandler implements MessageHandler<Notice> {

    @Override
    public void handle(MessageContext context, Notice notice) {

        context.getMessageProcessor().process(context, notice);
    }

}
