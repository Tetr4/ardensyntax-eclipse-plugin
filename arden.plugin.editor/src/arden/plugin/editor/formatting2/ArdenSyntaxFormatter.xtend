/*
 * generated by Xtext 2.10.0
 */
package arden.plugin.editor.formatting2

import arden.plugin.editor.ardenSyntax.action_block
import arden.plugin.editor.ardenSyntax.action_else
import arden.plugin.editor.ardenSyntax.action_for
import arden.plugin.editor.ardenSyntax.action_if_then_else2
import arden.plugin.editor.ardenSyntax.action_slot
import arden.plugin.editor.ardenSyntax.action_statement_empty
import arden.plugin.editor.ardenSyntax.action_while
import arden.plugin.editor.ardenSyntax.data_block
import arden.plugin.editor.ardenSyntax.data_else
import arden.plugin.editor.ardenSyntax.data_for
import arden.plugin.editor.ardenSyntax.data_if_then_else2
import arden.plugin.editor.ardenSyntax.data_slot
import arden.plugin.editor.ardenSyntax.data_statement_empty
import arden.plugin.editor.ardenSyntax.data_while
import arden.plugin.editor.ardenSyntax.endif
import arden.plugin.editor.ardenSyntax.evoke_block
import arden.plugin.editor.ardenSyntax.evoke_slot
import arden.plugin.editor.ardenSyntax.evoke_statement_empty
import arden.plugin.editor.ardenSyntax.knowledge_body
import arden.plugin.editor.ardenSyntax.knowledge_category
import arden.plugin.editor.ardenSyntax.library_body
import arden.plugin.editor.ardenSyntax.library_category
import arden.plugin.editor.ardenSyntax.logic_block
import arden.plugin.editor.ardenSyntax.logic_else
import arden.plugin.editor.ardenSyntax.logic_for
import arden.plugin.editor.ardenSyntax.logic_if_then_else2
import arden.plugin.editor.ardenSyntax.logic_slot
import arden.plugin.editor.ardenSyntax.logic_statement_empty
import arden.plugin.editor.ardenSyntax.logic_while
import arden.plugin.editor.ardenSyntax.maintenance_body
import arden.plugin.editor.ardenSyntax.maintenance_category
import arden.plugin.editor.ardenSyntax.mlm
import arden.plugin.editor.ardenSyntax.mlms
import arden.plugin.editor.ardenSyntax.priority_slot
import arden.plugin.editor.ardenSyntax.type_slot
import arden.plugin.editor.ardenSyntax.urgency_slot
import arden.plugin.editor.services.ArdenSyntaxGrammarAccess
import com.google.inject.Inject
import org.eclipse.xtext.formatting2.AbstractFormatter2
import org.eclipse.xtext.formatting2.IFormattableDocument

class ArdenSyntaxFormatter extends AbstractFormatter2 {

    @Inject extension ArdenSyntaxGrammarAccess

    def dispatch void format(mlms mlms, extension IFormattableDocument document) {
        for (i : 0..mlms.getMlms.size-1) {
            val mlm = mlms.getMlms().get(i)
            mlm.format
            if(i < mlms.getMlms.size-1) {
                // blank line between mlms, but not after last mlm
                mlm.append[setNewLines(2, 2, 2)]
            }
        }
    }

    def dispatch void format(mlm mlm, extension IFormattableDocument document) {
        mlm.maintainance_category.format
        mlm.library_category.format
        mlm.knowledge_category.format
    }

    def dispatch void format(maintenance_category maintenance_category, extension IFormattableDocument document) {
        maintenance_category.maintainance_body.format
        maintenance_category.append[setNewLines(2, 2, 2)] // blank line
    }

