package com.rytong.lua;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.dltk.ui.CodeFormatterConstants;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.editors.text.EditorsUI;
import org.erlide.ui.internal.ErlideUIPlugin;

public class LuaPreferenceInitializer extends AbstractPreferenceInitializer {
    @Override
    public void initializeDefaultPreferences() {
        DLTKUIPlugin.getDefault().getPreferenceStore().setDefault(PreferenceConstants.EDITOR_SYNC_OUTLINE_ON_CURSOR_MOVE, true);

        final IPreferenceStore store = ErlideUIPlugin.getDefault().getPreferenceStore();

        EditorsUI.useAnnotationsPreferencePage(store);
        EditorsUI.useQuickDiffPreferencePage(store);

        // Initialize DLTK default values
        PreferenceConstants.initializeDefaultValues(store);

        // Initialize Lua code color and style constants
        PreferenceConverter.setDefault(store, ILuaColorConstants.LUA_SINGLE_LINE_COMMENT, new RGB(63, 127, 95));
        PreferenceConverter.setDefault(store, ILuaColorConstants.LUA_MULTI_LINE_COMMENT, new RGB(63, 95, 191));
        PreferenceConverter.setDefault(store, ILuaColorConstants.LUA_KEYWORD, new RGB(127, 0, 85));
        PreferenceConverter.setDefault(store, ILuaColorConstants.LUA_STRING, new RGB(42, 0, 255));
        PreferenceConverter.setDefault(store, ILuaColorConstants.LUA_NUMBER, new RGB(185, 20, 20));

        store.setDefault(ILuaColorConstants.LUA_SINGLE_LINE_COMMENT + PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
        store.setDefault(ILuaColorConstants.LUA_SINGLE_LINE_COMMENT + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);

        store.setDefault(ILuaColorConstants.LUA_MULTI_LINE_COMMENT + PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
        store.setDefault(ILuaColorConstants.LUA_MULTI_LINE_COMMENT + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);

        store.setDefault(ILuaColorConstants.LUA_KEYWORD + PreferenceConstants.EDITOR_BOLD_SUFFIX, true);
        store.setDefault(ILuaColorConstants.LUA_KEYWORD + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);
        store.setDefault(PreferenceConstants.EDITOR_FOLDING_ENABLED, true);

        // Enable code folding
        store.setDefault(PreferenceConstants.EDITOR_COMMENTS_FOLDING_ENABLED, true);
        store.setDefault(PreferenceConstants.EDITOR_DOCS_FOLDING_ENABLED, true);

        // Enable auto close
        store.setDefault(PreferenceConstants.EDITOR_CLOSE_BRACES, true);
        store.setDefault(PreferenceConstants.EDITOR_CLOSE_BRACKETS, true);
        store.setDefault(PreferenceConstants.EDITOR_CLOSE_STRINGS, true);
        
        // Set Tab
        store.setDefault(CodeFormatterConstants.FORMATTER_TAB_CHAR,
                CodeFormatterConstants.TAB);
        store.setDefault(CodeFormatterConstants.FORMATTER_TAB_SIZE, "4");
        store.setDefault(CodeFormatterConstants.FORMATTER_INDENTATION_SIZE, "4");

    }


}
