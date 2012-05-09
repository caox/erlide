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

import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.ui.IEditorInput;

import com.rytong.template.editor.Activator;


public class TemplateEditor extends ScriptEditor {
    
    public static final String EDITOR_ID = "com.rytong.editors.TemplateEditor";

    public static final String EDITOR_CONTEXT = "#EWPTemplateEditorContext";

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
            IDocumentExtension3 extension = (IDocumentExtension3) document;
            if (extension.getDocumentPartitioner(ITemplatePartitions.LUA_PARTITIONING) == null) {
                TemplateTextTools tools = Activator.getDefault().getTextTools();
                tools.setupDocumentPartitioner(document, ITemplatePartitions.LUA_PARTITIONING);
            }
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

}
