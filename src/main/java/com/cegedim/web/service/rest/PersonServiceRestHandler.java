package com.cegedim.web.service.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.core.TypeReferences.PagedModelType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.cegedim.web.service.PersonService;
import com.gvt.commons.dto.v1.patient.PatientDTO;
import com.gvt.commons.dto.v1.simple.SimpleDTO;
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

	private static final class PatientParametrizedReturnType extends PagedModelType<PersonListHolder> {
	}

	private static final class SimpleDTOParametrizedReturnType extends ParameterizedTypeReference<List<SimpleDTO>> {
	}

	@Override
	public PagedModel<PersonListHolder> getPatients(int first, int pageSize, String sortField, String sortOrder,
			Map<String, Object> filters) {
		Map<String, Object> params = urlHrRestHandler.buildFilterParams(first, pageSize, sortField, sortOrder, filters);

		if (filters != null && StringUtils.isNotBlank((String) filters.get("globalFilter"))) {
			params.put("globalFilter", "%" + filters.get("globalFilter") + "%"); // Overwrite the globalFilter param
		}

		return restTemplate.exchange(urlHrRestHandler.buildURI("/api/v1/persons", params, null), HttpMethod.GET, null,
				new PatientParametrizedReturnType()).getBody();
	}

	@Override
//	@Cacheable(value = "simpleDomainCacheBloodGroups")
	public List<SimpleDTO> getBloodGroups() {
		return restTemplate.exchange(urlHrRestHandler.buildURI("/api/v1/simple/bloodGroups"), HttpMethod.GET, null,
				new SimpleDTOParametrizedReturnType()).getBody();
	}

	@Override
//	@Cacheable(value = "simpleDomainCacheSexItems")
	public List<SimpleDTO> getSexItems() {
		return restTemplate.exchange(urlHrRestHandler.buildURI("/api/v1/simple/sex"), HttpMethod.GET, null,
				new SimpleDTOParametrizedReturnType()).getBody();
	}

	@Override
	public PatientDTO savePatient(PatientDTO patientDTO) {
		return restTemplate.exchange(urlHrRestHandler.buildURI("/api/v1/patients"), HttpMethod.POST,
				new HttpEntity<>(patientDTO), PatientDTO.class).getBody();
	}

	@Override
	public PatientDTO updatePatient(PatientDTO patientDTO) {
		Map<String, String> pathParams = new HashMap<>();
		pathParams.put("id", String.valueOf(patientDTO.getId()));

		return restTemplate.exchange(urlHrRestHandler.buildURI("/api/v1/patients/{id}", null, pathParams),
				HttpMethod.PUT, new HttpEntity<>(patientDTO), PatientDTO.class).getBody();
	}

}
