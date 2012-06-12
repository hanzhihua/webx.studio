package webx.studio.projectcreation.ui.structure;

public class ChildNode {

	private IStructureRootNode parent;
	private String name;

	public ChildNode(IStructureRootNode parent,String name){
		this.parent = parent;
		this.name = name;
	}

	public IStructureRootNode getParent() {
		return parent;
	}
	public String getName() {
		return name;
	}



}
