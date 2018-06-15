package testdoxon.views;

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
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;

import testdoxon.exceptionHandlers.TDException;
import testdoxon.handlers.FileHandler;
import testdoxon.utils.DoxonUtils;


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

@SuppressWarnings("deprecation")
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

	// Listeners
	private IResourceChangeListener saveFileListener;
	private CaretListener wordListener;
	private ISelectionListener fileSelected;
	private IPartListener fileChanged;

	/*
	 * The content provider class is responsible for providing objects to the view.
	 * It can wrap existing objects in adapters or simply return objects as-is.
	 * These objects may be sensitive to the current input of the view, or ignore it
	 * and always show the same content (like Task List, for example).
	 */

	class ViewContentProvider implements IStructuredContentProvider {
		private String filePath = "";
		private String fileName = "";

		/**
		 * Checks the current files name and package name
		 * @param v
		 * @param oldInput
		 * @param newInput
		 */
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

		/**
		 * Updates the header with the current testfile name and package name. 
		 * If file is not found return is "file not found"
		 * @param parent
		 * @return String[]
		 */
		public Object[] getElements(Object parent) {
			try {
				header.setText(fileName);
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
		this.initiateListeners();
	}

	private void initiateListeners() {
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

		this.wordListener = null;

		this.fileSelected = new ISelectionListener() {

			@Override
			public void selectionChanged(IWorkbenchPart arg0, ISelection arg1) {
				// TODO Auto-generated method stub
				if (arg0.getTitle().matches(".*\\.java")) {

					File file = (File) arg0.getSite().getPage().getActiveEditor().getEditorInput()
							.getAdapter(File.class);

					if (currentFile == null || !file.getAbsolutePath().equals(currentFile.getAbsolutePath())) {
						currentFile = file;
						findFileToOpen();
					}

				}
			}
		};

		this.fileChanged = new IPartListener() {

			@Override
			public void partOpened(IWorkbenchPart arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void partDeactivated(IWorkbenchPart arg0) {
				// TODO Auto-generated method stub
				if (wordListener != null) {
					IEditorPart iep = arg0.getSite().getPage().getActiveEditor();
					if (iep != null) {
						AbstractTextEditor e = (AbstractTextEditor) iep;
						Control adapter = e.getAdapter(Control.class);

						if (adapter instanceof StyledText) {
							StyledText text = (StyledText) adapter;
							text.removeCaretListener(wordListener);
						}
					}
				}
			}

			@Override
			public void partClosed(IWorkbenchPart arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void partBroughtToTop(IWorkbenchPart arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void partActivated(IWorkbenchPart arg0) {
				// TODO Auto-generated method stub
				IEditorPart iep = arg0.getSite().getPage().getActiveEditor();
				if (iep != null) {
					AbstractTextEditor e = (AbstractTextEditor) iep;
					Control adapter = e.getAdapter(Control.class);

					if (adapter instanceof StyledText) {
						StyledText text = (StyledText) adapter;

						wordListener = new CaretListener() {

							@Override
							public void caretMoved(CaretEvent arg0) {
								// TODO Auto-generated method stub
								String word = getWordUnderCaret(arg0.caretOffset, text);

								if (word.length() > 0 && Character.isUpperCase(word.charAt(0))) {
									DoxonUtils doxonUtils = new DoxonUtils();
									String fileToLookFor = "Test" + word + ".java";
									String newTestFileath = doxonUtils.createTestPath(currentFile) + fileToLookFor;

									if (!newTestFileath.equals(currentTestFile.getAbsolutePath())) {
										currentTestFile = new File(newTestFileath);

										if (currentTestFile != null) {
											Display.getDefault().syncExec(new Runnable() {
												@Override
												public void run() {
													viewer.setInput(currentTestFile);
												}
											});
										}
									}
								} else {
									// Show current classes test class.
									findFileToOpen();
								}
							}

						};

						text.addCaretListener(wordListener);
					}
				}
			}
		};
	}
	
	private void findFileToOpen() {
		if (currentFile.getName().matches("^Test.*")) {
			if (currentTestFile == null || !currentTestFile.getAbsolutePath().equals(currentFile.getAbsolutePath())) {
				currentTestFile = currentFile;
			}
		} else {
			DoxonUtils doxonUtils = new DoxonUtils();
			String newTestFilepath = doxonUtils.createTestPath(currentFile) + "Test" + currentFile.getName();

			if (currentTestFile == null || !newTestFilepath.equals(currentTestFile.getAbsolutePath())) {
				currentTestFile = new File(newTestFilepath);
			}
		}
		
		if (currentTestFile != null) {
			Display.getDefault().syncExec(new Runnable() {
				@Override
				public void run() {
					viewer.setInput(currentTestFile);
				}
			});
		}
	}

	private void updateTable() {
		IEditorPart iEditorPart = this.getSite().getWorkbenchWindow().getActivePage().getActivePart().getSite()
				.getPage().getActiveEditor();

		if (iEditorPart != null) {
			File file = iEditorPart.getEditorInput().getAdapter(File.class);

			if (file != null) {
				// Always update on save
				currentFile = file;
				currentTestFile = currentFile;
				if (currentTestFile != null && viewer != null) {
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

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		GridLayout gl = new GridLayout(1, false);
		gl.marginLeft = 10;
		gl.marginHeight = 10;
		gl.marginRight = 0;
		gl.marginBottom = 0;
		gl.verticalSpacing = 15;

		parent.setLayout(gl);
		parent.setBackground(widgetColor);

		this.populatePlugin(parent);

		// Adding listeners
		ResourcesPlugin.getWorkspace().addResourceChangeListener(saveFileListener, IResourceChangeEvent.POST_BUILD);
		ISelectionService iSelectionService = this.getSite().getWorkbenchWindow().getSelectionService();
		iSelectionService.addPostSelectionListener(fileSelected);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().addPartListener(fileChanged);

		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "TestDoxon.viewer");
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
	}

	private void populatePlugin(Composite parent) {
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
	}

	private String getWordUnderCaret(int pos, StyledText text) {
		int lineOffset = pos - text.getOffsetAtLine(text.getLineAtOffset(pos));
		String line = text.getLine(text.getLineAtOffset(pos));
		String[] words = line.split("[ \t\\\\(\\\\);\\\\.{}]");
		String desiredWord = "";

		for (String word : words) {
			if (lineOffset <= word.length()) {
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
							System.out.println("LineNumber: " + lineNumber);
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

						@SuppressWarnings("rawtypes")
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

	@SuppressWarnings("unused")
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