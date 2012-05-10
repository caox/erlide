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

package com.rytong.template.editor.cs;

import org.eclipse.dltk.ui.text.AbstractScriptScanner;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.dltk.ui.text.ScriptPresentationReconciler;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.SingleTokenScriptScanner;
import org.eclipse.dltk.ui.text.completion.ContentAssistPreference;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.texteditor.ITextEditor;

import com.rytong.template.editor.editors.ColorManager;
import com.rytong.template.editor.editors.IXMLColorConstants;
import com.rytong.template.editor.editors.XMLScanner;
import com.rytong.template.editor.editors.XMLTagScanner;

public class TemplateSourceViewerConfiguration extends
        ScriptSourceViewerConfiguration {

    private AbstractScriptScanner fLuaCodeScanner;
	private XMLTagScanner fXMLTagScanner;
	private XMLScanner fXMLScanner;
	private ColorManager colorManager;
//    private AbstractScriptScanner fCommentScanner;
//    private AbstractScriptScanner fMultilineCommentScanner;
//    private AbstractScriptScanner fNumberScanner;
    
    public TemplateSourceViewerConfiguration(IColorManager colorManager, IPreferenceStore preferenceStore, ITextEditor editor, String partitioning) {
        super(colorManager, preferenceStore, editor, partitioning);
    }
    
    @Override
    protected ContentAssistPreference getContentAssistPreference() {
        return LuaContentAssistPreference.getDefault();
    }
    
    public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
        PresentationReconciler reconciler = new ScriptPresentationReconciler();
        reconciler.setDocumentPartitioning(this.getConfiguredDocumentPartitioning(sourceViewer));

        DefaultDamagerRepairer dr;
        dr = new DefaultDamagerRepairer(this.fXMLScanner);
        reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
        reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

        dr = new DefaultDamagerRepairer(this.fLuaCodeScanner);
        reconciler.setDamager(dr, ITemplatePartitions.LUA);
        reconciler.setRepairer(dr, ITemplatePartitions.LUA);

        dr = new DefaultDamagerRepairer(this.fXMLTagScanner);
        reconciler.setDamager(dr, ITemplatePartitions.XML_TAG);
        reconciler.setRepairer(dr, ITemplatePartitions.XML_TAG);


        return reconciler;
    }

    /**
     * This method is called from base class.
     */
    protected void initializeScanners() {
        // This is lua code scanner
        this.fLuaCodeScanner = new LuaCodeScanner(this.getColorManager(), this.fPreferenceStore);

        this.colorManager = getXMLColorManager();
        // This is xml scanners for partitions.
        this.fXMLScanner = getXMLScanner();
        this.fXMLTagScanner = getXMLTagScanner();
        

    }

    public void handlePropertyChangeEvent(PropertyChangeEvent event) {
        if (this.fLuaCodeScanner.affectsBehavior(event)) {
            this.fLuaCodeScanner.adaptToPreferenceChange(event);
        }

    }

    public boolean affectsTextPresentation(PropertyChangeEvent event) {
        return this.fLuaCodeScanner.affectsBehavior(event);
    }

    
	protected XMLScanner getXMLScanner() {
		if (fXMLScanner == null) {
			fXMLScanner = new XMLScanner(colorManager);
			fXMLScanner.setDefaultReturnToken(
				new Token(
					new TextAttribute(
						colorManager.getColor(IXMLColorConstants.DEFAULT))));
		}
		return fXMLScanner;
	}
	protected XMLTagScanner getXMLTagScanner() {
		if (fXMLTagScanner == null) {
			fXMLTagScanner = new XMLTagScanner(colorManager);
			fXMLTagScanner.setDefaultReturnToken(
				new Token(
					new TextAttribute(
						colorManager.getColor(IXMLColorConstants.TAG))));
		}
		return fXMLTagScanner;
	}
	
	protected ColorManager getXMLColorManager() {
		if (colorManager == null) {
			colorManager = new ColorManager();	
		}
			return colorManager;
	}
    /**
     * Lua specific one line comment
     * 
     * @see ScriptSourceViewerConfiguration#getCommentPrefix()
     */
    @Override
    protected String getCommentPrefix() {
        return TemplateConstants.COMMENT_STRING;
    }

    @Override
    public String[] getConfiguredContentTypes(final ISourceViewer sourceViewer) {
        return ITemplatePartitions.TEMPLATE_PARTITION_TYPES;
    }

}
