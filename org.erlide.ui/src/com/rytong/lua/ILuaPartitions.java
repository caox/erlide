package com.rytong.lua;

import org.eclipse.jface.text.IDocument;

public interface ILuaPartitions {
    public static final String LUA_PARTITIONING =  LuaConstants.LUA_PARTITIONING;

    public static final String LUA_COMMENT = "__lua_comment"; //$NON-NLS-1$
    public static final String LUA_MULTI_LINE_COMMENT = "__lua_multi_line_comment"; //$NON-NLS-1$
    public static final String LUA_NUMBER = "__lua_number"; //$NON-NLS-1$
    public static final String LUA_STRING = "__lua_string"; //$NON-NLS-1$
    public static final String LUA_SINGLE_QUOTE_STRING = "__lua_single_quote_string"; //$NON-NLS-1$

    public static final String[] LUA_PARTITION_TYPES = new String[] { IDocument.DEFAULT_CONTENT_TYPE, ILuaPartitions.LUA_COMMENT,
            ILuaPartitions.LUA_COMMENT, ILuaPartitions.LUA_STRING, ILuaPartitions.LUA_SINGLE_QUOTE_STRING, ILuaPartitions.LUA_MULTI_LINE_COMMENT,
            ILuaPartitions.LUA_NUMBER };
}
