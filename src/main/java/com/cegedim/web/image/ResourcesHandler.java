package com.cegedim.web.image;

import java.io.IOException;

import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

public interface ResourcesHandler {

	StreamedContent getImage() throws IOException;

	String saveImage(UploadedFile file) throws IOException;

}