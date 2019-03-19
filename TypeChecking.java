import java.util.ArrayList;

// The following methods type check  AST nodes used in CSX Lite
//  You will need to complete the methods after line 238 to type check the
//   rest of CSX
//  Note that the type checking done for CSX lite may need to be extended to
//   handle full CSX (for example binaryOpNode).

public class TypeChecking extends Visitor { 

//	static int typeErrors =  0;     // Total number of type errors found 
//  	public static SymbolTable st = new SymbolTable(); 	
	int typeErrors;     // Total number of type errors found 
	SymbolTable st;	
	static methodDeclNode currentMethod; //points to the method we are currently examining
	boolean mainExists = false;
	boolean secondPass = false;
	SymbolInfo currentMethodId; //points to the symbol info ID of the method we are currently examining
	
	TypeChecking(){
		typeErrors = 0;
		st = new SymbolTable(); 
	}
	
	ArrayList<ParmInfo> buildArgList(argsNodeOption arg){
		ArrayList<ParmInfo> args = new ArrayList<ParmInfo>();
		argsNodeOption currentArg = arg;
		while (!currentArg.isNull()) { 
			argsNode temp = (argsNode)currentArg;
			ParmInfo parm = new ParmInfo(temp.argVal.type, temp.argVal.kind);
			args.add(parm);
			currentArg = temp.moreArgs;
		}
		return args;
	}
	
	boolean isTypeCorrect(csxLiteNode n) {
        	this.visit(n);
        	System.out.println("Error count = " + typeErrors);
        	return (typeErrors == 0);
	}
	
	int countChars(String str){
		int count = 0;
		//ditch quotes
		str = str.substring(1,str.length()-1);

		while(str.contains("\\t")){
			str = str.replace("\\t", "");
			count++;
		}
		while(str.contains("\\")){
			str = str.replace("\\", "");
			count++;
		}
		while(str.contains("\\n")){
			str = str.replace("\\n", "");
			count++;
		}
		while(str.contains("\\'")){
			str = str.replace("\\'", "");
			count++;
		}
		return count+=str.length(); 

	}
	
	boolean isTypeCorrect(classNode n) {
    	this.visit(n);
    	System.out.println("Error count = " + typeErrors);
    	return (typeErrors == 0);
}
	boolean isScalar(ASTNode.Kinds kind) {
		return (kind==ASTNode.Kinds.Var)||(kind==ASTNode.Kinds.Value)||(kind==ASTNode.Kinds.ScalarParm);
	}
	
	static void assertCondition(boolean assertion){  
		if (! assertion)
			 throw new RuntimeException();
	}
	 void typeMustBe(ASTNode.Types testType,ASTNode.Types requiredType,String errorMsg) {
		 if ((testType != ASTNode.Types.Error) && (testType != requiredType)) {
                        System.out.println(errorMsg);
                        typeErrors++;
                }
        }
	 
	 void typeMustBe(ASTNode.Types testType,ASTNode.Types typeOption1, ASTNode.Types typeOption2, String errorMsg) {
		 if ( (testType != ASTNode.Types.Error) && (testType != typeOption1) && (testType != typeOption2) ) {
			 System.out.println(errorMsg);
			 typeErrors++;
		 }
	 }
	 
	 void typesMustBeEqual(ASTNode.Types type1,ASTNode.Types type2,String errorMsg) {
		 if ((type1 != ASTNode.Types.Error) && (type2 != ASTNode.Types.Error) &&
                     (type1 != type2)) {
                        System.out.println(errorMsg);
                        typeErrors++;
                }
        }
	 
	 boolean kindIsAssignable(ASTNode.Kinds kind) {
		 return ((kind==ASTNode.Kinds.Var) || (kind == ASTNode.Kinds.Array) || (kind == ASTNode.Kinds.ScalarParm) || (kind == ASTNode.Kinds.ArrayParm));
	 }
	 
	String error(ASTNode n) {
		return "Error (line " + n.linenum + "): ";
        }

