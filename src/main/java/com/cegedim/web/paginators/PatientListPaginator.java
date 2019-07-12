package com.cegedim.web.paginators;

import java.util.Map;

import org.primefaces.model.SortOrder;
import org.springframework.hateoas.PagedResources;

import com.cegedim.web.service.PersonService;
import com.cegedim.web.service.rest.PersonServiceRestHandler;
import com.gvt.main.hibernate.model.Persona;

import swf.cegedim.web.context.CustomApplicationContext;
import swf.cegedim.web.controllers.BaseActionList;

public class PatientListPaginator extends BaseActionList<Persona> {

	private static final long serialVersionUID = 7652225764105360134L;

	@Override
	public Object getRowKey(Persona item) {
		return item.getId();
	}

	@Override
	public Persona getRowData(String rowKey) {
		logger.debug("Executing getRowData:{}", rowKey);

		for (Persona item : getWrappedData()) {
			if (item.getId() == Long.parseLong(rowKey)) {
				return item;
			}
		}

		return null;
	}

	@Override
	protected PagedResources<Persona> dataSource(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		PersonService restHandler = CustomApplicationContext.getContext().getBean(PersonServiceRestHandler.class);

		return restHandler.getPaginationPatients(first, pageSize, "nombrePersona",
				sortOrder != null ? sortOrder.name() : null, filters);
	}

}
