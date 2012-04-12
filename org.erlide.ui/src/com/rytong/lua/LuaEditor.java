package com.rytong.lua;

import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.ui.IEditorInput;
import org.erlide.ui.internal.ErlideUIPlugin;

public class LuaEditor extends ScriptEditor {
    
    public static final String EDITOR_ID = "com.rytong.editors.LuaEditor";

    public static final String EDITOR_CONTEXT = "#EWPLuaEditorContext";

    protected void initializeEditor() {
        super.initializeEditor();
        setEditorContextMenuId(EDITOR_CONTEXT);
    }
    
    public IPreferenceStore getScriptPreferenceStore() {
        return ErlideUIPlugin.getDefault().getPreferenceStore();
    }
    
    /** Connects partitions used to deal with comments or strings in editor. */
    protected void connectPartitioningToElement(IEditorInput input, IDocument document) {
        if (document instanceof IDocumentExtension3) {
            IDocumentExtension3 extension = (IDocumentExtension3) document;
            if (extension.getDocumentPartitioner(ILuaPartitions.LUA_PARTITIONING) == null) {
                LuaTextTools tools = ErlideUIPlugin.getDefault().getTextTools();
                tools.setupDocumentPartitioner(document, ILuaPartitions.LUA_PARTITIONING);
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
        return ErlideUIPlugin.getDefault().getTextTools();
    }

}
