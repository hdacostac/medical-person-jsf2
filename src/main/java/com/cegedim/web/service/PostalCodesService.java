package com.cegedim.web.service;

import java.util.List;

import com.gvt.commons.dto.v1.simple.SimpleDTO;

public interface PostalCodesService {

	List<SimpleDTO> getProvinceItems();

	List<SimpleDTO> getMunicipalityItems(Long provinceId);

	List<SimpleDTO> getPostalCodeItems(Long provinceId, Long municipalityId);

}
