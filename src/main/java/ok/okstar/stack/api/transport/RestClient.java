package ok.okstar.stack.api.transport;

import jakarta.ws.rs.*;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.ext.ContextResolver;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ok.okstar.stack.api.auth.AuthenticationMode;
import ok.okstar.stack.api.auth.AuthenticationToken;
import ok.okstar.stack.api.exception.ErrorResponse;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.moxy.json.MoxyJsonConfig;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.moxy.xml.MoxyXmlFeature;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

/**
 * The Class RestClient.
 */
@Setter
@Getter
@Slf4j
public final class RestClient {

	static SSLContext sslContext;

	static {
		sslContext = createSSLContext();
	}

	private static SSLContext createSSLContext() {
		TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}
		}};

		try {
			var context = SSLContext.getInstance("TLS");
			context.init(null, trustAllCerts, new SecureRandom());
//            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
			return context;
		} catch (KeyManagementException | NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	static ClientConfig clientConfig;
	static ExecutorService executorService;

	static {
		executorService = Executors.newCachedThreadPool();
	}

	/**
	 * The uri.
	 */
	private String baseURI;

	/**
	 * The token.
	 */
	private AuthenticationToken token;

	/**
	 * The password.
	 */
	private String password;

	/**
	 * The connection timeout.
	 */
	private int connectionTimeout;

	/**
	 * The headers.
	 */
	private MultivaluedMap<String, Object> headers;

	/**
	 * The media type.
	 */
	private SupportedMediaType mediaType;

	private final Configuration configuration;

//    private final Client client;

	/**
	 * The Constructor.
	 *
	 * @param builder the builder
	 */
	private RestClient(RestClientBuilder builder) {
		this.baseURI = builder.baseURI;
		this.connectionTimeout = builder.connectionTimeout;
		this.setHeaders(builder.headers);
		this.token = builder.token;
		this.mediaType = builder.mediaType;
		this.configuration = createConfiguration();
//        this.client = createRestClient();
	}


	private Configuration createConfiguration() {
		ClientConfig config = new ClientConfig();
		// Set connection timeout
		if (this.connectionTimeout != 0) {
			config.property(ClientProperties.CONNECT_TIMEOUT, this.connectionTimeout);
			config.property(ClientProperties.READ_TIMEOUT, this.connectionTimeout);
		}
		config.property("jersey.config.apache.client.maxTotal", 100);
		config.property("jersey.config.apache.client.maxPerRoute", 20);

		config.register(MoxyJsonFeature.class)
				.register(MoxyXmlFeature.class)
				.register(createMoxyJsonResolver());
		config.executorService(executorService);
//        config.property("jersey.config.client.async.executor", config.getExecutorService());
		config.register(new org.glassfish.jersey.spi.ExecutorServiceProvider() {

			@Override
			public ExecutorService getExecutorService() {
				return executorService;
			}

			@Override
			public void dispose(ExecutorService executorService) {
//                executorService.shutdown();
			}
		});


//        if (this.baseURI.startsWith("https")) {
//            //ssl
//            config..sslContext(sslContext)
//                    .hostnameVerifier((hostname, sslSession) -> {
//                        log.info("Verify host:{}", hostname);
//                        return true;
//                    });
//        }


		return config;
	}

	/**
	 * Create rest client.
	 *
	 * @return the client
	 */
	private Client createRestClient() {
		Client client = JerseyClientBuilder.createClient(configuration);
		log.info("Created client:{}", client);
		return client;
	}

	public static ContextResolver<MoxyJsonConfig> createMoxyJsonResolver() {
		return new MoxyJsonConfig()
				.setAttributePrefix("")
				.setValueWrapper("value")
				.property(JAXBContextProperties.JSON_WRAPPER_AS_ARRAY_NAME, true)
				.resolver();
	}

	/**
	 * The Class Builder.
	 */
	public static class RestClientBuilder {

		/**
		 * The uri.
		 */
		private String baseURI;

		/**
		 * The connection timeout.
		 */
		private int connectionTimeout;

		/**
		 * The headers.
		 */
		private MultivaluedMap<String, Object> headers;

		/**
		 * The token.
		 */
		private AuthenticationToken token;

		/**
		 * The media type.
		 */
		private SupportedMediaType mediaType;

		/**
		 * The Constructor.
		 *
		 * @param baseUri the base uri
		 */
		public RestClientBuilder(String baseUri) {
			this.headers = new MultivaluedHashMap<String, Object>();
			this.baseURI = baseUri;
		}

		/**
		 * Connection timeout.
		 *
		 * @param connectionTimeout the connection timeout
		 * @return the builder
		 */
		public RestClientBuilder connectionTimeout(int connectionTimeout) {
			this.connectionTimeout = connectionTimeout;
			return this;
		}

		/**
		 * Authentication token.
		 *
		 * @param token the token
		 * @return the rest client builder
		 */
		public RestClientBuilder authenticationToken(AuthenticationToken token) {
			if (token.getAuthMode() == AuthenticationMode.SHARED_SECRET_KEY) {
				headers.add(HttpHeaders.AUTHORIZATION, token.getSharedSecretKey());
			} else if (token.getAuthMode() == AuthenticationMode.BASIC_AUTH) {
				String base64 = Base64.getEncoder().encodeToString((token.getUsername() + ":" + token.getPassword()).getBytes());
				headers.add(HttpHeaders.AUTHORIZATION, "Basic " + base64);
			}

			this.token = token;
			return this;
		}

		/**
		 * Headers.
		 *
		 * @param headers the headers
		 * @return the rest client builder
		 */
		public RestClientBuilder headers(MultivaluedMap<String, Object> headers) {
			this.headers = headers;
			return this;
		}

		/**
		 * .
		 *
		 * @param mediaType the mediaType
		 * @return the rest client builder
		 */
		public RestClientBuilder mediaType(SupportedMediaType mediaType) {
			this.mediaType = mediaType;
			return this;
		}

		/**
		 * Builds the.
		 *
		 * @return the rest client resource
		 */
		public RestClient build() {
			return new RestClient(this);
		}

	}


	/**
	 * Gets the
	 *
	 * @param <T>              the generic type
	 * @param restPath         the rest path
	 * @param expectedResponse the expected response
	 * @param queryParams      the query params
	 * @return the t
	 */
	public <T> T get(
			String restPath,
			Class<T> expectedResponse,
			Map<String, String> queryParams) {
		log.info("GET: {}", restPath);
		return call(HttpMethod.GET,
				restPath,
				expectedResponse,
				null,
				queryParams);
	}

	public <T> T post(
			String restPath,
			Class<T> expectedResponse,
			Object payload,
			Map<String, String> queryParams
	) {
		log.info("POST: {}", restPath);
		return call(HttpMethod.POST,
				restPath,
				expectedResponse,
				payload,
				queryParams);
	}

	/**
	 * Post.
	 *
	 * @param restPath    the rest path
	 * @param payload     the payload
	 * @param queryParams the query params
	 * @return the response
	 */
	public Response post(
			String restPath,
			Object payload,
			Map<String, String> queryParams) {
		log.debug("POST: {}", restPath);
		return call(HttpMethod.POST,
				restPath,
				Response.class,
				payload,
				queryParams);
	}

	/**
	 * Put.
	 *
	 * @param restPath    the rest path
	 * @param payload     the payload
	 * @param queryParams the query params
	 * @return the response
	 */
	public Response put(
			String restPath,
			Object payload,
			Map<String, String> queryParams) {
		log.debug("PUT: {}", restPath);
		return call(HttpMethod.PUT,
				restPath,
				Response.class,
				payload,
				queryParams);
	}

	/**
	 * Delete.
	 *
	 * @param restPath    the rest path
	 * @param queryParams the query params
	 * @return the response
	 */
	public Response delete(
			String restPath,
			Map<String, String> queryParams) {
		log.debug("DELETE: {}", restPath);
		return call(HttpMethod.DELETE,
				restPath,
				Response.class,
				null,
				queryParams);
	}

	/**
	 * Gets the.
	 *
	 * @param <T>          the generic type
	 * @param methodName   the method name
	 * @param restPath     the rest path
	 * @param tClass the clazz
	 * @param payload      the payload
	 * @param queryParams  the query params
	 * @return the t
	 */
	@SuppressWarnings("unchecked")
	public <T> T call(
			String methodName,
			String restPath,
			Class<T> tClass,
			Object payload,
			Map<String, String> queryParams) {

		AtomicReference<T> result = new AtomicReference<>();
		try {
			log.info("Call=> {}", getBaseURI() + restPath);
			createWebTarget(restPath, queryParams, webTarget -> {
				var response = (webTarget
						.request()
						.headers(headers)
						.accept(mediaType.getMediaType())
						.method(methodName,
								Entity.entity(payload, mediaType.getMediaType()),
								Response.class));

				if (tClass.getName().equals(Response.class.getName())) {
					result.set((T) response);
				}

				if (isStatusCodeOK(response, restPath)) {
					result.set((T) response.readEntity(tClass));
				}
			});
		} catch (Exception e) {
			throw new WebApplicationException("Call error:" + e.getMessage());
		}
		return result.get();
	}

	/**
	 * Checks if is status code ok.
	 *
	 * @param response the response
	 * @param uri      the uri
	 * @return true, if is status code ok
	 */
	private boolean isStatusCodeOK(Response response, String uri) {
		if (response.getStatus() == Response.Status.OK.getStatusCode()
				|| response.getStatus() == Response.Status.CREATED.getStatusCode()) {
			return true;
		} else if (response.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode()) {
			throw new NotAuthorizedException("UNAUTHORIZED: Your credentials are wrong. "
					+ "Please check your username/password or the secret key.", response);
		} else if (response.getStatus() == Response.Status.FORBIDDEN.getStatusCode()) {
			throw new ForbiddenException(response);
		} else if (response.getStatus() == Response.Status.CONFLICT.getStatusCode()
				|| response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()
				|| response.getStatus() == Response.Status.BAD_REQUEST.getStatusCode()) {
			ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
			throw new ClientErrorException(String.valueOf(errorResponse), response);
		} else if (response.getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
			throw new WebApplicationException("Server Error", response);
		} else {
			throw new WebApplicationException("Unsupported status", response);
		}
	}

	/**
	 * Creates the web target.
	 *
	 * @param restPath    the rest path
	 * @param queryParams the query params
	 * @param consumer    Consumer the webTarget
	 */
	private void createWebTarget(String restPath,
								 Map<String, String> queryParams,
								 Consumer<WebTarget> consumer) {
		try (Client client = createRestClient()) {
			URI u;
			if (!baseURI.endsWith("/") && !restPath.startsWith("/")) {
				u = new URI(this.baseURI + "/" + restPath);
			} else {
				u = new URI(this.baseURI + restPath);
			}
			WebTarget webTarget = client.target(u);
			if (queryParams != null) {
				for (Map.Entry<String, String> entry : queryParams.entrySet()) {
					if (entry.getKey() != null && entry.getValue() != null) {
						log.debug("Put query param: {} => {}", entry.getKey(), entry.getValue());
						webTarget = webTarget.queryParam(entry.getKey(), entry.getValue());
					}
				}
			}
			consumer.accept(webTarget);
		} catch (Exception e) {
			throw new WebApplicationException("Something went wrong by creating the client: " + e);
		}
	}
}
