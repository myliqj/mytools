package org.my.tools.util;

import javax.swing.JOptionPane;

public class MySysUtils {
	
	public static void showMessageBox(String mess){ 
		JOptionPane.showMessageDialog(null,
				mess, "系统信息", JOptionPane.INFORMATION_MESSAGE);
	}
	public static int showMessageConfirmBox(String mess){ 
		return JOptionPane.showConfirmDialog(null,
				mess, "确认信息", JOptionPane.YES_NO_OPTION);
	}
}
