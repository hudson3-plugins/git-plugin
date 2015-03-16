package hudson.plugins.git.browser;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.plugins.git.GitChangeSet;
import hudson.plugins.git.GitChangeSet.Path;
import hudson.scm.EditType;
import hudson.scm.RepositoryBrowser;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Git Browser for Oracle Developer Cloud Service
 */
public class ODCS extends GitRepositoryBrowser {

    private static final long serialVersionUID = 1L;
    private final URL orgUrl;
    private final String projectName;
    private final String repoName;

    @DataBoundConstructor
    public ODCS(String orgUrl, String projectName, String repoName) throws MalformedURLException {
        if (orgUrl != null && !orgUrl.endsWith("/")) {
            orgUrl = orgUrl + "/";
        }
        this.orgUrl = new URL(orgUrl);
		this.projectName = projectName;
		this.repoName = repoName;
    }

    public URL getOrgUrl() {
        return orgUrl;
    }

    public String getProjectName() {
        return projectName;
    }
    
    public String getRepoName() {
        return repoName;
    }

    /**
     * Creates a link to the changeset
     *
     * https://[GitLab URL]/commit/a9182a07750c9a0dfd89a8461adf72ef5ef0885b
     *
     * @return diff link
     * @throws IOException
     */
    @Override
    public URL getChangeSetLink(GitChangeSet changeSet) throws IOException {
        return new URL(getOrgUrl(), "#projects/" + getProjectName() + "/scm/" + getRepoName() + ".git/commit/" + changeSet.getId().toString());
    }

    /**
     * Creates a link to the commit diff.
     * 
     * https://[ODCS URL]/commit/a9182a07750c9a0dfd89a8461adf72ef5ef0885b
     * 
     * @param path
     * @return diff link
     * @throws IOException
     */
    @Override
    public URL getDiffLink(Path path) throws IOException {
        final GitChangeSet changeSet = path.getChangeSet();
        return new URL(getOrgUrl(), "#projects/" + getProjectName() + "/scm/" + getRepoName() + ".git/commit/" + changeSet.getId().toString() + "?oi=" + path.getPath());
    }

    /**
     * Creates a link to the file.
     * https://[GitLab URL]/a9182a07750c9a0dfd89a8461adf72ef5ef0885b/tree/pom.xml
     * https://[ODCS URL]/blob/tree/pom.xml
     * 
     * @param path
     * @return file link
     * @throws IOException
     */
    @Override
    public URL getFileLink(Path path) throws IOException {
        if (path.getEditType().equals(EditType.DELETE)) {
            return getDiffLink(path);
        } else {
            return new URL(getOrgUrl(), "#projects/" + getProjectName() + "/scm/" + getRepoName() + ".git/blob/" + path.getPath() + "?revision=" + path.getChangeSet().getId()); 
        }
    }

    @Extension
    public static class ODCSDescriptor extends Descriptor<RepositoryBrowser<?>> {
        public String getDisplayName() {
            return "ODCS";
        }

        @Override
        public ODCS newInstance(StaplerRequest req, JSONObject jsonObject) throws FormException {
            return req.bindJSON(ODCS.class, jsonObject);
        }
    }

}
