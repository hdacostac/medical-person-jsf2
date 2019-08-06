package com.cegedim.web.paginators;

import java.util.Map;

import org.omnifaces.util.Faces;
import org.primefaces.model.SortOrder;
import org.springframework.hateoas.PagedResources;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import com.cegedim.web.service.PersonService;
import com.cegedim.web.service.rest.PersonServiceRestHandler;
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
	protected PagedResources<PersonListHolder> dataSource(int first, int pageSize, String sortField,
			SortOrder sortOrder, Map<String, Object> filters) {
		PersonService restHandler = CustomApplicationContext.getContext().getBean(PersonServiceRestHandler.class);

		return restHandler.getPaginationPatients(first, pageSize, null, sortOrder != null ? sortOrder.name() : null,
				filters);
	}

	public void showUserInfo() {
		logger.debug("Información de usuario");
		
		OAuth2Authentication authentication = (OAuth2Authentication) SecurityContextHolder.getContext()
				.getAuthentication();
	}

}
