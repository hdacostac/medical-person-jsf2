package com.cegedim.web.components;

import java.util.ArrayList;

import javax.faces.event.AjaxBehaviorEvent;

import org.omnifaces.util.Faces;
import org.primefaces.component.selectonemenu.SelectOneMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.webflow.execution.RequestContextHolder;

import com.cegedim.web.service.PersonService;
import com.gvt.commons.dto.v1.simple.SimpleDTO;

@Controller
@RequestScope
public class PostalCodeController {

	private static final Logger logger = LoggerFactory.getLogger(PostalCodeController.class);

	private PersonService personService;

	public PostalCodeController(PersonService personService) {
		this.personService = personService;
	}

	public void onProvinceChange(AjaxBehaviorEvent event) {
		SimpleDTO provinceSelected = (SimpleDTO) ((SelectOneMenu) event.getSource()).getValue();

		RequestContextHolder.getRequestContext().getViewScope().put(
				Faces.getRequestParameter("municipalityItemsToChange"),
				personService.getMunicipalityItems(provinceSelected.getId()));

		RequestContextHolder.getRequestContext().getViewScope()
				.put(Faces.getRequestParameter("postalCodeItemsToChange"), new ArrayList<>());
	}

	public void onMunicipalityChange(AjaxBehaviorEvent event) {
		String provinceIdSelected = Faces.getRequestParameter("provinceItemIdSelected");
		SimpleDTO municipalitySelected = (SimpleDTO) ((SelectOneMenu) event.getSource()).getValue();

		RequestContextHolder.getRequestContext().getViewScope().put(
				Faces.getRequestParameter("postalCodeItemsToChange"),
				personService.getPostalCodeItems(Long.valueOf(provinceIdSelected), municipalitySelected.getId()));
	}

}
