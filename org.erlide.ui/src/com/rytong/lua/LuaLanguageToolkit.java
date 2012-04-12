package com.rytong.lua;

import org.eclipse.dltk.core.AbstractLanguageToolkit;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;

public class LuaLanguageToolkit extends AbstractLanguageToolkit {

    private static LuaLanguageToolkit toolkit;

    public static IDLTKLanguageToolkit getDefault() {
        if (toolkit == null) {
            toolkit = new LuaLanguageToolkit();
        }
        return toolkit;
    }
    @Override
    public String getLanguageContentType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getLanguageName() {
        // TODO Auto-generated method stub
        return "Lua";
    }

    @Override
    public String getNatureId() {
        // TODO Auto-generated method stub
        return null;
    }

}
