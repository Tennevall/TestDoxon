package testdoxon.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;


public class StatisticDialog extends Dialog {

	public StatisticDialog(Shell parent) {
		super(parent);
		
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "OK", true);
	}



	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		
		Label label = new Label(container, SWT.ALPHA);
		label.setLayoutData(new GridBagLayout());
		
		label.setText("Hej");
		
		return container;	
			
	}

	@Override
	protected Point getInitialSize() {
		return new Point(450,  300);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Configure...");
	}

}
