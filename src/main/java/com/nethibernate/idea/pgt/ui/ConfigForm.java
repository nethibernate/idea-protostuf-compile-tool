package com.nethibernate.idea.pgt.ui;

import com.nethibernate.idea.pgt.service.ConfigurationPersistentService;

import javax.swing.*;

/**
 * @author nethibernate
 */
public class ConfigForm {
    private JPanel root;
    private JTextField externalToolPathTextField;
    private JTextField outputPathTextField;
    
    private final ConfigurationPersistentService persistent = ConfigurationPersistentService.getInstance();

    public JComponent getRootPanel() {
        externalToolPathTextField.setText(persistent.getConfig().getExternalToolPath());
        outputPathTextField.setText(persistent.getConfig().getOutputPath());
        return root;
    }

    public JTextField getExternalToolPathTextField() {
        return externalToolPathTextField;
    }
    
    public String getExternalToolPath(){
    	return externalToolPathTextField.getText();
    }
	
	public JTextField getOutputPathTextField() {
		return outputPathTextField;
	}
	
	public String getOutputPath(){
    	return outputPathTextField.getText();
	}
}
