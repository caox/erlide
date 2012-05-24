/*******************************************************************************
 * Copyright (c) 2009, 2011 Sierra Wireless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package com.rytong.template.editor.template;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.erlide.backend.BackendCore;
import org.erlide.backend.IBackend;
import org.erlide.jinterface.ErlLogger;
import org.erlide.jinterface.rpc.RpcException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


import com.ericsson.otp.erlang.OtpErlangAtom;
import com.ericsson.otp.erlang.OtpErlangInt;
import com.ericsson.otp.erlang.OtpErlangList;
import com.ericsson.otp.erlang.OtpErlangLong;
import com.ericsson.otp.erlang.OtpErlangObject;
import com.ericsson.otp.erlang.OtpErlangString;
import com.ericsson.otp.erlang.OtpErlangTuple;
import com.rytong.template.editor.Activator;
import com.rytong.template.editor.lua.LuaLanguageToolkit;
import com.rytong.template.editor.markers.TemplateErrorHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class TemplateEditor extends ScriptEditor {

	public static final String EDITOR_ID = "com.rytong.editors.TemplateEditor";

	public static final String EDITOR_CONTEXT = "#EWPTemplateEditorContext";

	SAXParserFactory fParserFactory = null;

	IEditorInput input = null;

	protected void initializeEditor() {
		super.initializeEditor();
		setEditorContextMenuId(EDITOR_CONTEXT);
	}

	public IPreferenceStore getScriptPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}

	/** Connects partitions used to deal with comments or strings in editor. */
	protected void connectPartitioningToElement(IEditorInput input, IDocument document) {
		if (document instanceof IDocumentExtension3) {
			this.input = input;
			IDocumentExtension3 extension = (IDocumentExtension3) document;
			if (extension.getDocumentPartitioner(ITemplatePartitions.TEMPLATE_PARTITIONING) == null) {
				TemplateTextTools tools = Activator.getDefault().getTextTools();
				tools.setupDocumentPartitioner(document, ITemplatePartitions.TEMPLATE_PARTITIONING);
			}
			validateAndMark();
		}
	}

	@Override
	public String getEditorId() {
		// TODO Auto-generated method stub
		return EDITOR_ID;
	}

	@Override
	public IDLTKLanguageToolkit getLanguageToolkit() {
		// TODO Auto-generated method stub
		return LuaLanguageToolkit.getDefault();
	}

	@Override
	public ScriptTextTools getTextTools() {
		return Activator.getDefault().getTextTools();
	}


	@Override
	public void doSave(IProgressMonitor progressMonitor) {
		super.doSave(progressMonitor);
		this.input = getEditorInput();
		validateAndMark();
	}


	protected void validateAndMark() {
		try
		{
			IFile file = getInputFile(input);
			ErlLogger.debug("the file name : " +file.getName());
			String content = getInputDocument().get();
			TemplateErrorHandler reporter = new TemplateErrorHandler(file);
			reporter.removeExistingMarkers();


			try {
				IBackend ewpBackend = BackendCore.getBackendManager().getEWPBackend();
				if(ewpBackend != null) {
					ErlLogger.debug("call ewp backend to parse the cs file");
					OtpErlangObject res = ewpBackend.call("tmpl", "validate_for_ide", "s", content);
					//ErlLogger.debug("the rpc call result : " + res);
					if(res instanceof OtpErlangTuple) {
						final OtpErlangTuple tuple = (OtpErlangTuple) res;
						ErlLogger.debug("the tuple : " + tuple);
						OtpErlangList list = (OtpErlangList) tuple.elementAt(1);
						for(final OtpErlangObject o : list) {
							final OtpErlangTuple error = (OtpErlangTuple) o;
							OtpErlangLong l = (OtpErlangLong) error.elementAt(0);
							OtpErlangString s = (OtpErlangString) error.elementAt(1);
							//OtpErlangAtom a = (OtpErlangAtom) tuple.elementAt(0);
							reporter.addCSError(l.intValue(), s.stringValue());
						}
						
					}
				}

				getParser().parse(file.getContents(), reporter);
			} 
			catch (SAXParseException se) {				
			}catch (RpcException e) {
				//ErlLogger.debug("error happened when do rpc call");
				e.printStackTrace();
			}catch (Exception e1) {
				e1.printStackTrace();
			} 
		}
		catch (Exception e)
		{
			//e.printStackTrace();
		}
	}

	private SAXParser getParser() throws ParserConfigurationException,
	SAXException {
		if (fParserFactory == null) {
			fParserFactory = SAXParserFactory.newInstance();
		}
		return fParserFactory.newSAXParser();
	}

	protected IDocument getInputDocument()
	{
		IDocument document = getDocumentProvider().getDocument(input);
		return document;
	}

	protected IFile getInputFile(IEditorInput input)
	{
		IFileEditorInput ife = (IFileEditorInput) input;
		IFile file = ife.getFile();
		return file;
	}

}
