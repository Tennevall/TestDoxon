package testdoxon.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.*;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;

import exceptionHandlers.TDException;
import handlers.FileHandler;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

import java.io.File;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.*;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.*;
import org.eclipse.ui.ide.IDE;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyledText;

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

	private Color widgetColor;

	private TableViewer viewer;
	private Label header;

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

		private String filePath = "";
		private String fileName = "";

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			if (newInput instanceof File) {
				File currFile = (File) newInput;
				this.filePath = currFile.getAbsolutePath().replaceAll("\\\\", "\\/");
				String temp[] = currFile.getAbsolutePath().toString().split("\\\\");
				boolean pass = false;
				this.fileName = "";
				
				for(int i = 0; i<temp.length ; i++)
				{
					if(temp[i].contains("src") || pass)
					{
						if(temp[i].contains(currFile.getName()))
						{
							this.fileName += currFile.getName();
							pass = false;
						}
						else
						{
							pass = true;
							this.fileName += temp[i] + ".";
						}
					}
				}	
			}
		}

		public void dispose() {
		}

		public Object[] getElements(Object parent) {
			try {
				header.setText(this.fileName);
				return fileHandler.getMethodsFromFile(this.filePath);
			} catch (TDException e) {
				header.setText(e.getMessage());
				return new String[] {};
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
		this.currentTestFile = null;
		this.viewContentProvider = new ViewContentProvider();
		this.widgetColor = new Color(null, 255, 255, 230);

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
		IEditorPart iEditorPart = this.getSite().getWorkbenchWindow().getActivePage().getActivePart().getSite().getPage().getActiveEditor();

		if (iEditorPart != null) {
			File file = iEditorPart.getEditorInput().getAdapter(File.class);

			if (file != null) {
				currentFile = file;
				currentTestFile = currentFile;
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						viewer.setInput(currentTestFile);
					}
				});
			}
		}
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	@SuppressWarnings("deprecation")
	public void createPartControl(Composite parent) {
		GridLayout gl = new GridLayout(1, false);
		gl.marginLeft = 10;
		gl.marginHeight = 10;
		gl.marginRight = 0;
		gl.marginBottom = 0;
		gl.verticalSpacing = 15;

		parent.setLayout(gl);
		parent.setBackground(widgetColor);

		header = new Label(parent, SWT.NONE);
		header.setText("Test label");
		header.setBackground(widgetColor);
		header.setSize(200, 20);

		GridData gridDataLabel = new GridData(SWT.FILL, SWT.NONE, true, false);
		header.setLayoutData(gridDataLabel);

		Display display = Display.getCurrent();
		FontDescriptor fd = FontDescriptor.createFrom(header.getFont());
		Font font = fd.setStyle(SWT.BOLD).createFont(display);
		header.setFont(font);

		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(this.viewContentProvider);
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new NameSorter());

		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				viewer.setInput(getViewSite());
			}
		});

		viewer.getControl().setBackground(widgetColor);
		GridData gridDataTableView = new GridData(SWT.FILL, SWT.FILL, true, true);
		viewer.getControl().setLayoutData(gridDataTableView);

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

						if (currentFile.getName().matches("^Test.*")) {
							currentTestFile = currentFile;
							Display.getDefault().syncExec(new Runnable() {
								@Override
								public void run() {
									viewer.setInput(currentTestFile);
								}
							});
						} else {
							String[] parts = currentFile.getAbsolutePath().split("\\\\");
							String newFile = "";
							
							for (int i = 0; i < parts.length - 2; i++) {
								newFile += parts[i] + "\\";
							}
							newFile += "tests\\Test" + currentFile.getName();
							currentTestFile = new File(newFile);
							Display.getDefault().syncExec(new Runnable() {
								@Override
								public void run() {
									viewer.setInput(currentTestFile);
								}
							});
						}
					}
				}
			}
		});

		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().addPartListener(new IPartListener() {

			@Override
			public void partOpened(IWorkbenchPart arg0) {
			}

			@Override
			public void partDeactivated(IWorkbenchPart arg0) {
			}

			@Override
			public void partClosed(IWorkbenchPart arg0) {
			}

			@Override
			public void partBroughtToTop(IWorkbenchPart arg0) {
			}

			@Override
			public void partActivated(IWorkbenchPart arg0) {
				IEditorPart iep = arg0.getSite().getPage().getActiveEditor();
				if (iep != null) {
					AbstractTextEditor e = (AbstractTextEditor) iep;
					Control adapter = e.getAdapter(Control.class);

					if (adapter instanceof StyledText) {
						StyledText text = (StyledText) adapter;
						text.addCaretListener(new CaretListener() {

							@Override
							public void caretMoved(CaretEvent arg0) {
								String word = getWordUnderCaret(arg0.caretOffset, text);

								if (word.length() > 0 && Character.isUpperCase(word.charAt(0))) {
									String fileToLookFor = "Test" + word + ".java";
									String[] parts = currentFile.getAbsolutePath().split("\\\\");
									String newFile = "";
									
									for (int i = 0; i < parts.length - 2; i++) {
										newFile += parts[i] + "\\";
									}
									newFile += "tests\\" + fileToLookFor;
									currentTestFile = new File(newFile);
									Display.getDefault().syncExec(new Runnable() {
										@Override
										public void run() {
											viewer.setInput(currentTestFile);
										}
									});
								}
							}
						});
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

	private String getWordUnderCaret(int pos, StyledText text) {
		int lineOffset = pos - text.getOffsetAtLine(text.getLineAtOffset(pos));
		String line = text.getLine(text.getLineAtOffset(pos));
		String[] words = line.split("[ \t\\\\(\\\\);\\\\.{}]");
		String desiredWord = "";

		for (String word : words) {
			if (lineOffset < word.length()) {
				desiredWord = word;
				break;
			}
			lineOffset -= word.length() + 1;
		}
		return desiredWord;
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
	}

	private void fillContextMenu(IMenuManager manager) {
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
	}

	private void makeActions() {
		doubleClickAction = new Action() {
			@SuppressWarnings("deprecation")
			public void run() {
				ISelection selection = viewer.getSelection();
				@SuppressWarnings("unused")
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				File file = (File) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.getActiveEditor().getEditorInput().getAdapter(File.class);

				if (file.getAbsolutePath().equals(currentTestFile.getAbsolutePath().toString())) {
					// Opened class is the correct Test class, so just move to the correct line in
					// that class.
					ITextEditor editor = (ITextEditor) getSite().getWorkbenchWindow().getActivePage().getActiveEditor();
					IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());

					if (document != null) {
						IRegion lineInfo = null;

						try {
							int lineNumber = 1;
							try {
								lineNumber = fileHandler.getLineNumberOfSpecificMethod(
										currentTestFile.getAbsolutePath(), obj.toString());
								if (lineNumber == -1) {
									lineNumber = 1;
								}
							} catch (TDException e1) {
								e1.printStackTrace();
							}

							lineInfo = document.getLineInformation(lineNumber - 1);
						} catch (BadLocationException e) {

						}
						if (lineInfo != null) {
							editor.selectAndReveal(lineInfo.getOffset(), lineInfo.getLength());
						}
					}
				} else {
					// Open Test class and jump to correct line
					IPath location = Path.fromOSString(currentTestFile.getAbsolutePath());
					IFile iFile = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(location);

					if (iFile != null) {
						IWorkbenchPage iWorkbenchPage = getSite().getPage();
						HashMap<String, Comparable> map = new HashMap<String, Comparable>();
						int lineNumber = 0;					
						try {
							lineNumber = fileHandler.getLineNumberOfSpecificMethod(iFile.getRawLocation().toOSString(),
									obj.toString());
							if (lineNumber == 0) {
								map.put(IMarker.LINE_NUMBER, 1);
							} else {
								map.put(IMarker.LINE_NUMBER, lineNumber);
							}
						} catch (TDException e) {
							e.printStackTrace();
						}

						try {
							IMarker marker = iFile.createMarker(IMarker.TEXT);
							marker.setAttributes(map);
							marker.setAttribute(IDE.EDITOR_ID_ATTR, "org.eclipse.ui.MarkdownTextEditor");
							IDE.openEditor(iWorkbenchPage, marker, true);
							marker.delete();
						} catch (CoreException e2) {
							e2.printStackTrace();
						}
					}
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