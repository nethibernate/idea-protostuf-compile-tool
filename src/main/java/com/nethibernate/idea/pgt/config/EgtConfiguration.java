package com.nethibernate.idea.pgt.config;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.nethibernate.idea.pgt.service.ConfigurationPersistentService;
import com.nethibernate.idea.pgt.ui.ConfigForm;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author nethibernate
 */
public class EgtConfiguration implements Configurable {
	
	private ConfigForm configForm;
	
	private ConfigurationPersistentService persistent = ConfigurationPersistentService.getInstance();
	
	
	@Nls(capitalization = Nls.Capitalization.Title)
	@Override
	public String getDisplayName() {
		return "External Generate Tool";
	}
	
	@Nullable
	@Override
	public JComponent createComponent() {
		configForm = new ConfigForm();
		return configForm.getRootPanel();
	}
	
	@Override
	public boolean isModified() {
		return !configForm.getExternalToolPath().equals(persistent.getConfig().getExternalToolPath()) || !configForm.getOutputPath().equals(persistent.getConfig().getOutputPath());
	}
	
	@Override
	public void apply() throws ConfigurationException {
		persistent.getConfig().setExternalToolPath(configForm.getExternalToolPath());
		persistent.getConfig().setOutputPath(configForm.getOutputPath());
	}
	
	@Override
	public void reset() {
		configForm.getExternalToolPathTextField().setText(persistent.getConfig().getExternalToolPath());
		configForm.getOutputPathTextField().setText(persistent.getConfig().getOutputPath());
	}
	
	@Nullable
	@Override
	public String getHelpTopic() {
		return "Configuration for EGT";
	}
	
}
