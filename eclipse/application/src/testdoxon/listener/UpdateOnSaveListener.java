/**
Copyright 2018 Delicate Sound Of Software AB, All Rights Reserved

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package testdoxon.listener;

import java.io.File;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.ViewPart;

import testdoxon.model.TDFile;
import testdoxon.views.View;

public class UpdateOnSaveListener implements IResourceChangeListener {
	private TableViewer viewer;
	private ViewPart view;

	public UpdateOnSaveListener(TableViewer viewer, ViewPart view) {
		this.viewer = viewer;
		this.view = view;
	}

	@Override
	public void resourceChanged(IResourceChangeEvent arg0) {
		// TODO Auto-generated method stub
		IResourceDelta resourceDelta = arg0.getDelta();
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

	private void updateTable() {
		IEditorPart iEditorPart = view.getSite().getWorkbenchWindow().getActivePage().getActivePart().getSite()
				.getPage().getActiveEditor();
		
		if (iEditorPart != null && (iEditorPart.getTitle().matches("^Test.*.java") || iEditorPart.getTitle().matches(".*Test.java"))) {
			File file = iEditorPart.getEditorInput().getAdapter(File.class);
			if (file != null) {
				// Always update on save
				View.currentOpenFile = new TDFile(file);
				View.currentTestFile = View.currentOpenFile;
				View.currentTestFile.setHeaderFilepath(View.currentOpenFile.getAbsolutePath());

				if (View.currentTestFile != null && this.viewer != null) {
					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
							try {
								viewer.setInput(View.currentTestFile);
							} catch (AssertionFailedException e) {
								// Do nothing
							}
						}
					});
				}
			}
		}
	}

}
