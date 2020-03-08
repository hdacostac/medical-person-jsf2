package com.cegedim.web.service;

import java.util.List;
import java.util.Map;

import com.gvt.commons.dto.v1.patient.PatientDTO;
import com.gvt.commons.dto.v1.patient.PatientListDTO;
import com.gvt.commons.dto.v1.simple.SimpleDTO;
import com.gvt.support.rest.handlers.RestResponsePage;

public interface PersonService {

	RestResponsePage<PatientListDTO> getPatients(int first, int pageSize, String sortField, String sortOrder,
			Map<String, Object> filters);

	PatientDTO savePatient(PatientDTO patientDTO);

	PatientDTO updatePatient(PatientDTO patientDTO);

	List<SimpleDTO> getBloodGroups();

	List<SimpleDTO> getSexItems();

}
