package swf.cegedim.web.interceptors;

import java.io.IOException;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import com.cegedim.web.components.CustomMenuComponent;

public class HeaderInterceptor implements ClientHttpRequestInterceptor {

	private static final Logger LOGGER = LoggerFactory.getLogger(CustomMenuComponent.class);

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
			throws IOException {
		request.getHeaders().add("Accept-Language", LocaleContextHolder.getLocale().getLanguage());

		logRequest(request, body);
		ClientHttpResponse response = execution.execute(request, body);
		logResponse(response);

		return response;
	}

	private void logRequest(HttpRequest request, byte[] body) throws IOException {
		LOGGER.trace("===========================request begin================================================");
		LOGGER.trace("URI         : {}", request.getURI());
		LOGGER.trace("Method      : {}", request.getMethod());
		LOGGER.trace("Headers     : {}", request.getHeaders());
		LOGGER.trace("Request body: {}", new String(body, "UTF-8"));
		LOGGER.trace("==========================request end================================================");
	}

	private void logResponse(ClientHttpResponse response) throws IOException {
		LOGGER.trace("============================response begin==========================================");
		LOGGER.trace("Status code  : {}", response.getStatusCode());
		LOGGER.trace("Status text  : {}", response.getStatusText());
		LOGGER.trace("Headers      : {}", response.getHeaders());
//		LOGGER.trace("Response body: {}", StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
		LOGGER.trace("=======================response end=================================================");
	}

}
