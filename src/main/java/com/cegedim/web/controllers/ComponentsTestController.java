package com.cegedim.web.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.omnifaces.util.Faces;
import org.omnifaces.util.Messages;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.annotation.SessionScope;

import com.cegedim.web.beans.PlayerBean;
import com.gvt.web.controllers.BaseFaces;

@Controller
@SessionScope
public class ComponentsTestController extends BaseFaces implements Serializable {

	private static final long serialVersionUID = -2740529264326563818L;

	private String inputTextValue;
	private String inputTextAreaValue;
	private String autocompleteValue;
	private Date calendarValue;

	private PlayerBean playerValue;

	private boolean validateClient;

	private boolean propDisabled;
	private boolean propReadOnly;
	private boolean propShowComponent = true;
	private boolean propShowTooltipComponent;
	private String propAutocomplete = "on";
	private String propStyle;

	public List<PlayerBean> toIterate() {
		List<PlayerBean> playerList = new ArrayList<>();
		for (long i = 0; i < 500; ++i) {
			playerList.add(new PlayerBean(i, "" + i));
		}

		Faces.getSession().setAttribute("playerList", playerList);

		return playerList;
	}

	public List<PlayerBean> completeWithBean(String query) {
		List<PlayerBean> playerList = new ArrayList<>();
		for (long i = 0; i < 10; ++i) {
			playerList.add(new PlayerBean(i, query + i));
		}

		Faces.getSession().setAttribute("playerList", playerList);

		return playerList;
	}

	public List<String> completeText(String query) {
		List<String> results = new ArrayList<>();
		for (int i = 0; i < 10; ++i) {
			results.add(query + i);
		}

		return results;
	}

	public String divByZero() {
		int x = 100 / 0;

		return "";
	}

	public String processComplete() {
		logger.debug("Processing complete!!");

		Messages.addGlobalInfo("Process Completed");

		return null;
	}

	public String invalidateSession() {
		logger.debug("Processing invalidateSession!!");

		// Faces.getSession().invalidate();
		// ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		// SecurityContextHolder.clearContext();
		// ec.invalidateSession();

		return null;
	}

	public PlayerBean getPlayerValue() {
		return playerValue;
	}

	public void setPlayerValue(PlayerBean playerValue) {
		this.playerValue = playerValue;
	}

	public String getAutocompleteValue() {
		return autocompleteValue;
	}

	public void setAutocompleteValue(String autocompleteValue) {
		this.autocompleteValue = autocompleteValue;
	}

	public String getPropAutocomplete() {
		return propAutocomplete;
	}

	public void setPropAutocomplete(String propAutocomplete) {
		this.propAutocomplete = propAutocomplete;
	}

	public boolean isValidateClient() {
		return validateClient;
	}

	public void setValidateClient(boolean validateClient) {
		this.validateClient = validateClient;
	}

	public boolean isPropShowTooltipComponent() {
		return propShowTooltipComponent;
	}

	public void setPropShowTooltipComponent(boolean propShowTooltipComponent) {
		this.propShowTooltipComponent = propShowTooltipComponent;
	}

	public boolean isPropShowComponent() {
		return propShowComponent;
	}

	public void setPropShowComponent(boolean propShowComponent) {
		this.propShowComponent = propShowComponent;
	}

	public String getPropStyle() {
		return propStyle;
	}

	public void setPropStyle(String propStyle) {
		if (StringUtils.isBlank(propStyle)) {
			this.propStyle = "background-color: none";
		} else {
			this.propStyle = propStyle;
		}
	}

	public boolean isPropReadOnly() {
		return propReadOnly;
	}

	public void setPropReadOnly(boolean propReadOnly) {
		this.propReadOnly = propReadOnly;
	}

	public boolean isPropDisabled() {
		return propDisabled;
	}

	public void setPropDisabled(boolean propDisabled) {
		this.propDisabled = propDisabled;
	}

	public String getInputTextValue() {
		return inputTextValue;
	}

	public void setInputTextValue(String inputTextValue) {
		this.inputTextValue = inputTextValue;
	}

	public String getInputTextAreaValue() {
		return inputTextAreaValue;
	}

	public void setInputTextAreaValue(String inputTextAreaValue) {
		this.inputTextAreaValue = inputTextAreaValue;
	}

	public Date getCalendarValue() {
		return calendarValue;
	}

	public void setCalendarValue(Date calendarValue) {
		this.calendarValue = calendarValue;
	}
}