	static String opToString(int op) {
		switch (op) {
			case sym.PLUS:
				return(" + ");
			case sym.MINUS:
				return(" - ");
			case sym.EQ:
				return(" == ");
			case sym.NOTEQ:
				return(" != ");
			case sym.CAND:
				return(" && ");
			case sym.COR:
				return(" || ");
			case sym.GEQ:
				return(" >= ");
			case sym.GT:
				return(" > ");
			case sym.LEQ:
				return(" <= ");
			case sym.LT:
				return(" < ");
			case sym.NOT:
				return(" !");
			case sym.SLASH:
				return(" / ");
			case sym.TIMES:
				return(" * ");
			case sym.AND:
				return( "&" );
			case sym.OR:
				return( "|" );
			default:
				assertCondition(false);
				return "";
		}
	}


// Extend this to handle all CSX binary operators
	static void printOp(int op) {
		switch (op) {
			case sym.PLUS:
				System.out.print(" + ");
				break;
			case sym.MINUS:
				System.out.print(" - ");
				break;
			case sym.EQ:
				System.out.print(" == ");
				break;
			case sym.NOTEQ:
				System.out.print(" != ");
				break;
			case sym.CAND:
				System.out.print(" && ");
				break;
			case sym.COR:
				System.out.print(" || ");
				break;
			case sym.GEQ:
				System.out.print(" >= ");
				break;
			case sym.GT:
				System.out.print(" > ");
				break;
			case sym.LEQ:
				System.out.print(" <= ");
				break;
			case sym.LT:
				System.out.print(" < ");
				break;
			case sym.NOT:
				System.out.print(" !");
				break;
			case sym.SLASH:
				System.out.print(" / ");
				break;
			case sym.TIMES:
				System.out.print(" * ");
				break;
			case sym.AND:
				System.out.print(" & ");
				break;
			case sym.OR:
				System.out.print(" | ");
				break;
			default:
				throw new Error();
		}
	}

	
	 void visit(csxLiteNode n){
		this.visit(n.progDecls);
		this.visit(n.progStmts);
	}
	
	void visit(fieldDeclsNode n){
			this.visit(n.thisField);
			this.visit(n.moreFields);
	}
	void visit(nullFieldDeclsNode n){}

	void visit(stmtsNode n){
		  //System.out.println ("In stmtsNode\n");
		  this.visit(n.thisStmt);
		  this.visit(n.moreStmts);

	}
	void visit(nullStmtsNode n){}

// Extend varDeclNode's method to handle initialization
	void visit(varDeclNode n){
		SymbolInfo id;
		id = (SymbolInfo) st.localLookup(n.varName.idname);
    	if (id != null) {
           		 System.out.println(error(n) + id.name()+
                                 " is already declared.");
            	typeErrors++;
            	n.varName.type = ASTNode.Types.Error;
    	}
    	//No initial expression
        else if (n.initValue.isNull()) {
	           id = new SymbolInfo(n.varName.idname, ASTNode.Kinds.Var, n.varType.type);
	           n.varName.type = n.varType.type;
	           try {
	                st.insert(id);
				} 
				catch (DuplicateException d) 
					{ /* can't happen */ }
				catch (EmptySTException e)
					{ /* can't happen */ }
	            n.varName.idinfo=id;
	     }
		// If the varDeclNode has an initialization:
	   else {
       		//Type check the initial value expression
       		this.visit(n.initValue);
       		
       		//Check that the initial value expression's type is the same as typeNode
       		typesMustBeEqual(n.varType.type,((exprNode)n.initValue).type,error(n)+"Value of"
       				+ " initialized declaration must be "+n.varType.type);
       		
       		//Check that the initial value's kind is scalar
       		if (!isScalar(((exprNode)n.initValue).kind)){
       			System.out.println(error(n) + "Initial value must be scalar");
       			typeErrors++; 
       		}
       		
       		//Put new symbol in the symbol table
       		id = new SymbolInfo(n.varName.idname,ASTNode.Kinds.Var,n.varType.type);
       		n.varName.type = n.varType.type;
       		
       		try {
                st.insert(id);
			} 
			catch (DuplicateException d) 
				{ /* can't happen */ }
			catch (EmptySTException e)
				{ /* can't happen */ }
            n.varName.idinfo=id;
	   }

	};
	
	void visit(nullTypeNode n){}
	
	void visit(intTypeNode n){
		//no type checking needed}
	}
	void visit(boolTypeNode n){
		//no type checking needed}
	}
	void visit(identNode n){
		//Look up identNode in symbol table, error if absent
		SymbolInfo    id;
        id =  (SymbolInfo) st.globalLookup(n.idname);
        if (id == null) {
        	System.out.println(error(n) +  n.idname + " is not declared.");
            typeErrors++;
            n.type = ASTNode.Types.Error;
        } 
        //Copy symbol table type and kind into IdentNode and save a pointer to it
        else {
        	n.type = id.type;
        	n.kind = id.kind;
        	n.idinfo = id; // Save ptr to correct symbol table entry
        }
	}

// Extend nameNode's method to handle subscripts
	void visit(nameNode n){
		//Visit the identNode
		this.visit(n.varName); 
		
		//If no subscripts copy type and kind into identNode
		if (n.subscriptVal.isNull()) {
        	n.type=n.varName.type;
        	n.kind=n.varName.kind;
        	return;
		}
		else {
			//Visit the subscript
			this.visit(n.subscriptVal);
			//identNode's Kind must be array
			if (n.varName.kind!=ASTNode.Kinds.Array && n.varName.kind!=ASTNode.Kinds.ArrayParm) {
				System.out.println(error(n)+ "Subscripted ID must be an array");
				typeErrors++;
			}
			//SubscriptVal must have a kind of scalar and a type of integer or character
			if (!isScalar(((exprNode)n.subscriptVal).kind)){
				System.out.println(error(n)+ "Subscripted value must be scalar");
				typeErrors++;
			}

			typeMustBe( ((exprNode)n.subscriptVal).type,ASTNode.Types.Character,ASTNode.Types.Integer,error(n) + "Subscript "
					+ "type must be integer or character");
			}
			
			//Set nameNode's type and kind to identNode's type and kind
			n.type = n.varName.type;
			n.kind = n.varName.kind;

	}

