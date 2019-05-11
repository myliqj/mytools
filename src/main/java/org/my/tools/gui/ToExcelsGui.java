package org.my.tools.gui;

import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JTextField;

import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.my.tools.util.MyFileUtils;
import org.my.tools.util.MySysUtils;

public class ToExcelsGui {

	private JFrame frame;
	private JTable table;
	private JScrollPane table_jsp;
	private JTextField txtOutFileName;
	private JButton btnNewButton;
	private JTabbedPane tabbedPane;
	private JPanel pnl_sql;
	private JTextArea textSql;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ToExcelsGui window = new ToExcelsGui();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public ToExcelsGui() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("�������ݵ�Excel�ļ��У�ʹ��ģ���ָ��ʱ��������");
		frame.setBounds(100, 100, 550, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 2, 2));
		
		
		JLabel lblNewLabel = new JLabel("�����");
		//lblNewLabel.setBounds(154, 8, 20, 15);
		panel.add(lblNewLabel);
		
		txtOutFileName = new JTextField();
		panel.add(txtOutFileName);
		txtOutFileName.setColumns(30);
		
		btnNewButton = new JButton("����");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int row = table.getRowCount();
			    Map<String,String> m = new HashMap<String,String>(); 
		    	for (int i=0; i<row; i++){
		    		m.put(String.valueOf(table.getValueAt(i,0)),String.valueOf(table.getValueAt(i,2)));
		    	}
		    	m.put("sql", textSql.getText());
		    	String out_file = txtOutFileName.getText();
		    	m.put("out_file", out_file);
		        if (MyFileUtils.isExists_file(out_file) ){
		           int sel = MySysUtils.showMessageConfirmBox("����ļ��Ѵ��ڣ��Ƿ񸲸����ɴ��ļ���");
		           if(sel == 1) return;
		        }
		    	
		    	System.out.println(m);
		    	long start = System.nanoTime(); 
		    	try {
		    		ToExcels.getExcels().setValue(m);
		    		//MySysUtils.showMessageBox(ToExcels.driver);
		    		int rows = ToExcels.getExcels().OutputExcels();

		    		long ys = (System.nanoTime() - start)/1000000;
		    		ys = ys / 1000;
			    	
		    		MySysUtils.showMessageBox(String.format("�����ɹ�! �ܼ�¼����= %d ����ʱ= %d ��\n����ļ�= %s"
		    		   ,rows,ys,txtOutFileName.getText()));
		    		
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					
					String msg = e1.getMessage();
					if (msg!=null && msg.contains("already written to disk")){
						msg = "����ʧ�ܣ���ȷ�� ģ���ļ� д���ݿ�ʼ��Ϊ�գ� ����" + msg;
					}else{
						msg = "����ʧ�ܣ�����"+msg;
					}
					
					MySysUtils.showMessageBox(msg);
				}
			}
		});
		panel.add(btnNewButton);
		
		//frame.getContentPane().add(table_jsp, BorderLayout.CENTER);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
	    frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);

	    textSql = new JTextArea();
	   

		Vector<String> cols = new Vector<String>();
		cols.add("name");cols.add("������");cols.add("����ֵ");
		Vector<Vector<String>> data = new Vector<Vector<String>>();
		System.out.println("set-sql1:"+ToExcels.getExcels().sql);
		for (String[] val : ToExcels.getExcels().getValue()) {
			if("sql".equals(val[0])){
				System.out.println("set-sql:" + val[2]);
				textSql.setText(val[2]);
			}else if("out_file".equals(val[0])){
				txtOutFileName.setText(val[2]);
			}else{
				Vector<String> row = new Vector<String>();
				row.add(val[0]);row.add(val[1]); row.add(val[2]);
				data.add(row);
			}
		}
		
		DefaultTableModel dtm = new DefaultTableModel(data, cols){
			public boolean isCellEditable(int row,int column){  
				   if(column ==2){  
				       return true;  
				   }else{  
				       return false;  
				   }  
				}  
			};
		
		table = new JTable();
		table_jsp = new JScrollPane(table);
		tabbedPane.addTab("����", null, table_jsp, "����");
		table.setModel(dtm);

		DefaultTableColumnModel dcm = (DefaultTableColumnModel)table .getColumnModel();//��ȡ��ģ��  
	    dcm.getColumn(0).setMinWidth(0);  //����һ�е���С��ȡ�����ȶ�����Ϊ0���Ϳ�������
	    dcm.getColumn(0).setMaxWidth(0);
	    dcm.getColumn(1).setMinWidth(130);
	    dcm.getColumn(1).setMaxWidth(390);
	     
	    

	    pnl_sql = new JPanel();
	    tabbedPane.addTab("SQL���", null, pnl_sql, "ִ�е�SQL���");
	    pnl_sql.setLayout(new BorderLayout(0, 0));
	    
	    JScrollPane jsp_1 = new JScrollPane();
	    jsp_1.setViewportView(textSql);
	    pnl_sql.add(jsp_1); 
	    
	    
	}

	 
}
