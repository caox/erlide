package com.rytong.template.editor.markers;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.texteditor.MarkerUtilities;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;



public class XMLErrorHandler extends DefaultHandler {

	public static final String ERROR_MARKER_ID = "com.rytong.template.editor.xmlError";
	
	private IFile file;

	private Map<Object, Object> map;
	public XMLErrorHandler(IFile file) {
		this.file = file;
		this.map = new HashMap<Object, Object>();
	}
	
	public void removeExistingMarkers()
	{
		try
		{
			file.deleteMarkers(ERROR_MARKER_ID, true, IResource.DEPTH_ZERO);
		}
		catch (CoreException e1)
		{
			e1.printStackTrace();
		}
	}

	private void addMarker(SAXParseException e, int severity) {
		int lineNumber = e.getLineNumber();
		int columnNumber = e.getColumnNumber();
		
		System.out.println("the sax exception : " + e.toString());
		MarkerUtilities.setLineNumber(map, lineNumber);
		MarkerUtilities.setMessage(map, e.getMessage());
		map.put(IMarker.LOCATION, file.getFullPath().toString());

		map.put(IMarker.SEVERITY, new Integer(severity));

		try
		{
			MarkerUtilities.createMarker(file, map, ERROR_MARKER_ID);
		}
		catch (CoreException ee)
		{
			ee.printStackTrace();
		}
	}

	public void error(SAXParseException exception) throws SAXException {
		addMarker(exception, IMarker.SEVERITY_ERROR);
	}

	public void fatalError(SAXParseException exception) throws SAXException {
		addMarker(exception, IMarker.SEVERITY_ERROR);
	}

	public void warning(SAXParseException exception) throws SAXException {
		addMarker(exception, IMarker.SEVERITY_WARNING);
	}
}
