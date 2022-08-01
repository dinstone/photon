# What
**Photon** is a a message exchange framework. 

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
			<version>1.1.0</version>
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
        acceptor.setMessageProcessor(new DefaultMessageProcessor() {

            @Override
            public void process(Connection connection, Request msg) {
                Request req = (Request) msg;
                LOG.info("Request is {}", req.getMsgId());
                Notice notice = new Notice();
                notice.setAddress("");
                notice.setContent(req.getContent());
                connection.send(notice);

                Response response = new Response();
                response.setMsgId(req.getMsgId());
                response.setStatus(Status.SUCCESS);
                response.setContent(req.getContent());
                connection.send(response);
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

        Response response = connection.sync(request);
        LOG.info("sync response is {}", response.headers());

        request.setMsgId(2);
        request.setTimeout(3000);
        connection.async(request).addListener(new GenericFutureListener<Future<Response>>() {

            @Override
            public void operationComplete(Future<Response> future) throws Exception {
                try {
                    LOG.info("thread {}", Thread.currentThread().getName());
                    Response response = future.get();
                    LOG.info("async response is {}", response.headers());
                } catch (Throwable e) {
                    e.printStackTrace();
                }

            }
        });

        System.in.read();

        connector.destroy();
    }
```
