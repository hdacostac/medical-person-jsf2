package com.cegedim.web.components;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gvt.web.components.MenuComponent;

public class CustomMenuComponent implements MenuComponent, Serializable {

	protected final Logger logger = LoggerFactory.getLogger(CustomMenuComponent.class);

	protected MenuModel model;

	@Override
	@PostConstruct
	public void init() {
		logger.debug("Building the base menu for users");

		model = new DefaultMenuModel();

		// First submenu
		DefaultSubMenu firstSubmenu = new DefaultSubMenu("Especialidades");

		DefaultMenuItem item = new DefaultMenuItem("Listado");
		item.setUrl("/app/specialities");
		item.setIcon("fa fa-list");
		firstSubmenu.addElement(item);

		model.addElement(firstSubmenu);

		// Second submenu
		DefaultSubMenu thirdMenu = new DefaultSubMenu("Pacientes");

		item = new DefaultMenuItem("Listado");
		item.setUrl("/app/list");
		item.setIcon("fa fa-list");
		thirdMenu.addElement(item);

		item = new DefaultMenuItem("Nuevo");
		item.setUrl("/app/patient");
		item.setIcon("fa fa-plus");
		thirdMenu.addElement(item);

		model.addElement(thirdMenu);

		// Second submenu
		DefaultSubMenu fourthMenu = new DefaultSubMenu("Prescripciones");

		item = new DefaultMenuItem("Listado");
		item.setUrl("/app/list");
		item.setIcon("fa fa-list");
		fourthMenu.addElement(item);

		item = new DefaultMenuItem("Nuevo");
		item.setUrl("/app/patient");
		item.setIcon("fa fa-plus");
		fourthMenu.addElement(item);

		model.addElement(fourthMenu);
	}

	@Override
	public MenuModel getModel() {
		return model;
	}
}
