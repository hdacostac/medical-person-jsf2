package com.cegedim.web.service.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.cegedim.web.service.PostalCodesService;
import com.gvt.commons.dto.v1.simple.SimpleDTO;
import com.gvt.support.rest.handlers.UrlRestHandler;

@Component("postalCodesService")
public class PostalCodesRestService implements PostalCodesService {

	private RestTemplate restTemplate;
	private UrlRestHandler urlHrRestHandler;

	public PostalCodesRestService(RestTemplate restTemplate, UrlRestHandler urlHrRestHandler) {
		this.restTemplate = restTemplate;
		this.urlHrRestHandler = urlHrRestHandler;
	}

	private static final class SimpleDTOParametrizedReturnType extends ParameterizedTypeReference<List<SimpleDTO>> {
	}

	@Override
	public List<SimpleDTO> getProvinceItems() {
		List<SimpleDTO> list = restTemplate.exchange(urlHrRestHandler.buildURI("/api/v1/simple/provinces"),
				HttpMethod.GET, null, new SimpleDTOParametrizedReturnType()).getBody();

//		list.add(0, new SimpleDTO());

		return list;
	}

	@Override
	public List<SimpleDTO> getMunicipalityItems(Long provinceId) {
		Map<String, Object> queryParams = new HashMap<>();
		queryParams.put("province_id", provinceId);

		return restTemplate.exchange(urlHrRestHandler.buildURI("/api/v1/simple/municipalities", queryParams, null),
				HttpMethod.GET, null, new SimpleDTOParametrizedReturnType()).getBody();
	}

	@Override
	public List<SimpleDTO> getPostalCodeItems(Long provinceId, Long municipalityId) {
		Map<String, Object> queryParams = new HashMap<>();
		queryParams.put("province_id", provinceId);
		queryParams.put("municipality_id", municipalityId);

		return restTemplate.exchange(urlHrRestHandler.buildURI("/api/v1/simple/postal-codes", queryParams, null),
				HttpMethod.GET, null, new SimpleDTOParametrizedReturnType()).getBody();
	}

}
