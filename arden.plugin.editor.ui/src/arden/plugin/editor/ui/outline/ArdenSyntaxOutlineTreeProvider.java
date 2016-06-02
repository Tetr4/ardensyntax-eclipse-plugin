/*
* generated by Xtext
*/
package arden.plugin.editor.ui.outline;

import org.eclipse.xtext.ui.editor.outline.IOutlineNode;
import org.eclipse.xtext.ui.editor.outline.impl.DefaultOutlineTreeProvider;

import arden.plugin.editor.ardenSyntax.action_slot;
import arden.plugin.editor.ardenSyntax.data_slot;
import arden.plugin.editor.ardenSyntax.evoke_slot;
import arden.plugin.editor.ardenSyntax.knowledge_body;
import arden.plugin.editor.ardenSyntax.knowledge_category;
import arden.plugin.editor.ardenSyntax.library_category;
import arden.plugin.editor.ardenSyntax.logic_slot;
import arden.plugin.editor.ardenSyntax.maintenance_category;
import arden.plugin.editor.ardenSyntax.mlm;
import arden.plugin.editor.ardenSyntax.mlms;

/**
 * customization of the default outline structure
 * 
 */
public class ArdenSyntaxOutlineTreeProvider extends DefaultOutlineTreeProvider {

    protected void _createChildren(IOutlineNode parentNode, mlms mlms) {
        // add child modules directly to file node
        for (mlm node : mlms.getMlms()) {
            createNode(parentNode, node);
        }
    }
    
    protected void _createChildren(IOutlineNode parentNode, knowledge_category knowledgeCategory) {
        // skip body and append children directly to category
        knowledge_body body = knowledgeCategory.getKnowledge_body();
        if(body.getData_slot() != null)
            createNode(parentNode, body.getData_slot());
        if(body.getEvoke_slot() != null)
            createNode(parentNode, body.getEvoke_slot());
        if(body.getLogic_slot() != null)
            createNode(parentNode, body.getLogic_slot());
        if(body.getAction_slot() != null)
            createNode(parentNode, body.getAction_slot());
    }
    
    protected boolean _isLeaf(maintenance_category feature) {
        return true;
    }
    
    protected boolean _isLeaf(library_category feature) {
        return true;
    }
    
    protected boolean _isLeaf(data_slot dataSlot) {
        // don't include statements to keep outline flat
        return true;
    }
    
    protected boolean _isLeaf(evoke_slot feature) {
        // don't include statements to keep outline flat
        return true;
    }

    protected boolean _isLeaf(logic_slot feature) {
        // don't include statements to keep outline flat
        return true;
    }

    protected boolean _isLeaf(action_slot feature) {
        // don't include statements to keep outline flat
        return true;
    }

}
