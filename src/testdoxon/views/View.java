package testdoxon.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.*;

import exceptionHandlers.TDException;
import handlers.FileHandler;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import java.io.File;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class View extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "testdoxon.views.View";

	private TableViewer viewer;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;

	private ViewContentProvider viewContentProvider;

	private FileHandler fileHandler;
	private File currentFile;
	private File currentTestFile;
	private IResourceChangeListener saveFileListener;


	/*
	 * The content provider class is responsible for providing objects to the view.
	 * It can wrap existing objects in adapters or simply return objects as-is.
	 * These objects may be sensitive to the current input of the view, or ignore it
	 * and always show the same content (like Task List, for example).
	 */

	class ViewContentProvider implements IStructuredContentProvider {

		private String testPath = "";

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			if (newInput instanceof String) {
				this.testPath = (String) newInput;
				this.testPath = this.testPath.replaceAll("\\\\", "\\/");
			}
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			try {
				return fileHandler.getMethodsFromFile(testPath);
			} catch (TDException e) {
				return new String[] { e.getMessage() };
			}
		}
	}

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}

		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}

		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_FORWARD_DISABLED);
		}
	}

	@SuppressWarnings("deprecation")
	class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 */
	public View() {
		this.fileHandler = new FileHandler();
		this.currentFile = null;
		this.viewContentProvider = new ViewContentProvider();

		this.saveFileListener = new IResourceChangeListener() {
			public void resourceChanged(IResourceChangeEvent event) {
				IResourceDelta resourceDelta = event.getDelta();

				IResourceDeltaVisitor recourceDeltaVisitor = new IResourceDeltaVisitor() {
					boolean changed = false;

					@Override
					public boolean visit(IResourceDelta arg0) throws CoreException {
						IResource resource = arg0.getResource();
						if (resource.getType() == IResource.FILE && !changed) {
							if (arg0.getKind() == IResourceDelta.CHANGED) {
								System.out.println("CHANGED");
								updateTable();
								changed = true;
							}
						}
						return true;
					}

				};

				try {
					resourceDelta.accept(recourceDeltaVisitor);
				} catch (CoreException e) {
					System.out.println(e.getMessage());
				}

			}
		};
	}

	private void updateTable() {
		File file = this.getSite().getWorkbenchWindow().getActivePage().getActivePart().getSite().getPage()
				.getActiveEditor().getEditorInput().getAdapter(File.class);

		if (file != null) {
			currentFile = file;
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					viewer.setInput(currentFile.getAbsolutePath());
				}
			});
		}
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	@SuppressWarnings("deprecation")
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(this.viewContentProvider);
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new NameSorter());
		viewer.setInput(getViewSite());
		viewer.getControl().setBackground(new Color(null, 255, 255, 230));

		ResourcesPlugin.getWorkspace().addResourceChangeListener(saveFileListener, IResourceChangeEvent.POST_BUILD);

		ISelectionService iSelectionService = this.getSite().getWorkbenchWindow().getSelectionService();
		iSelectionService.addPostSelectionListener(new ISelectionListener() {

			@Override
			public void selectionChanged(IWorkbenchPart arg0, ISelection arg1) {
				if (arg0.getTitle().matches(".*\\.java")) {

					File file = (File) arg0.getSite().getPage().getActiveEditor().getEditorInput()
							.getAdapter(File.class);

					if (currentFile == null || !file.getAbsolutePath().equals(currentFile.getAbsolutePath())) {
						currentFile = file;
						if(currentFile.getName().matches("^Test.*"))
						{
							viewer.setInput(currentFile.getAbsolutePath());
						}
						else
						{
							String[] parts = currentFile.getAbsolutePath().split("\\\\");
							String newFile = "";
							for(int i = 0; i<parts.length-2;i++)
							{
								newFile += parts[i] + "\\";
							}
							newFile += "tests\\Test" + currentFile.getName();
							currentTestFile = new File(newFile);
							viewer.setInput(currentTestFile.getAbsolutePath());
						}
						
					}

				}
			}
		});

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "TestDoxon.viewer");
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				View.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				showMessage("Action 1 executed");
			}
		};
		action1.setText("Action 1");
		action1.setToolTipText("Action 1 tooltip");
		action1.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

		action2 = new Action() {
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				@SuppressWarnings("unused")
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				//showMessage("Double-click detected on " + obj.toString() + currentTestFile.getAbsolutePath());
				File file = (File)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput().getAdapter(File.class);
				
				if(file.getAbsolutePath().equals(currentTestFile.getAbsolutePath().toString()))
				{
					showMessage("same file");
				}
				else
				{
					showMessage("another file");
				}
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(), "TestDoxon", message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(@SuppressWarnings("rawtypes") Class arg0) {
		return null;
	}
}