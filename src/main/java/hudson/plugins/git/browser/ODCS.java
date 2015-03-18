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
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Git Browser for Oracle Developer Cloud Service
 */
public class ODCS extends GitRepositoryBrowser {

    private static final long serialVersionUID = 1L;
    private final URL url;

    @DataBoundConstructor
    public ODCS(String url) throws MalformedURLException {
        if (url != null && !url.endsWith("/")) {
            url = url + "/";
        }
        this.url = new URL(url);
    }

    public URL getUrl() {
        return url;
    }

    /**
     * Creates a link to the changeset
     *
     * https://[ODCS URL]/commit/a9182a07750c9a0dfd89a8461adf72ef5ef0885b
     *
     * @return diff link
     * @throws IOException
     */
    @Override
    public URL getChangeSetLink(GitChangeSet changeSet) throws IOException {
        String changeSetURLString = String.format("%s/commit/%s",
                urlAsStringWithoutSlash(), changeSet.getId().toString());
        return new URL(changeSetURLString);
    }

    /**
     * Creates a link to the commit diff.
     * 
     * https://[ODCS URL]/commit/a9182a07750c9a0dfd89a8461adf72ef5ef0885b?oi=foo.java
     * 
     * @param path
     * @return diff link
     * @throws IOException
     */
    @Override
    public URL getDiffLink(Path path) throws IOException {
        final GitChangeSet changeSet = path.getChangeSet();
        String changeSetURLString = String.format("%s/commit/%s?oi=%s",
                urlAsStringWithoutSlash(), changeSet.getId().toString(), 
                path.getPath());
        return new URL(changeSetURLString);
    }

    /**
     * Creates a link to the file.
     * https://[ODCS URL]/blob/pom.xml?revision=a9182a07750c9a0dfd89a8461adf72ef5ef0885b
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
            String changeSetURLString = String.format("%s/blob/%s?revision=%s",
                    urlAsStringWithoutSlash(), path.getPath(), 
                    path.getChangeSet().getId());
            return new URL(changeSetURLString);
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

    private String urlAsStringWithoutSlash() {
        String urlString = url.toString();
        while (urlString.endsWith("/")) {
            urlString = urlString.substring(0, urlString.length() - 1);
        }
        return urlString;
    }

}
