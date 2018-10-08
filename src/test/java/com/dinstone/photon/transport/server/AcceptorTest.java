package com.dinstone.photon.transport.server;

import java.io.IOException;

import com.dinstone.photon.codec.CodecManager;
import com.dinstone.photon.transport.client.StringCodec;

public class AcceptorTest {

	public static void main(String[] args) throws IOException {
		CodecManager.getInstance().regist(String.class, new StringCodec());

		Acceptor acceptor = new Acceptor().bind();

		System.in.read();

		acceptor.destroy();
	}

}
