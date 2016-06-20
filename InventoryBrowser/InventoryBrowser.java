package com.vmware.vrack.vrm.core.logical.inventory.service;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.TabExpander;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import org.apache.commons.net.ntp.TimeStamp;

import com.vmware.vim.binding.vmodl.compatible;
import com.vmware.vim.binding.vmodl.data;
import com.vmware.vim.binding.vmodl.set;
import com.vmware.vrack.vrm.core.logical.inventory.entity.LogicalEntity;
import com.vmware.vrack.vrm.core.logical.inventory.entity.PscEntity;
import com.vmware.vrack.vrm.core.logical.inventory.entity.VCenterEntity;

class DataHandler{
	private DefaultTableModel vCenterTable;
    private DefaultTableModel pscTable;
    private LogicalInventoryServiceImpl logicalService;
    private ConfigureMetaDataLocally configureMetaData;
    private Set<VCenterEntity> vCenterEntities;
    private Set<PscEntity> pscEntities;
    private String selectedNodeId=null;
    private VCenterEntity selectedVCenterNode;
    private PscEntity  selectedPscNode;
    private int selectedRow;
    
    public void Configure(){
    	try{
    		ConfigureMetaDataLocally.prepare();
    		configureMetaData= new ConfigureMetaDataLocally();
        	logicalService=configureMetaData.getLogicalService();
        	vCenterEntities=
        			logicalService.getAll(VCenterEntity.class);
        	pscEntities= logicalService.getAll(PscEntity.class);
        	//configureMetaData.after();
    	}catch(Exception exception){
    		System.out.println("Error in preparing connection");
    	}
    }
    
    public void getSelectedObject(String parent){
    	selectedVCenterNode=null;
    	selectedPscNode=null;
    	if(parent.equals("vCenter")){
    		Iterator<VCenterEntity> iter= vCenterEntities.iterator();
    		while(iter.hasNext()){
    			VCenterEntity temp= iter.next();
    			if(temp.getId().toString().equals(selectedNodeId)){
    				selectedVCenterNode=temp;
    				break;
    			}
    		}
    	}else{
    		Iterator<PscEntity> iter= pscEntities.iterator();
    		while(iter.hasNext()){
    			PscEntity temp= iter.next();
    			if(temp.getId().toString().equals(selectedNodeId)){
    				selectedPscNode=temp;
    				break;
    			}
    		}
    	}
    	System.out.println("id: "+selectedNodeId);
    }
   
    public DefaultMutableTreeNode prepareVCenterNodes(){
    	DefaultMutableTreeNode vCenterNode= new DefaultMutableTreeNode("vCenter");
    	if(vCenterEntities!=null && vCenterEntities.size()>0){
    		Iterator<VCenterEntity> iterator= vCenterEntities.iterator();
    	   while(iterator.hasNext()){
    		DefaultMutableTreeNode childNode = new 
    				DefaultMutableTreeNode(iterator.next().getId());
    		vCenterNode.add(childNode);
    	  }
    	}
    	return vCenterNode;
    }
    
    public DefaultMutableTreeNode preparePscNodes(){
    	DefaultMutableTreeNode pscNode= new DefaultMutableTreeNode("psc");
    	if(pscEntities!=null && pscEntities.size()>0){
    		Iterator<PscEntity> iterator= pscEntities.iterator();
    	   while(iterator.hasNext()){
    		DefaultMutableTreeNode childNode = new 
    				DefaultMutableTreeNode(iterator.next().getId());
    		pscNode.add(childNode);
    	  }
    	}
    	return pscNode;
    }
    
    public DefaultTableModel getPscTable(){
    	return this.pscTable;
    }
    
    public DefaultTableModel getVCenterTable(){
    	return this.vCenterTable;
    }
    
    public int getSelectedRow(){
    	return this.getSelectedRow();
    }
    
    public VCenterEntity getSelectedVCenterNode(){
    	return selectedVCenterNode;
    }
    
    public void setSelectedVCenterNode(VCenterEntity vCenterEntity){
    	this.selectedVCenterNode= vCenterEntity;
    }
    public PscEntity getSelectedPscNode(){
    	return selectedPscNode;
    }
    
    public void setSelectedPscNode(PscEntity pscEntity){
    	selectedPscNode= pscEntity;
    }
    
    public String getSelectedNodeId(){
    	return selectedNodeId;
    }
    