	void visit(asgNode n){
		this.visit(n.target);
		this.visit(n.source);
		//Check that nameNode's kind is assignable
		if ( !(kindIsAssignable(n.target.kind)) ) {
			System.out.println(error(n) + "Target of assignment cannot be changed");
			typeErrors++;
			return;
		}
		//If nameNode's kind is Scalar
		if (isScalar(n.target.kind)) {
			typeErrors++;
			typesMustBeEqual(n.source.type, n.target.type, error(n) + "Right hand side of an assignment is not assignable to left hand side.");
			return;
		}
		//If nameNode and exprTree's kinds are both arrays and both have same type
		if ( (n.target.kind == ASTNode.Kinds.Array) && (n.source.kind == ASTNode.Kinds.Array) &&
				(n.target.type == n.source.type) ) {
			SymbolInfo targetID = (SymbolInfo) st.globalLookup(n.target.varName.idname);
			//We can cast because we know the source is an array aka nameNode with subscript
			SymbolInfo sourceID = (SymbolInfo) st.globalLookup(((nameNode)n.source).varName.idname);
			//Make sure both arrays have same size
			if (targetID.getArraySize() != sourceID.getArraySize()) {
				System.out.println(error(n) + "When assigning arrays they must be the same size");
				typeErrors++;
			}
			return;
		}
		if ( (n.target.kind == ASTNode.Kinds.Array) && (n.target.type == ASTNode.Types.Character) && (n.source.kind == ASTNode.Kinds.String)) {
			SymbolInfo targetID = (SymbolInfo) st.globalLookup(n.target.varName.idname);
			strLitNode source = (strLitNode) n.source;
			if (targetID.getArraySize() != countChars(source.strval)) {
				System.out.println(error(n) + "When assigning a string to a character array they must be the same length");
			} 
			return;
		}
		System.out.println(error(n) + "Right hand side of an assignment is not assignable to left hand side.");
		typeErrors++;
	}
        

	void visit(ifThenNode n){
		  this.visit(n.condition);
        	  typeMustBe(n.condition.type, ASTNode.Types.Boolean,
                	error(n) + "The control expression of an" +
                          	" if must be a bool.");

		  this.visit(n.thenPart);
		  this.visit(n.elsePart);
	}
	  
	 void visit(printNode n){
		this.visit(n.outputValue);
        if ((n.outputValue.type != ASTNode.Types.Integer) && (n.outputValue.type != ASTNode.Types.Boolean)
        		&& (n.outputValue.type != ASTNode.Types.Character) && (n.outputValue.kind != ASTNode.Kinds.String)
        		&& ((n.outputValue.type != ASTNode.Types.Character) && (n.outputValue.kind != ASTNode.Kinds.Array)) ) {
        	System.out.println(error(n) + "Only integers, booleans, characters, string literals or arrays of characters may be printed");
        	typeErrors++;
        }
	  }
	  
