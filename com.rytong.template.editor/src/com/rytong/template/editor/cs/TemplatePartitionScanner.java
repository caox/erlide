package com.rytong.template.editor.cs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.Token;

import com.rytong.template.editor.editors.TagRule;

public class TemplatePartitionScanner extends RuleBasedPartitionScanner {

	public TemplatePartitionScanner() {
        super();
        List<PatternRule> rules = new ArrayList<PatternRule>();

        

        // Lua script contained in <![CDATA[ ... ]>
        IToken luaCode = new Token(ITemplatePartitions.LUA);
        rules.add(new MultiLineRule("<![CDATA[", "]>", luaCode));//$NON-NLS-1$ //$NON-NLS-2$

        // XML Tag  
        IToken tag = new Token(ITemplatePartitions.XML_TAG);
        rules.add(new TagRule(tag));

        // Apply rules
        IPredicateRule[] result = new IPredicateRule[rules.size()];
        rules.toArray(result);
        setPredicateRules(result);
    }
    
}
