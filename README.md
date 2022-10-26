# Photon
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/dinstone/photon/blob/master/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/com.dinstone.photon/photon.svg?label=Maven%20Central)](https://search.maven.org/search?q=com.dinstone.photon)

# Overview
**Photon** is a message exchange framework. 

# Features
* Efficient custom protocol ([Photon](https://github.com/dinstone/photon) message exchange protocol)
    - Request-Response Pattern
    - One-way / Notify Pattern
	
* High-performance NIO socket framework support - Netty4

# Quick Start
select API dependency:

		<dependency>
			<groupId>com.dinstone.photon</groupId>
			<artifactId>photon</artifactId>
			<version>1.1.1</version>
		</dependency>
	
# Example
### message provider:
```java
	public static void main(String[] args) throws Exception {
        AcceptOptions acceptOptions = new AcceptOptions();
        acceptOptions.setEnableSsl(true);
        acceptOptions.setIdleTimeout(100000000);
        SelfSignedCertificate cert = new SelfSignedCertificate();
        acceptOptions.setPrivateKey(cert.key());
        acceptOptions.setCertChain(new X509Certificate[] { cert.cert() });
        Acceptor acceptor = new Acceptor(acceptOptions);
        acceptor.setMessageProcessor(new MessageProcessor() {

            @Override
            public void process(Connection connection, Request req) {
                LOG.info("Request is {}", req.getMsgId());
                Notice notice = new Notice();
                notice.setAddress("");
                notice.setContent(req.getContent());
                connection.sendMessage(notice);

                Response response = new Response();
                response.setMsgId(req.getMsgId());
                response.setStatus(Status.SUCCESS);
                response.setContent(req.getContent());
                connection.sendMessage(response);
            }

        });

        acceptor.bind(new InetSocketAddress("127.0.0.1", 4444));

        System.in.read();

        acceptor.destroy();
    }
```

### message consumer:
```java
	public static void main(String[] args) throws Throwable {
        ConnectOptions connectOptions = new ConnectOptions();
        connectOptions.setEnableSsl(true);
        Connector connector = new Connector(connectOptions);

        Connection connection = connector.connect(new InetSocketAddress("127.0.0.1", 4444));
        LOG.info("channel active is {}", connection.isActive());

        Request request = new Request();
        request.setMsgId(1);
        request.setTimeout(10000);
        request.setContent("Hello World".getBytes());

        LOG.info("async request is  {}", request);
        connection.sendRequest(request).thenAccept(response -> {
            LOG.info("async response is {}", response);
        });

        request = new Request();
        request.setMsgId(2);
        request.setTimeout(3000);

        LOG.info("sync request is  {}", request);
        Response response = connection.sendRequest(request).get();
        LOG.info("sync response is {}", response);
        System.in.read();

        connector.destroy();
    }
```
