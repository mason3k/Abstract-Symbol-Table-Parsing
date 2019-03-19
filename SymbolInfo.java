import java.util.ArrayList;
import java.util.List;

/**************************************************
*  class used to hold information associated w/
*  Symbs (which are stored in SymbolTables)
*  Update to handle arrays and methods
*
****************************************************/

class SymbolInfo extends Symb {
 public ASTNode.Kinds kind; // Should always be Var in CSX-lite
 public ASTNode.Types type; // Should always be Integer or Boolean in CSX-lite
 private int arraySize;
 public ArrayList<ArrayList<ParmInfo>> parmListList;

 public SymbolInfo(String id, ASTNode.Kinds k, ASTNode.Types t){    
	super(id);
	kind = k; 
	type = t; 
	arraySize=0;
	parmListList = new ArrayList<ArrayList<ParmInfo>>();
	};
// public SymbolInfo(String id, int k, int t){
//	super(id);
//	kind = new Kinds(k); type = new Types(t);};
	
public void setArrayInfo(int arraySize) {
	this.arraySize = arraySize;
}

public int getArraySize() {
	return this.arraySize;
}

public void addParmListToMethod(ArrayList<ParmInfo> parmList) {
	parmListList.add(parmList);
}

public boolean hasIdenticalParmList(ArrayList<ParmInfo> parmList) {
	boolean result = true;
	for (int i=0; i < parmListList.size(); i++) {
		ArrayList<ParmInfo> referenceList = parmListList.get(i);
		if (parmList.size() == referenceList.size()) {
			if (parmList.size() == 0) {
				result = true;
			}
			else {
				for (int j=0; j < parmList.size(); j++) {
					if (!(parmList.get(j).isSame(referenceList.get(j)))) {
					result = false;
					break;
					}
				}
				if (result==true) {
					return true;
				}
			}
		}
		else {
			result = false;
		}
	}
	return result;
}

 public String toString(){
             return "("+name()+": kind=" + kind+ ", type="+  type+")";};
}

