package testdoxon.gui;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

import testdoxon.model.TDTableItem;

public class MethodLabelProvider extends LabelProvider implements ITableLabelProvider {
	public final Image redDot;
	public final Image greenDot;
	public final Image grayDot;
	public final Image blueDot;
	public final Image yellowDot;
	
	public MethodLabelProvider () {
		Bundle bundle = Platform.getBundle("TestDoxon");
		URL url = null;
		ImageDescriptor imageDesc = null;
		
		url = FileLocator.find(bundle, new Path("icons/testDox-red8px.png"), null);
		imageDesc = ImageDescriptor.createFromURL(url);
		this.redDot = imageDesc.createImage();
		
		url = FileLocator.find(bundle, new Path("icons/testDox-green8px.png"), null);
		imageDesc = ImageDescriptor.createFromURL(url);
		this.greenDot = imageDesc.createImage();
		
		url = FileLocator.find(bundle, new Path("icons/testDox-gray8px.png"), null);
		imageDesc = ImageDescriptor.createFromURL(url);
		this.grayDot = imageDesc.createImage();
		
		url = FileLocator.find(bundle, new Path("icons/testDox-blue8px.png"), null);
		imageDesc = ImageDescriptor.createFromURL(url);
		this.blueDot = imageDesc.createImage();
		
		url = FileLocator.find(bundle, new Path("icons/testDox-yellow8px.png"), null);
		imageDesc = ImageDescriptor.createFromURL(url);
		this.yellowDot = imageDesc.createImage();
	}
	
	public String getColumnText(Object obj, int index) {
		if (obj instanceof TDTableItem) {
			TDTableItem _tmp = (TDTableItem) obj;
			return _tmp.getMethodName(); 
		}
		return getText(obj);
	}

	public Image getColumnImage(Object obj, int index) {
		return getImage(obj);
	}

	public Image getImage(Object obj) {
		if(obj instanceof TDTableItem) {
			TDTableItem _tmp = (TDTableItem) obj;
			
			switch(_tmp.getPictureIndex()) {
				case TDTableItem.NONE:
					return redDot;
				case TDTableItem.TEST:
					return greenDot;
				case TDTableItem.IGNORE:
					return grayDot;
				case TDTableItem.BOTH_TEST_IGNORE:
					return yellowDot;
				default:
					return blueDot;
			}
		}
		
		return blueDot;
	}
}
