package com.cegedim.web.beans;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import com.gvt.commons.dto.v1.simple.SimpleDTO;

@Component
public class SimpleDTOsBuilder {

	private MessageSource messageSource;

	public SimpleDTOsBuilder(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public List<SimpleDTO> getRelationShips() {
		List<SimpleDTO> items = new ArrayList<>();

		for (Long id = 1L; id < 5L; ++id) {
			SimpleDTO simpleDTO = new SimpleDTO();
			simpleDTO.setCode("lookup.relationship." + StringUtils.leftPad(id.toString(), 3, '0'));
			simpleDTO.setDescription(
					messageSource.getMessage("lookup.relationship." + StringUtils.leftPad(id.toString(), 3, '0'), null,
							LocaleContextHolder.getLocale()));
			items.add(simpleDTO);
		}

		return items;
	}
}