    public void setSelectedNodeId(String id){
    	selectedNodeId= id;
    }
    
    
    /*
     * These two methods are not used, but are useful if
     * only fields declared in these classes are present in the data
     * */
    public String[] getPscTableColumNamesByField(){
    	
    	Field [] fields= PscEntity.class.getDeclaredFields();
    	int NoOfFields=fields.length;
    	String []columnNames= new String [NoOfFields];
    	for(int i=0;i<NoOfFields;i++)
    		columnNames[i]= fields[i].getName();
    	return columnNames;
    }
    
    public String[] getVCenterTableColumNamesByField(){
    	
    	Field [] fields= VCenterEntity.class.getDeclaredFields();
    	int NoOfFields=fields.length;
    	String []columnNames= new String [NoOfFields];
    	for(int i=0;i<NoOfFields;i++)
    		columnNames[i]= fields[i].getName();
    	return columnNames;
    }
    
    public String[] getPscTableColumnNames(){
    	String [] columnNames= {"port","domain",
    			"isReplica","createdDate","modifiedDate",
    			"ipAddress","name"};
    	return columnNames;
    }
    
    public String[]  getVCenterTableColumNames() {
    	String [] columnNames={"type", "createdDate",
    			"modifiedDate","ipAddress","name","size"};
    	return columnNames;
	}
    

    public void preparePscTable(){
    	
    	Iterator<PscEntity> iter= pscEntities.iterator();
    	Integer NoOfObjects= pscEntities.size();
    	String [] columnNames= getPscTableColumnNames();
    	
    	List<List<String>> tableData= new ArrayList<>();
    	PscEntity pscEntity;
    	List<String> temp;
  
    	while(iter.hasNext()){
    		pscEntity= iter.next();
    		temp= new ArrayList<String>();
    		
    		String isReplica=null;
    		if(pscEntity.getIsReplica()!=null)
    			isReplica=pscEntity.getIsReplica().toString();
    		
    		temp.add(pscEntity.getPort());
    		temp.add(pscEntity.getDomain());
    		temp.add(isReplica);
    		temp.add(pscEntity.getCreatedDate().toString());
    		temp.add(pscEntity.getModifiedDate().toString());
    		temp.add(pscEntity.getIpAddress());
    		temp.add(pscEntity.getName());
    		tableData.add(temp);
    	}
    	
    	String [][] tableDataArray= new String[NoOfObjects][];
    	for(int i=0;i<NoOfObjects; i++){
    		temp= tableData.get(i);
    		tableDataArray[i]= temp.toArray(new String[temp.size()]);
    	}
    	
    	pscTable= new DefaultTableModel(tableDataArray, columnNames);
    }
    
public void prepareVCenterTable(){
    	
    	Iterator<VCenterEntity> iter= vCenterEntities.iterator();
    	Integer NoOfObjects= vCenterEntities.size();
    	String [] columnNames= getVCenterTableColumNames();
    	List<List<String>> tableData= new ArrayList<>();
    	VCenterEntity vCenterEntity;
    	List<String> temp;
    	while(iter.hasNext()){
    	    vCenterEntity= iter.next();
    	    String type=null;
    	    String size=null;
    	    
    	    if(vCenterEntity.getType()!=null)
    	    	type= vCenterEntity.getType().toString();
    	    if(vCenterEntity.getSize()!=null)
    	    	size= vCenterEntity.getSize().toString();
    	    
    	    temp= new ArrayList<String>();
    	    temp.add(type);
    	    temp.add(vCenterEntity.getCreatedDate().toString());
    	    temp.add(vCenterEntity.getModifiedDate().toString());
    	    temp.add(vCenterEntity.getIpAddress());
    	    temp.add(vCenterEntity.getName());
    	    temp.add(size);
    	    tableData.add(temp);
    	}
    	
    
    	String [][] tableDataArray= new String[NoOfObjects][];
    	for(int i=0;i<NoOfObjects; i++){
    		temp= tableData.get(i);
    		tableDataArray[i]= temp.toArray(new String[temp.size()]);
    	}
    	
    	vCenterTable= new DefaultTableModel(tableDataArray, columnNames);
    }
    
    public void saveNode(){
    	if(selectedPscNode!=null)
    		logicalService.save(selectedPscNode);
    	else logicalService.save(selectedVCenterNode);
    }
    
    public void deleteNode () {
    	if(selectedPscNode!=null)
    		logicalService.deleteById(
    				PscEntity.class, selectedPscNode.getId());
    	else
    		logicalService.deleteById(VCenterEntity.class, selectedVCenterNode.getId());
	}
    
}

