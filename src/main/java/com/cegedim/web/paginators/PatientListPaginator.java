package com.cegedim.web.paginators;

import java.util.Map;

import org.primefaces.model.SortOrder;
import org.springframework.hateoas.PagedModel;

import com.cegedim.web.service.PersonService;
import com.gvt.commons.helper.PersonListHolder;
import com.gvt.web.context.CustomApplicationContext;
import com.gvt.web.controllers.BaseActionList;

public class PatientListPaginator extends BaseActionList<PersonListHolder> {

	private static final long serialVersionUID = 7652225764105360134L;

	@Override
	public Object getRowKey(PersonListHolder item) {
		return item.getId();
	}

	@Override
	public PersonListHolder getRowData(String rowKey) {
		logger.debug("Executing getRowData:{}", rowKey);

		for (PersonListHolder item : getWrappedData()) {
			if (item.getId() == Long.parseLong(rowKey)) {
				return item;
			}
		}

		return null;
	}

	@Override
	protected PagedModel<PersonListHolder> dataSource(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		PersonService restHandler = CustomApplicationContext.getContext().getBean(PersonService.class);

		return restHandler.getPatients(first, pageSize, null, sortOrder != null ? sortOrder.name() : null, filters);
	}

}
