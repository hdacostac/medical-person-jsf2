package com.cegedim.web.service;

import java.util.Collection;
import java.util.Map;

import org.springframework.hateoas.PagedResources;

import com.gvt.commons.helper.PersonListHolder;
import com.gvt.main.hibernate.model.TipoSangre;

public interface PersonService {

	PagedResources<PersonListHolder> getPatients(int first, int pageSize, String sortField, String sortOrder,
			Map<String, Object> filters);

	Collection<TipoSangre> getBloodGroups();

}