	  void visit(blockNode n){
		// open a new local scope for the block body
			st.openScope();
			this.visit(n.decls);
			this.visit(n.stmts);
			// close this block's local scope
			try { st.closeScope();
			}  catch (EmptySTException e) 
	                      { /* can't happen */ }
	  }

	
	  void visit(binaryOpNode n){
		  try{
			  assertCondition(n.operatorCode== sym.PLUS||n.operatorCode==sym.MINUS 
        				|| n.operatorCode== sym.EQ||n.operatorCode==sym.NOTEQ
        				|| n.operatorCode==sym.CAND||n.operatorCode==sym.COR
        				|| n.operatorCode==sym.GT||n.operatorCode==sym.GEQ
        				|| n.operatorCode==sym.LT||n.operatorCode==sym.LEQ
        				|| n.operatorCode==sym.AND||n.operatorCode==sym.OR
        				|| n.operatorCode==sym.SLASH||n.operatorCode==sym.TIMES
        				|| n.operatorCode==sym.AND || n.operatorCode==sym.OR);
		  }
		  catch(RuntimeException e) {
			  //should never happen
			  typeErrors++;
			  System.out.println("Invalid operator");
		  }
	
		  this.visit(n.leftOperand);
		  this.visit(n.rightOperand);
		
		  //Check that right and left operands are both scalars
		  if ( (!isScalar(n.rightOperand.kind)) || (!isScalar(n.leftOperand.kind)) ) {
			  System.out.println(error(n) + "Both sides of " + opToString(n.operatorCode) + "must be scalar");
			  typeErrors++;
		  }
		  
		  //binaryOpNode must have kind of value
		  if (n.kind != ASTNode.Kinds.Value) {
			  System.out.println(error(n) + opToString(n.operatorCode) + "must resolve to a value");
			  typeErrors++;
		  }
		  
		  //Arithmetic operations
		  if (n.operatorCode== sym.PLUS||n.operatorCode==sym.MINUS||n.operatorCode==sym.TIMES||n.operatorCode==sym.SLASH){
			  n.type = ASTNode.Types.Integer;
			  typeMustBe(n.leftOperand.type, ASTNode.Types.Integer, ASTNode.Types.Character, error(n) + "Left"
			  		+ " operand of" + opToString(n.operatorCode) +  "must be an int or char.");
			  typeMustBe(n.rightOperand.type, ASTNode.Types.Integer, ASTNode.Types.Character, error(n) + 
					  "Right operand of" + opToString(n.operatorCode) +  "must be an int or char.");
		  }
		  //Conditional and/or
		  else if (n.operatorCode==sym.CAND||n.operatorCode==sym.COR) {
			  n.type = ASTNode.Types.Boolean;
			  typeMustBe(n.leftOperand.type, ASTNode.Types.Boolean, error(n) + "Left operand of"
		  		+ opToString(n.operatorCode) + "must be boolean");
			  typeMustBe(n.rightOperand.type, ASTNode.Types.Boolean, error(n) + "Right operand of"
		  		+ opToString(n.operatorCode) + "must be boolean");
		  }
		  
		  //Bitwise and/or
		  else if (n.operatorCode==sym.AND||n.operatorCode==sym.OR) {
			  if (n.leftOperand.type == ASTNode.Types.Boolean) {
				  typeMustBe(n.rightOperand.type, ASTNode.Types.Boolean, error(n) + "The operands of" +
						  " both sides of " + opToString(n.operatorCode) + " must be the same type");
				  n.type = ASTNode.Types.Boolean;
			  }
			  else if ( (n.rightOperand.type == ASTNode.Types.Integer) || (n.rightOperand.type == ASTNode.Types.Character)) {
		  			typeMustBe(n.rightOperand.type, ASTNode.Types.Integer, ASTNode.Types.Character, error(n) 
		  				+ "The operands of both sides of " + opToString(n.operatorCode) + "must be the same type");
		  			n.type = ASTNode.Types.Boolean;
			  }
			  else if (n.leftOperand.type != ASTNode.Types.Error) {
				  System.out.println(error(n) + "Operands and both sides of " + opToString(n.operatorCode)
  					+ "must be boolean or arithmetic");
			  }
		  }
		  
		 //Must be a comparison operator
		  else { 
        		n.type = ASTNode.Types.Boolean;
        		if (n.leftOperand.type == ASTNode.Types.Boolean) {
        			typeMustBe(n.rightOperand.type, ASTNode.Types.Boolean, error(n) + "The operands of "
        					+ "both sides of " + opToString(n.operatorCode) + "must be the same type");
        		}
        		else if (n.leftOperand.type == ASTNode.Types.Integer || n.leftOperand.type == ASTNode.Types.Character) {
        			typeMustBe(n.rightOperand.type, ASTNode.Types.Integer, ASTNode.Types.Character, error(n) + "The operands"
        					+ " of both sides of " + opToString(n.operatorCode) + "must be the same type");
        		}
        		else if (n.leftOperand.type != ASTNode.Types.Error) {
        			System.out.println(error(n) + "Operands and both sides of " + opToString(n.operatorCode)
        			+ "must be boolean or arithmetic");
        		}
		  }
		
	  }

	
	
	void visit(intLitNode n){
	//      All intLits are automatically type-correct
	}


 
// Extend these unparsing methods to correctly unparse CSX AST nodes
	 
	 void visit(classNode n){
		SymbolInfo	id;
		id = new SymbolInfo(n.className.idname, ASTNode.Kinds.VisibleLabel, 
				ASTNode.Types.Void);
		try {
			st.insert(id);
			st.openScope();
			//Type check the members of the class
			this.visit(n.members); 
			st.closeScope();
		} catch (DuplicateException e) {
			// Can't occur
		} catch (EmptySTException e) {
			// Can't occur
		}
	}

