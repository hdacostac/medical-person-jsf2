package com.cegedim.web.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.cegedim.web.service.PersonService;
import com.gvt.commons.helper.Autoregistro;
import com.gvt.web.controllers.BaseActionForm;
import com.gvt.web.handlers.LocaleHandler;

@Scope("request")
@Controller("wizard")
public class PaginationListController extends BaseActionForm<Autoregistro> {

	private static final Logger LOGGER = LoggerFactory.getLogger(PaginationListController.class);

	private PersonService service;
	private LocaleHandler localeHandler;

	public PaginationListController(PersonService service, LocaleHandler localeHandler) {
		this.service = service;
		this.localeHandler = localeHandler;
	}

}