    def dispatch void format(maintenance_body maintenance_body, extension IFormattableDocument document) {
        maintenance_body.surround[indent]
        maintenance_body.title_slot.prepend[newLine]
        maintenance_body.mlmname_slot.prepend[newLine]
        maintenance_body.arden_version_slot.prepend[newLine]
        maintenance_body.version_slot.prepend[newLine]
        maintenance_body.institution_slot.prepend[newLine]
        maintenance_body.author_slot.prepend[newLine]
        maintenance_body.specialist_slot.prepend[newLine]
        maintenance_body.date_slot.prepend[newLine]
        maintenance_body.validation_slot.prepend[newLine]
    }

    def dispatch void format(library_category library_category, extension IFormattableDocument document) {
        library_category.library_body.format
        library_category.append[setNewLines(2, 2, 2)] // blank line
    }

    def dispatch void format(library_body library_body, extension IFormattableDocument document) {
        library_body.surround[indent]
        library_body.purpose_slot.prepend[newLine]
        library_body.explanation_slot.prepend[newLine]
        library_body.keywords_slot.prepend[newLine]
        library_body.citations_slot.prepend[newLine]
        library_body.links_slot.prepend[newLine]
    }

    def dispatch void format(knowledge_category knowledge_category, extension IFormattableDocument document) {
        knowledge_category.knowledge_body.format
        knowledge_category.append[setNewLines(2, 2, 2)] // blank line
    }

    def dispatch void format(knowledge_body knowledge_body, extension IFormattableDocument document) {
        knowledge_body.surround[indent]
        knowledge_body.type_slot.format
        knowledge_body.data_slot.format
        knowledge_body.priority_slot.format
        knowledge_body.evoke_slot.format
        knowledge_body.logic_slot.format
        knowledge_body.action_slot.format
        knowledge_body.urgency_slot.format
    }

    def dispatch void format(type_slot type_slot, extension IFormattableDocument document) {
        type_slot.prepend[newLine]
    }
    
    def dispatch void format(priority_slot priority_slot, extension IFormattableDocument document) {
        priority_slot.prepend[setNewLines(2, 2, 2)] // blank line
    }
    
    def dispatch void format(urgency_slot urgency_slot, extension IFormattableDocument document) {
        urgency_slot.prepend[setNewLines(2, 2, 2)] // blank line
    }

    /**** data_slot ****/
    def dispatch void format(data_slot data_slot, extension IFormattableDocument document) {
        data_slot.prepend[setNewLines(2, 2, 2)] // blank line
        if (data_slot.data_block.statements.size == 1 && data_slot.data_block.statements.get(0) instanceof data_statement_empty) {
            // empty -> compact
            data_slot.regionFor.keyword(';;').prepend[oneSpace]
        } else {
            data_slot.data_block.format
            data_slot.regionFor.keyword(';;').prepend[newLine]
        }
    }

    def dispatch void format(data_block data_block, extension IFormattableDocument document) {
        data_block.surround[indent]
        for (statement : data_block.statements) {
            statement.prepend[newLine]
            statement.format
        }
    }

    def dispatch void format(data_for data_for, extension IFormattableDocument document) {
        data_for.block.format
        data_for.regionFor.keyword('enddo').prepend[newLine]
    }

    def dispatch void format(data_while data_while, extension IFormattableDocument document) {
        data_while.block.format
        data_while.regionFor.keyword('enddo').prepend[newLine]
    }

    def dispatch void format(data_if_then_else2 if_then_else, extension IFormattableDocument document) {
        if_then_else.regionFor.keyword('elseif').prepend[newLine]
        if_then_else.elseif.format
        if_then_else.block.format
    }

    def dispatch void format(endif endif, extension IFormattableDocument document) {
        endif.regionFor.keyword('endif').prepend[newLine]
    }

    def dispatch void format(data_else data_else, extension IFormattableDocument document) {
        data_else.regionFor.keyword('else').prepend[newLine]
        data_else.regionFor.keyword('endif').prepend[newLine]
        data_else.block.format
    }


