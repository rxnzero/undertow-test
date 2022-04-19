package com.dhlee.undertow.server;

import javax.net.ssl.SSLContext;
import javax.servlet.ServletException;

import org.xnio.Options;

import com.dhlee.util.ThreadDumpUtil;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.handlers.IPAddressAccessControlHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.RequestLimit;
import io.undertow.server.handlers.RequestLimitingHandler;
import io.undertow.server.handlers.ResponseCodeHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletInfo;

public class TestHttpServer {

	public TestHttpServer() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TestHttpServer test = new TestHttpServer();
		try {
			Integer listenPort = 8081;
			Integer max_connection = 10;
			Integer max_queue = 100;
			Integer ioThreads = 1;
			Integer workerThreads = ioThreads * 8;

			test.start(listenPort, max_connection, max_queue, ioThreads, workerThreads);
			System.out.println(ThreadDumpUtil.dump());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void start(Integer listenPort, Integer max_connection, Integer max_queue, Integer ioThreads,
			Integer workerThreads) throws Exception {
		String httpsUseYn = "Y";
		String httpsKeyStore = null;
		String httpsKeyPass = null;
		String allowIp = null;
		Undertow server = null;
		
		allowIp = "127.0.0.1,192.168.10.87";
		
		if (listenPort != 0) {
			ServletInfo servletInfo = Servlets.servlet(AdapterServlet.class);
//			servletInfo.addMapping(AdapterServlet.class.getSimpleName().toLowerCase());
			servletInfo.addMapping("/api/*");
			servletInfo.addMapping("/API/*");
			System.out.println("AdapterServlet mappings : " + servletInfo.getMappings().toString());
			DeploymentInfo servletBuilder = Servlets.deployment().setClassLoader(TestHttpServer.class.getClassLoader())
					.setDeploymentName("HttpDynamicCustomAdapter").setContextPath("/").addServlet(servletInfo);

			DeploymentManager manager = Servlets.defaultContainer().addDeployment(servletBuilder);
			manager.deploy();

			PathHandler path = null;
			IPAddressAccessControlHandler ipAddressAccessControlHandler = null;
			RequestLimitingHandler requestLimitHandler = null;

			try {
				path = Handlers.path(Handlers.redirect("/")).addPrefixPath("/", manager.start());
				if (allowIp != null && allowIp.length() > 1) {
					String[] allowIps = allowIp.split(",");
					ipAddressAccessControlHandler = Handlers.ipAccessControl(path, false);
					for (int i = 0; i < allowIps.length; i++) {
						ipAddressAccessControlHandler.addAllow(allowIps[i]);
					}
				}

				RequestLimit requestLimit = new RequestLimit(max_connection, max_queue);
				requestLimit.setFailureHandler(ResponseCodeHandler.HANDLE_500);

				if (ipAddressAccessControlHandler != null) {
					requestLimitHandler = Handlers.requestLimitingHandler(requestLimit, ipAddressAccessControlHandler);
				} else {
					requestLimitHandler = Handlers.requestLimitingHandler(requestLimit, path);
				}

			} catch (ServletException e) {
				e.printStackTrace();
			}

			SSLContext sslContext = null;
			if ("Y".equals(httpsUseYn)) {
				if ((httpsKeyStore != null && httpsKeyStore.length() > 1)
						&& (httpsKeyPass != null && httpsKeyPass.length() > 1)) {
					sslContext = HttpsGenerator.createSSLContext(httpsKeyStore, httpsKeyPass);
				}
			}

			Undertow.Builder builder = Undertow.builder();
			builder
//			.setServerOption(UndertowOptions.ENABLE_HTTP2, true)
//            .setServerOption(UndertowOptions.HTTP2_SETTINGS_MAX_CONCURRENT_STREAMS,  1)
//            .setServerOption(UndertowOptions.MAX_CONCURRENT_REQUESTS_PER_CONNECTION, 1)
					.setServerOption(UndertowOptions.NO_REQUEST_TIMEOUT, 10 * 1000)
//            .setWorkerOption(Options.WORKER_IO_THREADS, 1)
//            .setWorkerOption(Options.WORKER_TASK_CORE_THREADS, 5)
//            .setWorkerOption(Options.WORKER_TASK_MAX_THREADS, 10)
					.setWorkerOption(Options.WORKER_TASK_KEEPALIVE, 1 * 1000) // ms
					.setIoThreads(ioThreads).setWorkerThreads(workerThreads)
					.setSocketOption(Options.READ_TIMEOUT, 1 * 1000) // ms
			;

//			builder.setHandler(new HttpHandler() {
//                @Override
//                public void handleRequest(final HttpServerExchange exchange) throws Exception {
//                	System.out.println("Client address is: "
//                			+ exchange.getConnection().getPeerAddress().toString());
//                    if (exchange.isInIoThread()) {
//                        exchange.dispatch(this);
//                        return;
//                    }
//                    exchange.setStatusCode(200);
//                }
//            });
			if (sslContext != null) {
				builder.addHttpsListener(listenPort, "0.0.0.0", sslContext).setHandler(requestLimitHandler);

			} else {
				builder.addHttpListener(listenPort, "0.0.0.0").setHandler(requestLimitHandler);
			}
			server = builder.build();

			server.start();
			System.out.println("getIoThreadCount = " + server.getWorker().getIoThreadCount());
			printHeap();

		}
	}

	public static void printHeap() {
		long heapSize = Runtime.getRuntime().totalMemory();
		long heapMaxSize = Runtime.getRuntime().maxMemory();
		long heapFreeSize = Runtime.getRuntime().freeMemory();
		ThreadGroup it = Thread.currentThread().getThreadGroup();
		int threadCount = it.activeCount();

		System.out.println("heapsize=" + formatSize(heapSize) + " max=" + formatSize(heapMaxSize) + " free="
				+ formatSize(heapFreeSize) + " threadCount=" + threadCount + " processor="
				+ Runtime.getRuntime().availableProcessors());
	}

	public static String formatSize(long v) {
		if (v < 1024)
			return v + " B";
		int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
		return String.format("%.1f %sB", (double) v / (1L << (z * 10)), " KMGTPE".charAt(z));
	}
}
