package com.nethibernate.idea.pgt.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.nethibernate.idea.pgt.service.ConfigurationPersistentService;

import java.io.IOException;

/**
 * @author nethibernate
 */
public class ProtostuffCompileAction extends AnAction {
	
	public ProtostuffCompileAction() {
		super("Protostuff Compile");
	}
	
	@Override
	public void actionPerformed(AnActionEvent e) {
		
		Project project = e.getProject();
		VirtualFile dir = PlatformDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
		if (dir == null) {
			Messages.showErrorDialog("The file is not found", "Error");
			return;
		}
		if(!dir.isDirectory()){
			Messages.showErrorDialog("You must use it on a directory!", "Oops!");
			return;
		}
		
		//Get module
		Module module = ModuleUtil.findModuleForFile(dir, project);
		//Get module dir path
		String moduleDirPath = ModuleUtil.getModuleDirPath(module);
		
		StringBuilder commandSb = new StringBuilder();
		commandSb.append("java -jar ")
				.append(ConfigurationPersistentService.getInstance().getConfig().getExternalToolPath())
				.append(" -g java -o ")
				.append(moduleDirPath)
				.append("/").append(ConfigurationPersistentService.getInstance().getConfig().getOutputPath())
				.append(" -I ")
				.append(dir.getPath())
				.append(" ");
		
		handleDir(dir, commandSb.toString(), project);
		Messages.showInfoMessage(project, "success", "Compile");
		//refresh whole project
		project.getBaseDir().refresh(false, true);
	}
	
	@Override
	public void update(AnActionEvent e) {
		VirtualFile dir = PlatformDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
		if(dir != null && dir.isDirectory()) e.getPresentation().setVisible(true);
		else e.getPresentation().setVisible(false);
	}
	
	public void handleDir(VirtualFile dir, String commandPrefix, Project project){
		if(dir == null || !dir.isDirectory() || !dir.exists()) return;
		//Loop whole files in the dir
		for (VirtualFile child : dir.getChildren()) {
			if(child.isDirectory()) handleDir(child, commandPrefix, project);
			
			if(!child.getExtension().equals("proto")) continue;
			
			try {
				Process process = Runtime.getRuntime().exec(commandPrefix + child.getPath());
				int exitValue = process.waitFor();
				if (exitValue != 0) {
					Messages.showErrorDialog(project, "error exit " + exitValue, "Compile Failed");
				}
			} catch (IOException e1) {
				e1.printStackTrace();
				Messages.showErrorDialog(e1.getMessage() + " " + commandPrefix, "exception");
			} catch (InterruptedException e1) {
				e1.printStackTrace();
				Messages.showErrorDialog(e1.getMessage() + " " + commandPrefix, "exception");
			}
		}
	}
}
