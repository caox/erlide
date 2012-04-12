package com.rytong.lua;

import org.eclipse.dltk.ast.Modifiers;

public class LuaConstants {

    protected LuaConstants() {
    }

    public static final int LuaAttributeModifier = 2 << (Modifiers.USER_MODIFIER + 1);
    public static final int LuaAliasModifier = 2 << (Modifiers.USER_MODIFIER + 2);
    public static final String COMMENT_STRING = "--"; //$NON-NLS-1$
    public final static String LUA_PARTITIONING = "__lua_partitioning"; //$NON-NLS-1$
    public static final String REQUIRE = "require"; //$NON-NLS-1$
    
}
