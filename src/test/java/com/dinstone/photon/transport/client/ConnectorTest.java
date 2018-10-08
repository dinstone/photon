package com.dinstone.photon.transport.client;

import java.net.InetSocketAddress;

import com.dinstone.photon.codec.CodecManager;
import com.dinstone.photon.session.Session;
import com.dinstone.photon.transport.TransportConfig;

import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

public class ConnectorTest {

	public static void main(String[] args) throws Throwable {
		CodecManager.getInstance().regist(String.class, new StringCodec());

		Connector connector = new Connector(new TransportConfig());
		Session session = connector.createSession(new InetSocketAddress("127.0.0.1", 4444));
		System.out.println("channel active is " + session.isActive());
		ChannelFuture cf = session.write("hello world");
		cf.addListener(new FutureListener<Void>() {

			@Override
			public void operationComplete(Future<Void> future) throws Exception {
				future.get();
			}

		});

		System.in.read();

		connector.dispose();
	}

}