	 void  visit(memberDeclsNode n){
		this.visit(n.fields);
		this.visit(n.methods);
		secondPass = true;
		this.visit(n.methods);
		secondPass = false;
		if (!mainExists) {
			typeErrors++;
			System.out.println("No main method declared");
		}
	 }
	 
	 void  visit(methodDeclsNode n){
		this.visit(n.thisDecl);
		this.visit(n.moreDecls);
	 }
	 
	 void visit(nullStmtNode n){}
	 
	 void visit(nullReadNode n){}

	 void visit(nullPrintNode n){}

	 void visit(nullExprNode n){}

	 void visit(nullMethodDeclsNode n){}

	 void visit(methodDeclNode n){
		 //Method declarations require two passes due to forward references
		 //First pass - handle symbol table declarations for all methods, do not handle calls
		 if (secondPass == false) {
			 SymbolInfo method = new SymbolInfo(n.name.idname, ASTNode.Kinds.Method, n.returnType.type);
			 
			 //In the first pass, just populates the arg list of the method in the symbol table
			 st.openScope();
			 this.visit(n.args);
			 method.addParmListToMethod(st.parmList);
			 try {
					st.closeScope();
			} catch (EmptySTException e) {}
				 
			 
			 
			 SymbolInfo id = (SymbolInfo) st.localLookup(n.name.idname);
			 if (id == null) {
				 try {
		                st.insert(method);
					} 
					catch (DuplicateException d) 
						{ /* can't happen */ }
					catch (EmptySTException e)
						{ /* can't happen */ }
			 }
			 //We have a conflict, but this may be valid due to overloading
			 else {
				  if (id.kind != ASTNode.Kinds.Method) {
					  System.out.println(error(n) + "Method " + id.name() +  " already used in previous declaration");
					  typeErrors++;
					  n.name.type = ASTNode.Types.Error;
				  }
				//Check id to see if we have an overlap in arguments
				  else if (id.hasIdenticalParmList(st.parmList)) {
					  System.out.println(error(n) + "Identical method " + id.name() +" already declared");
					  typeErrors++;
					  n.name.type = ASTNode.Types.Error;
					  }
				  //If we get here we are valid so add to symbol table
				  else {
					  try {
			                st.insert(method);
						} 
						catch (DuplicateException d) 
							{ /* can't happen */ }
						catch (EmptySTException e)
							{ /* can't happen */ }
				 }
			 }
			 
			 //Also handle checking for main while we're at it
			 if(n.name.idname.toLowerCase().equals("main")){
				mainExists = true;
				if(!n.args.isNull()){
					System.out.println(error(n)+"The main method must not have any arguments");
					typeErrors++;
					n.name.type = ASTNode.Types.Error;
				}

				if(n.returnType.type != ASTNode.Types.Void){
					System.out.println(error(n)+"The main method must return void");
					typeErrors++;
					n.name.type = ASTNode.Types.Error;
				}
			}
		 }
		 
		 //Otherwise we're on the second pass - at this point all methods should be in symbol table
		 else {
			 //Create new scope
			 st.openScope();
			 currentMethod = n;
			 
			 this.visit(n.args);
			 
			 //Visit the statements and declarations
			this.visit(n.decls);
			this.visit(n.stmts);
			
			//Close the current scope
			try {
				st.closeScope();
			} catch (EmptySTException e) {
				// Nothing to do
			}
		 }
	 }
	 
	 void visit(incrementNode n){
		 this.visit(n.target);
		 typeMustBe(n.target.type, ASTNode.Types.Integer, ASTNode.Types.Character, error(n) + "A value"
		 		+ " that is not a character or integer may not be incremented");
		 if ((n.target.kind != ASTNode.Kinds.Var) && (n.target.kind != ASTNode.Kinds.ArrayParm) && (n.target.kind != ASTNode.Kinds.ScalarParm)) {
			 if (! isScalar(n.target.kind)) {
				 System.out.println(error(n) + "Operand of ++ must be scalar");
			 }
			 else {
				 System.out.println(error(n) + "Target of ++ cannot be changed");
			 }
			typeErrors++;
		 }
	 }
	 
	 void visit(decrementNode n){
		 this.visit(n.target);
		 typeMustBe(n.target.type, ASTNode.Types.Integer, ASTNode.Types.Character, error(n) + "A value"
			 		+ " that is not a character or integer may not be decremented");
		 if ((n.target.kind != ASTNode.Kinds.Var) && (n.target.kind != ASTNode.Kinds.ArrayParm) && (n.target.kind != ASTNode.Kinds.ScalarParm)) {
				 System.out.println(error(n) + "Target of -- cannot be changed");
				 typeErrors++;
		 }
	 }
	 
