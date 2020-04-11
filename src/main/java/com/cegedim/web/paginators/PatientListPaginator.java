package com.cegedim.web.paginators;

import java.util.Map;

import org.primefaces.model.SortOrder;

import com.cegedim.web.service.PersonService;
import com.gvt.commons.dto.v1.patient.PatientListDTO;
import com.gvt.context.CustomApplicationContext;
import com.gvt.data.domain.PageableRestResponse;
import com.gvt.web.controllers.AbstractActionList;

public class PatientListPaginator extends AbstractActionList<PatientListDTO> {

	private static final long serialVersionUID = 7652225764105360134L;

	@Override
	public Object getRowKey(PatientListDTO item) {
		return item.getId();
	}

	@Override
	public PatientListDTO getRowData(String rowKey) {
		logger.debug("Executing getRowData:{}", rowKey);

		for (PatientListDTO item : getWrappedData()) {
			if (item.getId() == Long.parseLong(rowKey)) {
				return item;
			}
		}

		return null;
	}

	@Override
	protected PageableRestResponse<PatientListDTO> dataSource(int first, int pageSize, String sortField,
			SortOrder sortOrder, Map<String, Object> filters) {
		PersonService restHandler = CustomApplicationContext.getContext().getBean(PersonService.class);

		return restHandler.getPatients(first, pageSize, null, sortOrder != null ? sortOrder.name() : null, filters);
	}

}
