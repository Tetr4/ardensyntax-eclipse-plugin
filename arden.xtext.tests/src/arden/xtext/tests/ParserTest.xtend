package arden.xtext.tests

import com.google.inject.Inject
import arden.xtext.ArdenSyntaxInjectorProvider
import org.eclipse.xtext.junit4.InjectWith
import org.eclipse.xtext.junit4.XtextRunner
import org.eclipse.xtext.junit4.util.ParseHelper
import org.eclipse.xtext.junit4.validation.ValidationTestHelper
import org.junit.Test
import org.junit.runner.RunWith
import arden.xtext.ardenSyntax.mlms

@RunWith(XtextRunner)
@InjectWith(ArdenSyntaxInjectorProvider)
class ParserTest {
	
	@Inject extension ParseHelper<mlms>
	@Inject extension ValidationTestHelper

    @Test
    def void testHelloWorld() {
        '''
            // You may run this MLM with: $ arden2bytecode -r hello_world.mlm
            
            maintenance:
                title: Hello World Example;;
                mlmname: hello_world;;
                arden: version 2.1;;  
                version: 1.0;;
                institution: Peter L. Reichertz Institut;;
                author: Hannes Flicka;;
                specialist: Hannes Flicka;;
                date: 2011-09-08;;
                validation: testing;;
            
            library:
                purpose: Demonstration of Arden Syntax and read statement;;
                explanation: n.a.;;
                keywords: hello world, example, arden2bytecode, read statement;;
                links: http://arden2bytecode.sf.net/ ;;
            
            knowledge:
                type: data-driven;;
                data:
                    name := read {Enter your name};
                    stdout_dest := destination{STDOUT}; 
                ;;
            
                evoke: ;; // MLM is called directly
                  
                logic:
                    conclude true;;
            
                action:
                    write "Hello " || name || "! " at stdout_dest; 
                ;;
            
            end:
        '''.parse.assertNoErrors
    }
    
        @Test
    def void testExample() {
        '''
                // You may run this MLM with: $ arden2bytecode -r example.mlm

                maintenance:
                    title: Example MLM;;
                    mlmname: examplemlm;;
                    arden: version 1.0;;
                    version: 1.0;;
                    institution: Peter L. Reichertz Institut;;
                    author: Hannes Flicka;;
                    specialist: Hannes Flicka;;
                    date: 2012-04-04;;
                    validation: testing;;
                
                library:
                    purpose: Demonstration of the Arden Syntax Editor;;
                    explanation: n.a.;;
                    keywords: example, arden syntax editor, arden2bytecode;;
                    links: http://arden2bytecode.sf.net/ ;;
                
                knowledge:
                    type: data-driven;;
                    data:
                        // set the variables used in this program:
                        let variable be 4; // <- here you can insert other values to try out
                        // set a destination for the write statement later used:
                        let dest be destination {stdout};;
                    evoke: ;; // MLM is called directly
                    logic:
                        // double variable. could also use: variable := variable * 2;
                        let variable be variable * 2;
                        if variable > 6 then
                            conclude true;  // execute action slot
                        else
                            conclude false; // do not run action slot
                        endif;; 
                    action:
                        write "variable is: " || variable || "." at dest;;
                end:
        '''.parse.assertNoErrors
    }
	

	
}