	void visit(argDeclsNode n){
		this.visit(n.thisDecl);
		this.visit(n.moreDecls);
	}

	void visit(nullArgDeclsNode n){}

	
	void visit(valArgDeclNode n){
		//In the first pass, just populate the argList of currentMethodID
		if (secondPass == false) {
			st.addParmToSt(n.argType.type, ASTNode.Kinds.ScalarParm);
		}
		//In the second pass do the actual checking
		else {
			SymbolInfo id;
			id = (SymbolInfo) st.localLookup(n.argName.idname);
			if (id != null) {
				System.out.println(error(n) + id.name()+ " has already been declared");
				typeErrors++;
				n.argName.type = ASTNode.Types.Error;
			}
			else {
				id = new SymbolInfo(n.argName.idname, ASTNode.Kinds.ScalarParm, n.argType.type);
				n.argName.type = n.argType.type;
				try {
					st.insert(id);
				} catch (DuplicateException d) 
				{ /* can't happen */ }
				catch (EmptySTException e) 
				{ /* can't happen */ }
				n.argName.idinfo=id;
			}
		}
	}
	
	void visit(arrayArgDeclNode n){
		//In the first pass, just populate the argList of currentMethodID
		if (secondPass == false) {
			st.addParmToSt(n.elementType.type, ASTNode.Kinds.ArrayParm);
		}
		//In the second pass do the actual checking
		else {
			SymbolInfo id;
			id = (SymbolInfo) st.localLookup(n.argName.idname);
			if (id != null) {
				System.out.println(error(n) + id.name()+ " has already been declared");
				typeErrors++;
				n.argName.type = ASTNode.Types.Error;
			}
			else {
				n.argName.type = n.elementType.type;
				id = new SymbolInfo(n.argName.idname, ASTNode.Kinds.ArrayParm, n.argName.type);
				n.argName.type = n.argName.type;
				try {
					st.insert(id);
				} catch (DuplicateException d) 
				{ /* can't happen */ }
				catch (EmptySTException e) 
				{ /* can't happen */ }
				n.argName.idinfo=id;
			}
		}
	}
	
	void visit(constDeclNode n){
		//Check that identNode is not already in symbol table
		SymbolInfo id;
		id = (SymbolInfo) st.localLookup(n.constName.idname);
    	if (id != null) {
           	System.out.println(error(n) + id.name()+" is already declared.");
            typeErrors++;
            n.constName.type = ASTNode.Types.Error;
    	}
    	else {
	    	//Type check the constant value expression
	    	this.visit(n.constValue);
	    	
	    	//Check that constant value's kind is scalar
	   		if (!isScalar(n.constValue.kind)){
	   			System.out.println(error(n) + "Initial value must be scalar");
	   			typeErrors++; 
	   		}
	   		
	   		//Enter ID into symbol table
	   		id = new SymbolInfo(n.constName.idname,ASTNode.Kinds.Value,n.constValue.type);
       		n.constName.type = n.constValue.type;
       		try {
                st.insert(id);
			} 
			catch (DuplicateException d) 
				{ /* can't happen */ }
			catch (EmptySTException e)
				{ /* can't happen */ }
            n.constName.idinfo=id;
    	}
	}
	 
	 void visit(arrayDeclNode n){
		SymbolInfo id = (SymbolInfo) st.localLookup(n.arrayName.idname);
		if (id != null) {
			System.out.println(error(n) + n.arrayName.idname + "already declared");
			typeErrors++;
			n.arrayName.type = ASTNode.Types.Error;
		}
		else {
			id = new SymbolInfo(n.arrayName.idname,ASTNode.Kinds.Array, n.elementType.type);
       		//Check that the arraySize is greater than 0
			if (n.arraySize.intval <= 0) {
				System.out.println(error(n) + "Array " + n.arrayName.idname + " must be initialized with"
						+ " a size larger than 0");
			}
			else {
				id.setArrayInfo(n.arraySize.intval);
				n.arrayName.type = n.elementType.type;
				try {
					st.insert(id);
				} 
				catch (DuplicateException d) 
					{ /* can't happen */ }
				catch (EmptySTException e)
					{ /* can't happen */ }
			}
		}
	 }
	
	void visit(charTypeNode n){
		//handled in AST
	}
	void visit(voidTypeNode n){
		//handled in AST
	}

