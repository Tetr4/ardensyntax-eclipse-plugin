package arden.xtext.ui.folding;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.ui.editor.folding.DefaultFoldingRegionProvider;
import org.eclipse.xtext.ui.editor.folding.IFoldingRegionAcceptor;
import org.eclipse.xtext.util.ITextRegion;

import arden.xtext.ardenSyntax.action_slot;
import arden.xtext.ardenSyntax.data_slot;
import arden.xtext.ardenSyntax.evoke_slot;
import arden.xtext.ardenSyntax.knowledge_category;
import arden.xtext.ardenSyntax.library_category;
import arden.xtext.ardenSyntax.logic_slot;
import arden.xtext.ardenSyntax.maintenance_category;

public class ArdenSyntaxFoldingRegionProvider extends DefaultFoldingRegionProvider {

    @Override
    protected void computeObjectFolding(EObject eObject, IFoldingRegionAcceptor<ITextRegion> foldingRegionAcceptor) {
        boolean fold = eObject instanceof maintenance_category
                | eObject instanceof library_category
                | eObject instanceof knowledge_category
                | eObject instanceof data_slot
                | eObject instanceof evoke_slot
                | eObject instanceof logic_slot
                | eObject instanceof action_slot;
        
        if(fold) {
            computeObjectFolding(eObject, foldingRegionAcceptor, false);
        }
    }
    
    @Override
    protected boolean shouldProcessContent(EObject object) {
        boolean ignoreContent = object instanceof maintenance_category
                | object instanceof library_category
                | object instanceof data_slot
                | object instanceof evoke_slot
                | object instanceof logic_slot
                | object instanceof action_slot;
        
        return !ignoreContent;
    }
    
}
