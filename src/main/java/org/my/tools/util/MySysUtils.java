package org.my.tools.util;

import javax.swing.JOptionPane;

public class MySysUtils {
	
	public static void showMessageBox(String mess){ 
		JOptionPane.showMessageDialog(null,
				mess, "ϵͳ��Ϣ", JOptionPane.INFORMATION_MESSAGE);
	}
	public static int showMessageConfirmBox(String mess){ 
		return JOptionPane.showConfirmDialog(null,
				mess, "ȷ����Ϣ", JOptionPane.YES_NO_OPTION);
	}
}