	void visit(whileNode n){
		this.visit(n.condition);
		//Type must be boolean kind must be scalar
		typeMustBe(n.condition.type, ASTNode.Types.Boolean, error(n) + "Condition's type must be Boolean");
		if (!isScalar(n.condition.kind)) {
			System.out.println(error(n) + "Condition's kind must be scalar");
			typeErrors++;
		}
		//If no label, just type check the body
		if (n.label.isNull()) {
			this.visit(n.loopBody);
			return;
		}
		else {
			//Check if the label is already declared
			identNode identLabel = (identNode) n.label;
			SymbolInfo id = (SymbolInfo) st.localLookup(identLabel.idname);
			if (id != null) {
				System.out.println(error(n) + identLabel.idname + " is already declared");
				typeErrors++;
				identLabel.type = ASTNode.Types.Error;
				this.visit(n.loopBody);
			}
			//Add the label to the symbol table as a visible label, type check the body, then hide the label
			else {
				id = new SymbolInfo(identLabel.idname, ASTNode.Kinds.VisibleLabel, ASTNode.Types.Void);
				try {
					st.insert(id);
				}
				catch (DuplicateException d) 
					{ /* can't happen */ }
				catch (EmptySTException e)
					{ /* can't happen */ }
				this.visit(n.loopBody);
				id.kind = ASTNode.Kinds.HiddenLabel;
			}
		}
	  }

	void visit(breakNode n){
		SymbolInfo id = (SymbolInfo) st.globalLookup(n.label.idname);
		if (id == null) {
			System.out.println(error(n) + n.label.idname + " doesn't label an enclosing while loop");
			typeErrors++;
			return;
		}
		else {
			if (id.kind != ASTNode.Kinds.VisibleLabel) {
				System.out.println(error(n) + n.label.idname + " doesn't label an enclosing while loop");
				typeErrors++;
			}
		}
	}
	
	void visit(continueNode n){
		SymbolInfo id = (SymbolInfo) st.globalLookup(n.label.idname);
		if (id == null) {
			System.out.println(error(n) + n.label.idname + " doesn't label an enclosing while loop");
			typeErrors++;
			return;
		}
		else {
			if (id.kind != ASTNode.Kinds.VisibleLabel) {
				System.out.println(error(n) + n.label.idname + " doesn't label an enclosing while loop");
				typeErrors++;
			}
		}
	}
	  
	void visit(callNode n){
		boolean temp;
		ArrayList<ParmInfo> currentParms;
		SymbolInfo id = (SymbolInfo) st.globalLookup(n.methodName.idname);
		if (id == null) {
			System.out.println(error(n) + "Method " + n.methodName.idname + " is undeclared.");
			typeErrors++;
			return;
		}
		if (id.type != ASTNode.Types.Void && id.kind == ASTNode.Kinds.Method) {
			System.out.println(error(n) + "Method call " + n.methodName.idname + " is a procedure and must return Void");
			typeErrors++;
			return;
		}
		if (id.kind != ASTNode.Kinds.Method) {
			System.out.println(error(n) + n.methodName.idname + " is not a method and cannot be called");
			typeErrors++;
			return;
		}
		temp = secondPass;
		secondPass = true;
		this.visit(n.args);
		
		secondPass = temp;
		currentParms = buildArgList(n.args);
		if (!id.hasIdenticalParmList(currentParms)) {
			typeErrors++;
			n.methodName.type = ASTNode.Types.Error;
			//No parameters
			if (id.parmListList.size() == 0) {
				System.out.println(error(n) + "Method " + n.methodName.idname + " requires 0 parameters");
				return;
			}
			//Exactly one matching declaration
			else if (id.parmListList.size() == 1) {
				if (id.parmListList.get(0).size() != currentParms.size()) {
					System.out.println(error(n) + "Method " + n.methodName.idname + " requires " + id.parmListList.get(0).size() + " parameters");
				}
				//Same number of parameters - type or kind must be wrong
				else {
					for (int i =0 ; i < id.parmListList.get(0).size(); i++) {
						if  (!id.parmListList.get(0).get(i).isSame(currentParms.get(i))) {
							System.out.println(error(n) + "In call to " + n.methodName.idname + " parameter " + (i+1) + " has incorrect type");
						}
					}
				}
				return;
			}
			
			//Multiple overloaded declarations with none of them 
			else {
				System.out.println(error(n) + " None of the " + id.parmListList.size() + " definitions of method " + n.methodName.idname + " match the parameters in this call");
			}
			
		}
	}

	  
	  void visit(readNode n){
		  this.visit(n.targetVar);
		  typeMustBe(n.targetVar.type, ASTNode.Types.Integer, ASTNode.Types.Character, error(n) + "Only integers"
		  		+ " and characters may be read");
		  this.visit(n.moreReads);
	  }
	  

	  void visit(returnNode n){
		  if (n.returnVal.isNull()) {
			  typeMustBe(currentMethod.returnType.type, ASTNode.Types.Void, error(n) + "Expected a void return type");
		  }
		  else {
			  exprNode exprReturnVal = (exprNode) n.returnVal;
			  if (!isScalar(exprReturnVal.kind)) {
				  System.out.println(error(n) + "Return value must be scalar");
				  typeErrors++;
			  }
			  typeMustBe(currentMethod.returnType.type, exprReturnVal.type, error(n) + "Expected a return value of "
			  		+ currentMethod.returnType.type.name());
		  }
	  }

	  
	  void visit(argsNode n){
		  this.visit(n.argVal);
		  this.visit(n.moreArgs);
	  }
	  	
