package testdoxon.gui;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import testdoxon.model.TestFile;

public class ClassPathsComboContentProvider implements IStructuredContentProvider {

	TestFile[] testClassPaths;
	
	public ClassPathsComboContentProvider () {
		this.testClassPaths = null;
	}
	
	@Override
	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		if (newInput != null && newInput instanceof TestFile[]) {
			this.testClassPaths = (TestFile[]) newInput;
		}
	}

	@Override
	public void dispose() {
	}
	
	@Override
	public Object[] getElements(Object arg0) {
		if(this.testClassPaths != null) {
			return this.testClassPaths;
		}
		
		return new String[] {};
	}

}
