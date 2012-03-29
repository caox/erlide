package com.rytong.ewp;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.erlide.backend.BackendCore;
import org.erlide.backend.IBackend;
import org.erlide.backend.runtimeinfo.RuntimeInfo;
import org.erlide.jinterface.ErlLogger;

public class EWPWizard extends Wizard implements INewWizard {

    private EWPWizardPage fPage;
    
    private ISelection fSelection;
    
    public EWPWizard() {
        super();
        setNeedsProgressMonitor(true);
        setWindowTitle("New EWP Node");
        }

    /**
     * Adding the page to the wizard.
     */

    @Override
    public void addPages() {
        fPage = new EWPWizardPage(fSelection);
        addPage(fPage);
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean performFinish() {
        final String ewpName = fPage.getEWPName();
        final String cookie = fPage.getCookie();
        final String ewpEbin = fPage.getEWPEbinPath();
        final IBackend ideBackend = BackendCore.getBackendManager()
                .getIdeBackend();
        ErlLogger.debug("Starting regsiter ewp node %s with cookie %s and ebin is under %s", ewpName, cookie, ewpEbin);
        final RuntimeInfo info = ideBackend.getRuntimeInfo();
        info.setNodeName(ewpName);
        info.setCookie(cookie);
        // TODO Add error message and prompt it when can't find the ewp node
        return null != BackendCore.getBackendManager().registerExsitedBackend(info);
    }

}
