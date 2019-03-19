
public class ParmInfo {
	public ASTNode.Types type;
	public ASTNode.Kinds kind;
	
	ParmInfo(ASTNode.Types type, ASTNode.Kinds kind){
		this.type = type;
		this.kind = kind;
	}
	public ASTNode.Types getType() {
		return this.type;
	}
	
	private static boolean KindsAreCompatible(ASTNode.Kinds kind1, ASTNode.Kinds kind2){
		if (kind1==ASTNode.Kinds.Var || kind1 == ASTNode.Kinds.Value || kind1 == ASTNode.Kinds.ScalarParm) {
			return ((kind2==ASTNode.Kinds.Var || kind2 == ASTNode.Kinds.Value || kind2 == ASTNode.Kinds.ScalarParm));
		}
		if (kind1==ASTNode.Kinds.Array || kind1==ASTNode.Kinds.ArrayParm) {
			return (kind2 == ASTNode.Kinds.Array || kind2==ASTNode.Kinds.ArrayParm);
		}
		return false;
	}
	boolean isSame(ParmInfo otherParm) {
		if ((otherParm.type == this.type && otherParm.kind == this.kind)
				|| (otherParm.type == this.type && KindsAreCompatible(otherParm.kind,this.kind))) {
			return true;
		}
		else {
			return false;
		}
	}

}
