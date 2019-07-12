package com.cegedim.web.service;

import java.util.Map;

import org.springframework.hateoas.PagedResources;

import com.gvt.main.hibernate.model.Persona;

public interface PersonService {

	PagedResources<Persona> getPaginationPatients(int first, int pageSize, String sortField, String sortOrder,
			Map<String, Object> filters);

}
