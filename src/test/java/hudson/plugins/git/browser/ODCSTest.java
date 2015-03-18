package hudson.plugins.git.browser;

import hudson.plugins.git.GitChangeLogParser;
import hudson.plugins.git.GitChangeSet;
import hudson.plugins.git.GitChangeSet.Path;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

/**
 * @author Paul Nyheim (paul.nyheim@gmail.com)
 */
public class ODCSTest extends TestCase {

    private static final String ODCS_URL = "http://SERVER/org/#projects/org_project/scm/repo.git";
    private final ODCS odcs;

    {
        try {
            odcs = new ODCS(ODCS_URL);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Test method for {@link hudson.plugins.git.browser.ODCS#getUrl()}.
     * 
     * @throws MalformedURLException
     */
    public void testGetUrl() throws MalformedURLException {
        assertEquals(ODCS_URL + "/", odcs.getUrl().toString());
    }

    /**
     * Test method for {@link hudson.plugins.git.browser.ODCS#getUrl()}.
     * 
     * @throws MalformedURLException
     */
    public void testGetUrlForRepoWithTrailingSlash() throws MalformedURLException {
        assertEquals(new ODCS(ODCS_URL + "/").getUrl().toString(), ODCS_URL + "/");
    }

    /**
     * Test method for
     * {@link hudson.plugins.git.browser.ODCS#getChangeSetLink(hudson.plugins.git.GitChangeSet)}
     * .
     * 
     * @throws SAXException
     * @throws IOException
     */
    public void testGetChangeSetLinkGitChangeSet() throws IOException, SAXException {
        final URL changeSetLink = odcs.getChangeSetLink(createChangeSet("rawchangelog"));
        assertEquals(ODCS_URL + "/commit/396fc230a3db05c427737aa5c2eb7856ba72b05d", changeSetLink.toString());
    }

    /**
     * Test method for
     * {@link hudson.plugins.git.browser.ODCS#getDiffLink(hudson.plugins.git.GitChangeSet.Path)}
     * .
     * 
     * @throws SAXException
     * @throws IOException
     */
    public void testGetDiffLinkPath() throws IOException, SAXException {
        final HashMap<String, Path> pathMap = createPathMap("rawchangelog");
        final Path path1 = pathMap.get("src/main/java/hudson/plugins/git/browser/GithubWeb.java");
        assertEquals(ODCS_URL + "/commit/396fc230a3db05c427737aa5c2eb7856ba72b05d?oi=src/main/java/hudson/plugins/git/browser/GithubWeb.java", odcs.getDiffLink(path1).toString());
        final Path path2 = pathMap.get("src/test/java/hudson/plugins/git/browser/GithubWebTest.java");
        assertEquals(ODCS_URL + "/commit/396fc230a3db05c427737aa5c2eb7856ba72b05d?oi=src/test/java/hudson/plugins/git/browser/GithubWebTest.java", odcs.getDiffLink(path2).toString());
        final Path path3 = pathMap.get("src/test/resources/hudson/plugins/git/browser/rawchangelog-with-deleted-file");
        assertEquals(ODCS_URL + "/commit/396fc230a3db05c427737aa5c2eb7856ba72b05d?oi=" + path3.getPath(), odcs.getDiffLink(path3).toString());
    }

    /**
     * Test method for
     * {@link hudson.plugins.git.browser.ODCS#getFileLink(hudson.plugins.git.GitChangeSet.Path)}
     * .
     * 
     * @throws SAXException
     * @throws IOException
     */
    public void testGetFileLinkPath() throws IOException, SAXException {
        final HashMap<String, Path> pathMap = createPathMap("rawchangelog");
        final Path path = pathMap.get("src/main/java/hudson/plugins/git/browser/GithubWeb.java");
        final URL fileLink = odcs.getFileLink(path);
        assertEquals(ODCS_URL + "/blob/src/main/java/hudson/plugins/git/browser/GithubWeb.java?revision=396fc230a3db05c427737aa5c2eb7856ba72b05d",
                String.valueOf(fileLink));
    }
    
    public void testGetDiffLinkForDeletedFile() throws Exception{
        final HashMap<String, Path> pathMap = createPathMap("rawchangelog-with-deleted-file");
        final Path path = pathMap.get("bar");
        assertEquals(ODCS_URL + "/commit/fc029da233f161c65eb06d0f1ed4f36ae81d1f4f?oi=" + path.getPath(), odcs.getDiffLink(path).toString());
    }

    /**
     * Test method for
     * {@link hudson.plugins.git.browser.ODCS#getFileLink(hudson.plugins.git.GitChangeSet.Path)}
     * .
     * 
     * @throws SAXException
     * @throws IOException
     */
    public void testGetFileLinkPathForDeletedFile() throws IOException, SAXException {
        final HashMap<String, Path> pathMap = createPathMap("rawchangelog-with-deleted-file");
        final Path path = pathMap.get("bar");
        final URL fileLink = odcs.getFileLink(path);

        assertEquals(ODCS_URL + "/commit/fc029da233f161c65eb06d0f1ed4f36ae81d1f4f?oi=" + path.getPath(), String.valueOf(fileLink));
    }

    private GitChangeSet createChangeSet(String rawchangelogpath) throws IOException, SAXException {
        final File rawchangelog = new File(ODCSTest.class.getResource(rawchangelogpath).getFile());
        final GitChangeLogParser logParser = new GitChangeLogParser(false);
        final List<GitChangeSet> changeSetList = logParser.parse(null, rawchangelog).getLogs();
        return changeSetList.get(0);
    }

    /**
     * @param changelog
     * @return
     * @throws IOException
     * @throws SAXException
     */
    private HashMap<String, Path> createPathMap(final String changelog) throws IOException, SAXException {
        final HashMap<String, Path> pathMap = new HashMap<String, Path>();
        final Collection<Path> changeSet = createChangeSet(changelog).getPaths();
        for (final Path path : changeSet) {
            pathMap.put(path.getPath(), path);
        }
        return pathMap;
    }

}