    /**** logic_slot ****/
    def dispatch void format(logic_slot logic_slot, extension IFormattableDocument document) {
        logic_slot.prepend[setNewLines(2, 2, 2)] // blank line
        if (logic_slot.logic_block.statements.size == 1 && logic_slot.logic_block.statements.get(0) instanceof logic_statement_empty) {
            // empty -> compact
            logic_slot.regionFor.keyword(';;').prepend[oneSpace]
        } else {
            logic_slot.logic_block.format
            logic_slot.regionFor.keyword(';;').prepend[newLine]
        }
    }
    
    def dispatch void format(logic_block logic_block, extension IFormattableDocument document) {
        logic_block.surround[indent]
        for (statement : logic_block.statements) {
            statement.prepend[newLine]
            statement.format
        }
    }
    
    def dispatch void format(logic_for logic_for, extension IFormattableDocument document) {
        logic_for.block.format
        logic_for.regionFor.keyword('enddo').prepend[newLine]
    }
    
    def dispatch void format(logic_while logic_while, extension IFormattableDocument document) {
        logic_while.block.format
        logic_while.regionFor.keyword('enddo').prepend[newLine]
    }
    
    def dispatch void format(logic_if_then_else2 if_then_else, extension IFormattableDocument document) {
        if_then_else.regionFor.keyword('elseif').prepend[newLine]
        if_then_else.elseif.format
        if_then_else.block.format
    }
    
    def dispatch void format(logic_else logic_else, extension IFormattableDocument document) {
        logic_else.regionFor.keyword('else').prepend[newLine]
        logic_else.regionFor.keyword('endif').prepend[newLine]
        logic_else.block.format
    }
    
    /**** evoke_slot ****/
    def dispatch void format(evoke_slot evoke_slot, extension IFormattableDocument document) {
        evoke_slot.prepend[setNewLines(2, 2, 2)] // blank line
        if (evoke_slot.evoke_block.statements.size == 1 && evoke_slot.evoke_block.statements.get(0) instanceof evoke_statement_empty) {
            // empty -> compact
            evoke_slot.regionFor.keyword(';;').prepend[oneSpace]
        } else {
            evoke_slot.evoke_block.format
            evoke_slot.regionFor.keyword(';;').prepend[newLine]
        }
    }
    
    def dispatch void format(evoke_block evoke_block, extension IFormattableDocument document) {
        evoke_block.surround[indent]
        for (statement : evoke_block.statements) {
            statement.prepend[newLine]
            statement.format
        }
    }
    
    /**** action_slot ****/    
    def dispatch void format(action_slot action_slot, extension IFormattableDocument document) {
        action_slot.prepend[setNewLines(2, 2, 2)] // blank line
        if (action_slot.action_block.statements.size == 1 && action_slot.action_block.statements.get(0) instanceof action_statement_empty) {
            // empty -> compact
            action_slot.regionFor.keyword(';;').prepend[oneSpace]
        } else {
            action_slot.action_block.format
            action_slot.regionFor.keyword(';;').prepend[newLine]
        }
    }
    
    def dispatch void format(action_block action_block, extension IFormattableDocument document) {
        action_block.surround[indent]
        for (statement : action_block.statements) {
            statement.prepend[newLine]
            statement.format
        }
    }
    
    def dispatch void format(action_for action_for, extension IFormattableDocument document) {
        action_for.block.format
        action_for.regionFor.keyword('enddo').prepend[newLine]
    }
    
    def dispatch void format(action_while action_while, extension IFormattableDocument document) {
        action_while.block.format
        action_while.regionFor.keyword('enddo').prepend[newLine]
    }
    
    def dispatch void format(action_if_then_else2 if_then_else, extension IFormattableDocument document) {
        if_then_else.regionFor.keyword('elseif').prepend[newLine]
        if_then_else.elseif.format
        if_then_else.block.format
    }
    
    def dispatch void format(action_else action_else, extension IFormattableDocument document) {
        action_else.regionFor.keyword('else').prepend[newLine]
        action_else.regionFor.keyword('endif').prepend[newLine]
        action_else.block.format
    }

}