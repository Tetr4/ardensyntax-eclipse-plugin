# Arden4Eclipse
If you want to express your medical knowledge with [Arden Syntax](https://en.wikipedia.org/wiki/Arden_syntax) you can use Arden4Eclipse, an Arden Syntax Editor for the [Eclipse IDE](https://eclipse.org/). It integrates [Arden2ByteCode](https://github.com/PLRI/arden2bytecode) so Arden Syntax code can be easily written as well as executed.

Arden4Eclipse is made with the [xText](https://www.eclipse.org/Xtext/) framework.

## Usage
Check out <https://plri.github.io/arden2bytecode/arden4eclipse/> for installation instructions, features and screenshots.


## Project Structure
This repository contains multiple Eclipse [plugin projects](http://help.eclipse.org/luna/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Fguide%2Farch.htm&cp=2_0_1) (plugin projects contain a `MANIFEST.MF` file).  
Together they form two Eclipse [features](http://help.eclipse.org/luna/index.jsp?topic=%2Forg.eclipse.platform.doc.user%2Fconcepts%2Fconcepts-25.htm): An [Xtext](https://eclipse.org/Xtext/) based **editor** for syntax highlighting, content-assist, etc. and an **Arden2ByteCode integration**, so MLMs can be run via the run button in Eclipse.

The projects contain the following:

    .
    ├── arden.plugin.editor            # An xText based Arden Syntax parser and scoping/validation rules, etc.
    ├── arden.plugin.editor.tests      # Tests for the parser
    ├── arden.plugin.editor.ui         # The Eclipse editor UI and code for content-assist, code folding, outline, syntaxcoloring, etc.
    ├── arden.plugin.editor.feature    # A feature which includes the above parser and editor plugins    
    ├── arden.plugin.compiler          # A plugin wrapper for Arden2ByteCode
    ├── arden.plugin.compiler.launch   # The launch configuration to run/debug MLMs in Eclipse
    ├── arden.plugin.compiler.feature  # A feature which includes above compiler plugins
    └── arden.plugin.update-site       # Allows the generation of an update-site, from which eclipse can download/update both features.

## Building

1. To build Arden4Eclipse you need to have an Eclipse installation with the following tools:
   - [Eclipse PDE (Plug-in Development Environment)](https://marketplace.eclipse.org/content/eclipse-pde-plug-development-environment)
   - [Xtext](https://marketplace.eclipse.org/content/xtext)
   - [Eclipse Xtend](https://marketplace.eclipse.org/content/eclipse-xtend)
   - Eclipse RCP Target Components (optional, includes sources/javadoc for the Eclipse API)
1. Import all projects and then generate the parser and xtext language artifacts by right-clicking on [arden.plugin.editor/.../ArdenSyntax.xtext](arden.plugin.editor/src/arden/plugin/editor/ArdenSyntax.xtext) and selecting *Run As* &rArr; *Generate Xtext Artifacts*
1. For the Arden2ByteCode integration, you need to place a `arden2bytecode.jar` into the [arden.plugin.compiler](arden.plugin.compiler) project. You can get the newest release [here](https://github.com/PLRI/arden2bytecode/releases/latest).

To run Arden4Eclipse in a new Eclipse runtime: right-click on a project and select *Run As* &rArr; *Eclipse Application*

To generate an update-site open [arden.plugin.update-site/site.xml](arden.plugin.update-site/site.xml) and click *Build All*.
