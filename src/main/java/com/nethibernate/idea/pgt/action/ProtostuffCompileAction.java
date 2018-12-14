package com.nethibernate.idea.pgt.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.nethibernate.idea.pgt.service.ConfigurationPersistentService;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author nethibernate
 */
public class ProtostuffCompileAction extends AnAction {
	
	public ProtostuffCompileAction() {
		super("Protostuff Compile");
	}
	
	@Override
	public void actionPerformed(AnActionEvent e) {
		
		String externalToolPath = ConfigurationPersistentService.getInstance().getConfig().getExternalToolPath();
		if (externalToolPath == null || externalToolPath.isEmpty()) {
			Messages.showErrorDialog("You must set your protostuff compiler!", "Oops!");
			return;
		}
		
		Project project = e.getProject();
		VirtualFile dir = PlatformDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
		if (dir == null) {
			Messages.showErrorDialog("The file is not found", "Error");
			return;
		}
		if (!dir.isDirectory()) {
			Messages.showErrorDialog("You must use it on a directory!", "Oops!");
			return;
		}
		
		//Get module
		Module module = ModuleUtil.findModuleForFile(dir, project);
		//Get module dir path
		String moduleDirPath = ModuleUtil.getModuleDirPath(module);
		String outputPath = moduleDirPath + "/" + ConfigurationPersistentService.getInstance().getConfig().getOutputPath();
		
		//first delete output path
		Path directory = Paths.get(outputPath);
		try {
			Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}
				
				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e1) {
			Messages.showErrorDialog(project, "Old generated files cannot be deleted", "Failed");
			return;
		}
		
		StringBuilder commandSb = new StringBuilder();
		commandSb.append("java -jar ")
				.append(externalToolPath)
				.append(" -g java -o ")
				.append(outputPath)
				.append(" -I ")
				.append(dir.getPath())
				.append(" ");
		
		if (handleDir(dir, commandSb.toString(), project)) {
			Messages.showInfoMessage(project, "Success", "Done");
		} else {
			Messages.showErrorDialog(project, "Not all proto files have been compiled!", "Failed");
		}
		//refresh whole project
		project.getBaseDir().refresh(false, true);
	}
	
	@Override
	public void update(AnActionEvent e) {
		VirtualFile dir = PlatformDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
		if (dir != null && dir.isDirectory()) e.getPresentation().setVisible(true);
		else e.getPresentation().setVisible(false);
	}
	
	public boolean handleDir(VirtualFile dir, String commandPrefix, Project project) {
		if (dir == null || !dir.isDirectory() || !dir.exists()) return true;
		//Loop whole files in the dir
		for (VirtualFile child : dir.getChildren()) {
			if (child.isDirectory()) {
				if (!handleDir(child, commandPrefix, project)) {
					return false;
				}
				continue;
			}
			
			if (!child.getExtension().equals("proto")) continue;
			
			try {
				Process process = Runtime.getRuntime().exec(commandPrefix + child.getPath());
				int exitValue = process.waitFor();
				if (exitValue != 0) {
					Messages.showErrorDialog(project, "File " + child.getName() + " compile failed!", "Compile Failed");
					return false;
				}
			} catch (IOException e1) {
				e1.printStackTrace();
				Messages.showErrorDialog(e1.getMessage() + " " + child.getName(), "Exception");
				return false;
			} catch (InterruptedException e1) {
				e1.printStackTrace();
				Messages.showErrorDialog(e1.getMessage() + " " + child.getName(), "Exception");
				return false;
			}
		}
		
		return true;
	}
}
