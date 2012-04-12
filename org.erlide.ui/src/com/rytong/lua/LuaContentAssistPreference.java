package com.rytong.lua;

import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.dltk.ui.text.completion.ContentAssistPreference;
import org.erlide.ui.internal.ErlideUIPlugin;

public class LuaContentAssistPreference extends ContentAssistPreference {
    private static LuaContentAssistPreference sDefault;

    public static ContentAssistPreference getDefault() {
        if (sDefault == null) {
            sDefault = new LuaContentAssistPreference();
        }
        return sDefault;
    }
    @Override
    protected ScriptTextTools getTextTools() {
        return ErlideUIPlugin.getDefault().getTextTools();
    }

}