	  void visit(nullArgsNode n){}
		
	  void visit(castNode n){
		  this.visit(n.operand);
		  if (n.operand.type!=ASTNode.Types.Error && n.operand.type != ASTNode.Types.Integer
				  && n.operand.type != ASTNode.Types.Boolean && (n.operand.type != ASTNode.Types.Character || n.operand.kind == ASTNode.Kinds.String) ) {
			  System.out.println(error(n) + "Only integers, booleans, and characters may be type cast");
			  typeErrors++;
		  }
		  if (n.resultType.type == ASTNode.Types.Boolean) {
			  n.type = ASTNode.Types.Boolean;
		  }
		  if (n.resultType.type == ASTNode.Types.Character) {
			  n.type = ASTNode.Types.Character;
		  }
		  if (n.resultType.type == ASTNode.Types.Integer) {
			  n.type = ASTNode.Types.Integer;
		  }
		  n.kind = n.operand.kind;
	  }

	  void visit(fctCallNode n){
		  	boolean temp;
		  	temp = secondPass;
			ArrayList<ParmInfo> currentParms;
			SymbolInfo id = (SymbolInfo) st.globalLookup(n.methodName.idname);
			if (id == null) {
				System.out.println(error(n) + n.methodName.idname + " is undeclared.");
				typeErrors++;
				n.methodName.type = ASTNode.Types.Error;
				return;
			}
			else if(id.kind != ASTNode.Kinds.Method) {
				System.out.println(error(n) + id.name() + "is not a method");
				typeErrors++;
				n.methodName.type = ASTNode.Types.Error;
				return;
			}
			else {
				n.type = id.type;
				n.kind = ASTNode.Kinds.ScalarParm;
				if (id.type == ASTNode.Types.Void) {
				System.out.println(error(n) + "Method call " + n.methodName.idname + " is a procedure and should return void");
				typeErrors++;
				return;
				}	
			}
			//Type check the arguments
			secondPass = true;
			this.visit(n.methodArgs);
			secondPass = temp;
			
			//Build the arglist
			currentParms = buildArgList(n.methodArgs);
			
			if (!id.hasIdenticalParmList(currentParms)) {
				typeErrors++;
				n.methodName.type = ASTNode.Types.Error;
				//No parameters
				if (id.parmListList.size() == 0) {
					System.out.println(error(n) + "Method " + n.methodName.idname + " requires 0 parameters");
					return;
				}
				//Exactly one matching declaration
				else if (id.parmListList.size() == 1) {
					if (id.parmListList.get(0).size() != currentParms.size()) {
						System.out.println(error(n) + "Method " + n.methodName.idname + " requires " + id.parmListList.get(0).size() + " parameters");
					}
					//Same number of parameters - type or kind must be wrong
					else {
						for (int i =0 ; i < id.parmListList.get(0).size(); i++) {
							if  (!id.parmListList.get(0).get(i).isSame(currentParms.get(i))) {
								System.out.println(error(n) + "In call to " + n.methodName.idname + " parameter " + (i+1) + " has incorrect type");
							}
						}
					}
					return;
				}
				
				//Multiple overloaded declarations with none of them 
				else {
					System.out.println(error(n) + " None of the " + id.parmListList.size() + " definitions of method " + n.methodName.idname + " match the parameters in this call");
				}
				
			}
	  }

	  void visit(unaryOpNode n){
		  this.visit(n.operand);
		  if (n.operand.type == ASTNode.Types.Boolean) {
			  n.type = ASTNode.Types.Boolean;
		  }
		  else if (n.operand.type == ASTNode.Types.Integer || n.operand.type == ASTNode.Types.Character) {
			  n.type = ASTNode.Types.Integer;
		  }
		  else if (n.operand.type != ASTNode.Types.Error) {
			  if (!isScalar(n.operand.kind)) {
				  System.out.println(error(n) + "Operand of ! must be scalar");
			  }
			  System.out.println(error(n) + "! operator must act on a boolean, integer, or character");
			  typeErrors++;
		  }
	  }

	
	void visit(charLitNode n){
		//handled in AST
	}
	  
	void visit(strLitNode n){
		n.kind = ASTNode.Kinds.String;
	}

	
	void visit(trueNode n){
		//handled in AST
	}

	void visit(falseNode n){
		//handled in AST
	}

	void visit(bitStringNode n){
		//handled in AST
	}

}
