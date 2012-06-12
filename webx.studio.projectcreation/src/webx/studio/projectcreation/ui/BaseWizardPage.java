/**
 * Project: webx3.projectcreation
 *
 * File Created at 2011-4-27
 * $Id: BaseWizardPage.java 112231 2011-09-16 05:57:39Z zhihua.hanzh $
 *
 * Copyright 1999-2100 Alibaba.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
 */
package webx.studio.projectcreation.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import com.alibaba.antx.util.FileUtil;

/**
 * TODO Comment of BaseWizardPage
 *
 * @author zhihua.hanzh
 */
public abstract class BaseWizardPage extends WizardPage {

    protected final Map<String, Control> nameControlMap  = new HashMap<String, Control>();

    protected static final int           MAX_HISTORY     = 15;
    protected IDialogSettings            dialogSettings;
    private Map<String, List<Combo>>     fieldsWithHistory;
//    private boolean                      isHistoryLoaded = false;

    protected Map<String, Control> getNameControlMap() {
        return nameControlMap;
    }

    protected String getControlVaule(String key) {
        if (nameControlMap == null || nameControlMap.size() == 0)
            return "";
        Control control = nameControlMap.get(key);
        if (control == null)
            return null;
        Object object = getControlValue(control);
        if (object == null)
            return "";
        return object.toString();
    }

    protected Object getControlValue(final Control control) {

        if (control == null)
            return null;
        if (control instanceof Button) {
            return ((Button) control).getSelection();
        } else if (control instanceof Text) {
            return ((Text) control).getText();
        } else if (control instanceof Combo) {
            return ((Combo) control).getText();
        }
        return null;

    }

    public BaseWizardPage(String pageName) {
        super(pageName);
        fieldsWithHistory = new HashMap<String, List<Combo>>();
        initDialogSettings();
    }

//    public void setVisible(boolean visible) {
//        if (visible) {
//            if (!isHistoryLoaded) {
////                loadInputHistory();
//                isHistoryLoaded = true;
//            } else {
//                saveInputHistory();
//            }
//
//        }
//
//        super.setVisible(visible);
//
//    }

    public void dispose() {
        saveInputHistory();
        super.dispose();
    }

    private void initDialogSettings() {
        IDialogSettings pluginSettings;
        if (ProjectCreationPlugin.getDefault() == null) {
            pluginSettings = new DialogSettings("Workbench");
        } else {
            pluginSettings = ProjectCreationPlugin.getDefault().getDialogSettings();
        }
        dialogSettings = pluginSettings.getSection(getName());
        if (dialogSettings == null) {
            dialogSettings = pluginSettings.addNewSection(getName());
            pluginSettings.addSection(dialogSettings);
        }
    }

    protected void loadInputHistory() {
        for (Map.Entry<String, List<Combo>> e : fieldsWithHistory.entrySet()) {
            String id = e.getKey();
            String[] items = dialogSettings.getArray(id);
            if (items != null) {
                for (Combo combo : e.getValue()) {
                    String text = combo.getText();
                    combo.setItems(items);
                    if (text.length() > 0) {
                        combo.setText(text);
                    }
                }
            }
        }
    }

    private void saveInputHistory() {
        for (Map.Entry<String, List<Combo>> e : fieldsWithHistory.entrySet()) {
            String id = e.getKey();

            Set<String> history = new LinkedHashSet<String>(MAX_HISTORY);

            for (Combo combo : e.getValue()) {
                String lastValue = combo.getText();
                if (lastValue != null && lastValue.trim().length() > 0) {
                    history.add(lastValue);
                }
            }

            Combo combo = e.getValue().iterator().next();
            String[] items = combo.getItems();
            for (int j = 0; j < items.length && history.size() < MAX_HISTORY; j++) {
                history.add(items[j]);
            }

            dialogSettings.put(id, history.toArray(new String[history.size()]));
        }
    }

    protected void addFieldWithHistory(String id, Combo combo) {
        if (combo != null) {
            List<Combo> combos = fieldsWithHistory.get(id);
            if (combos == null) {
                combos = new ArrayList<Combo>();
                fieldsWithHistory.put(id, combos);
            }
            combos.add(combo);
        }
    }

    protected String validateArtifactIdInput(String text) {
        return validateIdInput(text, true);
    }

    protected boolean validateFile(String text){
    	if(StringUtils.isEmpty(text))
    		return true;
    	File file = new File(text);
    	if(file!=null &&  file.isFile())
    		return true;
    	return false;

    }

    protected String validateGroupIdInput(String text) {
        return validateIdInput(text, false);
    }

    private String validateIdInput(String text, boolean artifact) {
        if (StringUtils.isBlank(text)) {
            if(artifact){
                return "Enter an artifact id";
            }else{
                return "Enter a group id for the artifact.";
            }
        }else if (text.contains(" ")) {
            if(artifact){
                return "Artifact id cannot contain spaces.";
            }else{
                return "Group id cannot contain spaces.";
            }
        }

        IStatus nameStatus = ResourcesPlugin.getWorkspace().validateName(text, IResource.PROJECT);
        if (!nameStatus.isOK()) {
            return NLS.bind(artifact? "Invalid artifact id\\: {0}": "Invalid group id\\: {0}", nameStatus.getMessage());
        }

        if (!text.matches("[A-Za-z0-9_.]+")) {
            return NLS.bind(artifact? "Invalid artifact id\\: {0}": "Invalid group id\\: {0}", text);
         }

        return null;
    }

    public final void updateStatus(String message) {
        setErrorMessage(message);
        if (message == null) {
            setPageComplete(true);
        } else {
            setPageComplete(false);
        }
    }

    public abstract void internalCreateControl(Composite parent) throws ProjectCreationException;

    public final void createControl(Composite parent) {
        try {
            internalCreateControl(parent);
        } catch (Exception exception) {
            ProjectCreationPlugin.logThrowable(exception);
        }
    }

    public void updateDescription(String desc) {
        setDescription(desc);
    }
}
