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
	
	// TODO check crossreferences
	// TODO check for expected errors

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
    def void testExample1() {
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
                urgency: 1;;
            end:
        '''.parse.assertNoErrors
    }
    
    @Test
    def void testExample2() {
        '''
            maintenance: 
            title: Feeding Check;;
            mlmname: feeding_check;;
            arden: version 2.5;;
            version: 1.0;;
            institution: Die Hundebarf;;
            author: Mike;;
            specialist: ;;
            date: 2015-11-25;;
            validation: testing;;
        
        library: 
            purpose: Prevention of accidentally poisoning your hedgehog;;
            explanation: Checks for food that is toxic to hedgehogs;;
            keywords: hedgehog, toxic, poison, diet, nutrition;;
            citations: The Hedgehog Encyclopedia;;
            links: http://wiki.hedgehogcentral.com/tiki-index.php?page=Toxic;;
        
        knowledge:
            type: data-driven;;
        
            data:
                food := READ {Food};
                toxic_foods := "Raisin", "Grape", "Tea Tree", "Avocado", "Nut", "Seed", "Chocolate", "Milk", "Cheese", "Joghurt";
                
                feeding_event := EVENT {feeding};
                email := DESTINATION {email: "abc@xyz.uvw"};
            ;;
        
            evoke: 
                // MLM is called directly
                // feeding_event;
            ;;
        
            logic: 
                FOR toxic_food IN toxic_foods DO
                    wildcard_pattern := "%" || toxic_food || "%";
                    IF food MATCHES PATTERN wildcard_pattern THEN
                        CONCLUDE TRUE;
                    ENDIF;
                ENDDO;
                CONCLUDE FALSE;
            ;;
        
            action:
                WRITE food FORMATTED WITH "WARNING: %s can be toxic to hedgehogs!" AT email;
                RETURN FALSE;
            ;;
        
        end:
        '''.parse.assertNoErrors
    }
    


    
    
    @Test
    def void testComments() {
        '''
            // x
            maintenance: // x 
                title: //title;; 
                /*
                  mlmname: mlmname;;
                */
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                // citations: citations;;
                /* links: links;; */
            knowledge:
                type: data-driven;;
                data: ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }
    
    @Test
    def void testAssignment() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                  aString := "asdf";
                  aNumber := 5123;
                  LET anotherString BE "asdfgh"; // alternative syntax 
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }
    
    @Test
    def void testRead() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                  input := READ {Please enter your name};
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }
    

    @Test
    def void testConcat() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    aString := "blubb";
                    anotherString := "meh" || aString || "bla";
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }

    @Test
    def void testFormattedWith() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    formatedString := 3.14 FORMATTED WITH "Pi is about %.2f";
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }   
    
    @Test
    def void testUpperLowerCase() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    uppercaseString := UPPERCASE "Abcdef";
                    lowercaseString := LOWERCASE "Abcdef";
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }
    
    @Test
    def void testSubstring() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    ab := SUBSTRING 2 CHARACTERS FROM "Abcdef";
                    def := SUBSTRING 3 CHARACTERS STARTING AT 4 FROM "Abcdef";
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }
    
    @Test
    def void testMatchesPattern() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    aBoolean := "fatal heart attack" MATCHES PATTERN "%heart%";
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }
    
    @Test
    def void testLength() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    lengthOfString := LENGTH OF "Abcdef";
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }

    @Test
    def void testLocalization() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    aLocalizedString := LOCALIZED 'msg' by "de";
                    anotherLocalizedString := LOCALIZED 'msg'; // system language
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
                resources:
                    default: de;;
                    language: en
                        'msg' : "The patient's BMI %.1f is not in the normal range and is classified as ";
                        'under' : "Underweight";
                        'over' : "Overweight"
                    ;;
                    language: de
                        'msg' : "Der BMI %.1f des Patienten ist nicht im normalen Bereich und wird klassifiziert als ";
                        'under' : "Untergewicht";
                        'over' : "Übergewicht"
                    ;;
            end:
        '''.parse.assertNoErrors
    }
    
    @Test
    def void testList() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    aList := "Raisins", "Grapes", "Avocados", "Chocolate", "Milk", "Cheese";
                    singleElementList := , "Blubb";
                    anotherList := (1,2,3,4);
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }
    
    @Test
    def void testSequence() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    sequence := 2 SEQTO 4;
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }
    
    @Test
    def void testListSort() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    aList := "Raisins", "Grapes", "Avocados", "Chocolate", "Milk", "Cheese";
                    sortedList := SORT aList;
                    sortedByTimeList := SORT TIME aList;
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }
    
    @Test
    def void testListMerge() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    aList := "Raisins", "Grapes", "Avocados", "Chocolate", "Milk", "Cheese";
                    aListWithCookies := aList MERGE "Cookies";
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    } 
    
    @Test
    def void testListActions() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    a := REVERSE (3,5,10);
                    a := FIRST (3,5,10);
                    a := LAST (3,5,10);
                    a := COUNT (3,5,10);
                    a := AVERAGE (3,5,10); 
                    a := AVG (3,5,10); // alternative syntax
                    a := SUM (3,5,10);
                    a := MEDIAN (3,5,10);
                    a := VARIANCE (3,5,10);
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }
    
    @Test
    def void testNull() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    a := (1,null,3);
                    b := null;
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }
    
    @Test
    def void testExists() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    atLeastOneNonNull := EXISTS (1,null,3);
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }
    
    @Test
    def void testObjectDefinition() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    Patient := OBJECT [Name, DateOfBirth, Gender];
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }
    
    @Test
    def void testObjectInstantiation() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    Patient := OBJECT [Name, DateOfBirth, Gender];
                    john := NEW Patient WITH "John Doe", "1970-1-1", "Male";
                    blankPatient := NEW Patient;
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }
    
    @Test
    def void testObjectAccess() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    Patient := OBJECT [Name, DateOfBirth, Gender];
                    john := NEW Patient WITH "John Doe", "1970-1-1", "Male";
                    johnsName := john.Name; 
                    john.DateOfBirth = "1970-1-2";
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }    
    
    @Test
    def void testObjectClone() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    Patient := OBJECT [Name, DateOfBirth, Gender];
                    john := NEW Patient WITH "John Doe", "1970-1-1", "Male";
                    john2 := CLONE OF john;
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }
    
    @Test
    def void testEvent() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    anEvent := EVENT {something happens};
                ;;
                evoke:
                    anEvent; 
                ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }
    
    @Test
    def void testMessage() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    warning := MESSAGE {WARNING: Your food may be toxic};
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }
    
    @Test
    def void testDestination() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    email := DESTINATION {email: "abc@xyz.uvw"};
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }
	
    @Test
    def void testArguments() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    args := ARGUMENT;
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }
    
    @Test
    def void testInclude() {
        '''
            maintenance: 
                title: title;;
                mlmname: another_mlm;;
                arden: version 2.5;;
                version: 1.0;;
                institution: my institution;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    x := 5;
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
            
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: my institution;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    mlm2 := MLM 'another_mlm' FROM INSTITUTION "my institution";
                    INCLUDE mlm2;
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }    
    
    @Test
    def void testMlmCall() {
        '''
            maintenance: 
                title: title;;
                mlmname: another_mlm;;
                arden: version 2.5;;
                version: 1.0;;
                institution: my institution;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    x := 5;
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
            
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: my institution;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    mlm2 := MLM 'another_mlm' FROM INSTITUTION "my institution";
                    mlmResult := CALL mlm2 WITH "arg";
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }
    
    @Test
    def void testEventCall() {
        '''
            maintenance: 
                title: title;;
                mlmname: another_mlm;;
                arden: version 2.5;;
                version: 1.0;;
                institution: my institution;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    anEvent := EVENT {somethinghappens};
                ;;
                evoke: 
                    anEvent;
                ;;
                logic: ;;
                action: ;;
            end:
            
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: my institution;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    anEvent := EVENT {somethinghappens};
                    result := CALL anEvent WITH "arg1", "arg2";
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }
    
    @Test
    def void testInterface() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    nativeInterface := INTERFACE {\\Server\Modules\native\dosomething.exe};
                    result := CALL nativeInterface WITH "arg1", "arg2";
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }
    
    @Test
    def void testMath() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    result := 2 ** 10; // 1024
                    result := COSINE 0; // 1
                    result := LOG 1; // 0
                    result := ABS (-12.3); // 12.3
                    result := CEILING (12.3);  // 13
                    result := TRUNCATE (12.3); // 12
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }
    
    @Test
    def void testDateParse() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    t := 2015-11-27T00:00:00;
                    t := 2011-01-03T14:23:17.3;
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }    
    
    @Test
    def void testDateCurrent() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    t := CURRENTTIME;
                    t := NOW; // alternative syntax
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }  
    
    @Test
    def void testDateExtract() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    t := TIME OF NOW;
                    t := DAY OF NOW;
                    t := WEEK OF NOW;
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }
    
    @Test
    def void testDateTime() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    t := 15:00:00;
                    t := NOW ATTIME 15:00:00;
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }
    
    @Test
    def void testDateArithmetic1() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    tomorrow := 1 DAY AFTER NOW;
                    t:= NOW - 3 MONTHS;
                    t := 6 DAYS / 3; // 2 days
                    t := SUM (1 DAY,6 DAYS); // 7 days
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }  
    
    @Test
    def void testDateArithmetic2() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    t := AVERAGE (2011-01-03T03:10:00, 2011-01-03T05:10:00); // 04:10:00
                    a := LATEST (NOW, 1 DAY AFTER NOW);
                    a := EARLIEST (NOW, 1 DAY AFTER NOW);
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    } 
    
    @Test
    def void testDateLogic() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    aBoolean := NOW IS BEFORE 2016-11-27T00:00:00;
                    aBoolean := 2015-01-10T00:00:00 IS WITHIN 15 DAYS FOLLOWING 2015-01-01T00:00:00; // true
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    } 
    
    @Test
    def void testLogic() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    aBoolean := TRUE;
                    aBoolean := NOT FALSE;
                    aBoolean := TRUE OR FALSE;
                    aBoolean := NOT (TRUE AND FALSE);
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }
    
    @Test
    def void testLogicComparison() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    aBoolean := 1=1 AND NOT "moep"="asdf";
                    aBoolean := 1<>5;
                    aBoolean := 5 < 3;
                    aBoolean := 5 IS LESS THAN 3; // alternative syntax
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }  
    
    @Test
    def void testLogicNull() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    aBoolean := TRUE OR NULL; //  true
                    aBoolean := FALSE OR NULL; // null
                    aBoolean := FALSE OR "bla"; // null
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    } 
    
    @Test
    def void testLogicIn() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    aBoolean := 5 IS WITHIN 0 TO 10;
                    aBoolean := 2015-01-10T00:00:00 IS WITHIN 15 DAYS FOLLOWING 2015-01-01T00:00:00; // true
                    aBoolean := 3 IS IN (10,3,7);
                    aBooleanList:= (5,3) IS IN (9,3,8); // (false,true)
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    } 
    
    @Test
    def void testType() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    aVariable := "asdf";
                    Pixel := OBJECT[x, y];
                    
                    aBoolean := aVariable IS NULL; // false
                    aBoolean := aVariable IS PRESENT; // true
                    aBoolean := aVariable IS STRING; // true
                    aBoolean := aVariable IS NUMBER; // false
                    aBoolean := aVariable IS Pixel; // false
                ;;
                evoke: ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }  
    
    
    @Test
    def void testEvoke1() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data: ;;
                evoke:
                    2016-01-01T00:00:00;
                ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }  
    
    @Test
    def void testEvoke2() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data: ;;
                evoke:
                    EVERY 2 HOURS FOR 1 WEEK STARTING 2016-01-01T00:00:00;
                ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    } 
    
    @Test
    def void testEvoke3() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data: ;;
                evoke:
                    EVERY MONDAY;
                ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }  
    
    @Test
    def void testEvoke4() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    anEvent := EVENT {something happens};
                ;;
                evoke:
                    5 SECONDS AFTER TIME OF anEvent;
                ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }  
    
    @Test
    def void testEvoke5() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    anEvent := EVENT {something happens};
                    a := 0;
                    b := 6;
                ;;
                evoke:
                    EVERY 1 MONTH FOR 1 YEAR STARTING 15 SECONDS AFTER TIME OF anEvent UNTIL a IS GREATER THAN b;
                ;;
                logic: ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }  
    
    @Test
    def void testConclude() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data: ;;
                evoke: ;;
                logic:
                    CONCLUDE 5>2;
                ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }
    
    @Test
    def void testIf() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data: ;;
                evoke: ;;
                logic:
                    a := 5;
                    IF a > 7 THEN
                        CONCLUDE TRUE;
                    ELSEIF a < -10 THEN
                        CONCLUDE TRUE;
                    ELSE 
                        CONCLUDE FALSE;
                    ENDIF;
                ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }         
    
    @Test
    def void testWhile() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data: ;;
                evoke: ;;
                logic:
                    a := 5;
                    WHILE a > 0 DO
                        a:= a - 1;
                    ENDDO; 
                ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }  
    
    @Test
    def void testFor() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data: ;;
                evoke: ;;
                logic:
                    s := 0;
                    FOR nr IN 2 SEQTO 4 DO
                        s := s + nr;
                    ENDDO;
                ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }
    
    @Test
    def void testForList() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data: ;;
                evoke: ;;
                logic:
                    l := ("abc", "bla", "blubb", "meh");
                    FOR item IN l DO
                        IF item="abc" THEN
                            ;
                        ELSEIF item="blubb" THEN
                            ;
                        ELSE 
                            ;
                        ENDIF;
                    ENDDO; 
                ;;
                action: ;;
            end:
        '''.parse.assertNoErrors
    }  
    
        @Test
    def void testWrite() {
        '''
            maintenance: 
                title: title;;
                mlmname: mlmname;;
                arden: version 2.5;;
                version: 1.0;;
                institution: ;;
                author: author;;
                specialist: specialist;;
                date: 2012-09-05;;
                validation: testing;;
            library: 
                purpose: purpose;;
                explanation: explanation;;
                keywords: keywords;;
                citations: citations;;
                links: links;;
            knowledge:
                type: data-driven;;
                data:
                    warning := MESSAGE {WARNING: Your food may be toxic};
                    email := DESTINATION {email: "abc@xyz.uvw"};
                ;;
                evoke: ;;
                logic:
                    CONCLUDE TRUE;
                ;;
                action:
                    WRITE "WARNING: Your food may be toxic";
                    WRITE warning;
                    WRITE warning AT email;
                    RETURN TRUE;
                ;;
            end:
        '''.parse.assertNoErrors
    }             

}