public class InventoryBrowser extends JFrame {
	private JTree tree;
    private DefaultMutableTreeNode root;
    private JPanel panel1;
    private JPanel panel2;
    private File rootNode;
    private JTable table;
    private DataHandler dataHandler;
    private int selectedRow;
    /*
    * used to clear the table whenever some node doesn't have
    * any data associated with it. Saves creating objects
    * repeatedly
    * */
    private DefaultTableModel emptyTable;

    /*
    * Recursively adds files and directories to child directories
    * and in turn adds child directories and file to the root
    * */
    public void createChildren(File rootF,
                               DefaultMutableTreeNode parentNode){
        File [] files= rootF.listFiles();
        if(files==null)
            return;

        for (File file: files){
            DefaultMutableTreeNode child=
                    new DefaultMutableTreeNode(file.getName());
            parentNode.add(child);
            if(file.isDirectory())
                createChildren(file, child);
        }
    }

    /*
    * Splits the window in two parts,
    * one to show the treeView and another to
    * show the table view
    * */
    public void splitWindow(){
        tree.setShowsRootHandles(true);
        JSplitPane splitPane= new
                JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                panel1.add(new JScrollPane(tree)),
                panel2.add(new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED)));
        splitPane.setDividerSize(3);
        splitPane.setDividerLocation(150);
        getContentPane().add(splitPane);
    }
    
    public InventoryBrowser(){
    	
    }
    
   public  InventoryBrowser(String rootN){

        //this.rootNode= new File(rootNode);
        root= new DefaultMutableTreeNode(
                rootN);
        tree= new JTree(root);
        tree.setShowsRootHandles(true);
        tree.addTreeSelectionListener(createEvent());
        tree.setCellRenderer(setIcons());
        tree.addMouseListener(showPopupOnRihgtClick());
        panel1= new JPanel();
        panel2= new JPanel();
        table= new JTable();
        emptyTable= new DefaultTableModel();
        dataHandler = new DataHandler();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setVisible(true);
        setResizable(true);
        setTitle("InventoryServices");
    }
   
   private ActionListener deleteAction(){
	   return new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent event) {
			// TODO Auto-generated method stub
			dataHandler.deleteNode();
		}
	};
   }
   
   private ActionListener saveAction(){
	   return new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent event) {
			// TODO Auto-generated method stub
			dataHandler.saveNode();
		}
	};
   }
   
   private ActionListener refreshAction(){
	   return new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent event) {
			// TODO Auto-generated method stub
			dataHandler.Configure();
			root.add(dataHandler.prepareVCenterNodes());
			root.add(dataHandler.preparePscNodes());
		}
	};
   }
   private MouseListener showPopupOnRihgtClick(){
	   return new MouseListener() {
		
		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			if(SwingUtilities.isRightMouseButton(e)){
				TreePath path= tree.getPathForLocation(e.getX(), e.getY());
				Rectangle pathBounds= tree.getUI().getPathBounds(tree, path);
				JMenuItem refresh= new JMenuItem("Refresh");
				JMenuItem delete= new JMenuItem("Delete");
				JMenuItem save = new JMenuItem("Save");
				
				delete.addActionListener(deleteAction());
				save.addActionListener(saveAction());
				refresh.addActionListener(refreshAction());
				
				DefaultMutableTreeNode rightClickedNode= (DefaultMutableTreeNode)
						path.getLastPathComponent();
				
				if(pathBounds!=null && pathBounds.contains(e.getX(), e.getY()) && rightClickedNode.isLeaf()){
					JPopupMenu popup= new JPopupMenu();
					popup.add(save);
					popup.add(delete);
					popup.add(refresh);
					popup.show(tree, pathBounds.x, pathBounds.y+pathBounds.height);
				}
			}
		}
		
		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
	};
   }
   
   private DefaultTreeCellRenderer setIcons(){
	   
	   return new DefaultTreeCellRenderer(){
		   @Override
		   public Component getTreeCellRendererComponent(JTree tree, Object value,
				   boolean selected, boolean expanded, boolean leaf, 
				   int row, boolean hasFocus ){
			   super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
				   final ImageIcon closedIcon=
						   new ImageIcon("C:\\Users\\abhisheky\\Pictures\\folder_folder.png");
				   
				   final ImageIcon homeFolder= new ImageIcon(
						   "C:\\Users\\abhisheky\\Pictures\\folder_home.png");
				   
				   final ImageIcon selectedIcon= new ImageIcon
						   ("C:\\Users\\abhisheky\\Pictures\\orange_folder.png");
				   
				   final ImageIcon expandedIcon= new ImageIcon(
						   "C:\\Users\\abhisheky\\Pictures\\folder_expanded.png");
			
				   final ImageIcon leafIcon=
						   new ImageIcon("C:\\Users\\abhisheky\\Pictures\\drought_leaf.png");
				  
				   String rootVal=value.toString();
				   if(rootVal.equals("LogicalResources")){
					   setIcon(homeFolder);
					   setName("LogicalResources");
				   }else if(!leaf && !rootVal.equals("LogicalResources")){
					   setIcon(closedIcon);
					   if(selected)
						   setIcon(selectedIcon);
					   if(expanded)
						   setIcon(expandedIcon);
						   
				   }else if(leaf)
					   setIcon(leafIcon);
				   else
					   setIcon(getDefaultClosedIcon());
			   return this;
		   }
	   };
   }
   
    private void setTableProperties(){
        table.setRowHeight(20);
        table.getTableHeader().setFont(new Font("SansSerif",Font.BOLD,13));
    }
    
    
    private TreeSelectionListener createEvent(){
        return new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode node=
                        (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                Object object ;
                if(node!=null) {
                	Object parent=node.getParent();
                	String selectedNode = node.getUserObject().toString();
                    if(parent==null)
                    	table.setModel(emptyTable);
                    else if(selectedNode.equals("psc") || 
                    		(parent!=null && parent.toString().equals("psc"))){
                        table.setModel(dataHandler.getPscTable());
                        //if(!selectedNode.equals("psc")){
                        	dataHandler.setSelectedNodeId(selectedNode);
                        	dataHandler.getSelectedObject(parent.toString());
                        	highlightCorrespondingRow();
                        //}
                    }
                    else if(selectedNode.equals("vCenter") || node.getParent().toString().equals("vCenter")){
                        table.setModel(dataHandler.getVCenterTable());
                        //if(!selectedNode.equals("vCenter")){
                        	dataHandler.setSelectedNodeId(selectedNode);
                        	dataHandler.getSelectedObject(parent.toString());
                        	highlightCorrespondingRow();
                        
                    }
                    else 
                    	table.setModel(emptyTable);
                    setTableProperties();
                  }
            }
        };
    }
    
    public int getRowIndex(String selectedCreatedDate, int columnIndex){
    	int rowIndex=-1;
    	
		for(int i=0 ; i<table.getRowCount();i++){
			if(table.getValueAt(i, columnIndex).
					equals(selectedCreatedDate)){
				rowIndex=i;
				break;
			}
		}
		return rowIndex;
    }
    
    
    public void highlightCorrespondingRow(){
    	int columnIndex=0;
    	selectedRow=-1;
    	String selectedCreatedDate=null;
    	columnIndex= table.getColumnModel().getColumnIndex("createdDate");
    	PscEntity selectedPscNode=dataHandler.getSelectedPscNode();
    	VCenterEntity selectedVCenterNode= dataHandler.getSelectedVCenterNode();
    	
    	if(dataHandler.getSelectedPscNode()!=null){
    		selectedCreatedDate= selectedPscNode.getCreatedDate().toString();
    		selectedRow= getRowIndex(selectedCreatedDate, columnIndex);
    	}else if(selectedVCenterNode!=null){
    		selectedCreatedDate= selectedVCenterNode.getCreatedDate().toString();
    		selectedRow= getRowIndex(selectedCreatedDate, columnIndex);
    	}
    	//System.out.println("row: "+selectedRow+"  "+"column: "+ columnIndex+" selectedCreatedDate: "+selectedCreatedDate);
    	
    	table.setDefaultRenderer(Object.class, highlight(table));
    }
    
    public DefaultTableCellRenderer highlight(JTable table){
    	return new DefaultTableCellRenderer(){
    		@Override
    		public Component getTableCellRendererComponent(JTable table, Object value,
    				boolean isSelected, boolean hasFocus, int row, int column){
    			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    			if(row==selectedRow){
    				setBackground(Color.LIGHT_GRAY);
    				setForeground(Color.WHITE);
    			}else{
    				setBackground(table.getBackground());
    				setForeground(table.getForeground());
    			}
    	       return this;
    		}
    	};
    }
    
 public void showUI(){
	 splitWindow();
	 dataHandler.Configure();
	 dataHandler.preparePscTable();
	 dataHandler.prepareVCenterTable();
	 root.add(dataHandler.prepareVCenterNodes());
	 root.add(dataHandler.preparePscNodes());
	 
  }
    public static void main(String [] args){
  
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                InventoryBrowser fileBrowser = new InventoryBrowser("LogicalResources");
                fileBrowser.showUI();
            }
        });
        
    }
}
