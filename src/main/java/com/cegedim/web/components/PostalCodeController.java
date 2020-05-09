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

import com.cegedim.web.service.PostalCodesService;
import com.gvt.commons.dto.v1.simple.SimpleDTO;

@Controller
@RequestScope
public class PostalCodeController {

	private static final Logger logger = LoggerFactory.getLogger(PostalCodeController.class);

	private PostalCodesService postalCodesService;

	public PostalCodeController(PostalCodesService postalCodesService) {
		this.postalCodesService = postalCodesService;
	}

	public void onProvinceChange(AjaxBehaviorEvent event) {
		SimpleDTO provinceSelected = (SimpleDTO) ((SelectOneMenu) event.getSource()).getValue();

		RequestContextHolder.getRequestContext().getViewScope().put(
				Faces.getRequestParameter("municipalityItemsToChange"),
				postalCodesService.getMunicipalityItems(provinceSelected.getId()));

		RequestContextHolder.getRequestContext().getViewScope()
				.put(Faces.getRequestParameter("postalCodeItemsToChange"), new ArrayList<>());
	}

	public void onMunicipalityChange(AjaxBehaviorEvent event) {
		SimpleDTO municipalitySelected = (SimpleDTO) ((SelectOneMenu) event.getSource()).getValue();
		String provinceIdSelected = Faces.getRequestParameter("provinceItemIdSelected");

		RequestContextHolder.getRequestContext().getViewScope().put(
				Faces.getRequestParameter("postalCodeItemsToChange"),
				postalCodesService.getPostalCodeItems(Long.valueOf(provinceIdSelected), municipalitySelected.getId()));
	}

}
