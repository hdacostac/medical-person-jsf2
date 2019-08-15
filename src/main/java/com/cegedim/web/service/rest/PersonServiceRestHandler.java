package com.cegedim.web.service.rest;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.mvc.TypeReferences;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.cegedim.web.service.PersonService;
import com.gvt.commons.helper.PersonListHolder;
import com.gvt.support.rest.handlers.URLRestHandler;

@Component
public class PersonServiceRestHandler implements PersonService {

	private RestTemplate restTemplate;
	private URLRestHandler urlHrRestHandler;

	public PersonServiceRestHandler(RestTemplate restTemplate, URLRestHandler urlHrRestHandler) {
		this.restTemplate = restTemplate;
		this.urlHrRestHandler = urlHrRestHandler;
	}

	private static final class PatientParametrizedReturnType
			extends TypeReferences.PagedResourcesType<PersonListHolder> {
	}

	@Override
	public PagedResources<PersonListHolder> getPaginationPatients(int first, int pageSize, String sortField,
			String sortOrder, Map<String, Object> filters) {
		Map<String, Object> params = urlHrRestHandler.buildFilterParams(first, pageSize, sortField, sortOrder, filters);

		if (filters != null && StringUtils.isNotBlank((String) filters.get("globalFilter"))) {
			params.put("globalFilter", "%" + filters.get("globalFilter") + "%"); // Overwrite the globalFilter param
		}

		return restTemplate.exchange(urlHrRestHandler.buildURI("/api/v1/persons", params), HttpMethod.GET, null,
				new PatientParametrizedReturnType()).getBody();
	}

}
