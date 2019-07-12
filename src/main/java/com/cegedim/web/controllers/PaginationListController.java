package com.cegedim.web.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.cegedim.web.service.PersonService;
import com.gvt.gpc.helper.Autoregistro;

import swf.cegedim.web.controllers.BaseActionForm;
import swf.cegedim.web.handlers.LocaleHandler;

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
