import com.applitools.eyes.*;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.StitchMode;
import junit.framework.JUnit4TestAdapter;
import org.junit.*;
import org.junit.rules.TestName;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;

import static org.junit.Assert.assertEquals;

public class ExampleTest {

    @Rule
    public TestName name = new TestName() {
        public String getMethodName() {
            return String.format("%s", super.getMethodName());
        }
    };

    private Eyes eyes = new Eyes();
    private WebDriver driver;
    private String applitoolsKey = System.getenv("APPLITOOLS_API_KEY");

    private static String username = System.getenv("SAUCE_USER");
    private static String accesskey = System.getenv("SAUCE_KEY");

    private static BatchInfo batch;

    @BeforeClass
    public static void batchInitialization(){
        batch = new BatchInfo("Jenkins Plugin Example");
    }

    @Before
    public void setUp () throws Exception {

        //eyes.setServerUrl(URI.create("https://your-onprem-host"));
        eyes.setApiKey(applitoolsKey);
        //Hide scrollbars on older browsers. Usually IE includes them...
        eyes.setHideScrollbars(true);
        //Take a full page screenshot
        eyes.setForceFullPageScreenshot(false);
        //Stitch pages together and remove floating headers and footers...
        eyes.setStitchMode(StitchMode.CSS);
        //Set match level to Layout2 for dynamic content sites.
        eyes.setMatchLevel(MatchLevel.LAYOUT2);
        //Set batch name. Essentially a folder name to group your images.
        //Set only once per Jenkins job
        //http://support.applitools.com/customer/en/portal/articles/2689601-integration-with-the-jenkins-plugin

        if (System.getenv("APPLITOOLS_BATCH_ID") != null ) {
            batch.setId(System.getenv("APPLITOOLS_BATCH_ID"));
        }

        //End of - Set only once per Jenkins job
        eyes.setBatch(batch);

        //set new baseline images. Use this when your site has changed without having to do in the dashboard.
        //eyes.setSaveFailedTests(true);

        //output detailed log data to console...
        eyes.setLogHandler(new StdoutLogHandler(true));

        DesiredCapabilities capability = new DesiredCapabilities();
        capability.setCapability(CapabilityType.PLATFORM, "Windows 10");
        capability.setCapability(CapabilityType.BROWSER_NAME, "chrome");
        capability.setCapability(CapabilityType.VERSION, "62.0");
        capability.setCapability("screenResolution", "2560x1600");
        capability.setCapability("name", name.getMethodName());

        String sauce_url = "https://"+ username +":"+ accesskey + "@ondemand.saucelabs.com:443/wd/hub";
        driver = new RemoteWebDriver(new URL(sauce_url), capability);
        driver.get("https://github.com");
    }

    @Test
    public void GithubHomePage () throws Exception {
        eyes.open(driver, "Github", "Home Page", new RectangleSize(1000, 600));
        eyes.checkWindow("github");
        TestResults results = eyes.close(false);
        assertEquals(true, results.isPassed());
    }

    @After
    public void tearDown () throws Exception {
        driver.quit();
        eyes.abortIfNotClosed();
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(new JUnit4TestAdapter(ExampleTest.class));
    }
}