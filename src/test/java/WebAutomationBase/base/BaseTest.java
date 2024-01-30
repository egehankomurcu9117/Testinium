package WebAutomationBase.base;
import com.thoughtworks.gauge.AfterScenario;

import com.thoughtworks.gauge.BeforeScenario;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.apache.commons.lang3.StringUtils;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;

import org.openqa.selenium.chrome.ChromeDriver;

import org.openqa.selenium.chrome.ChromeOptions;

import org.openqa.selenium.firefox.FirefoxDriver;

import org.openqa.selenium.firefox.FirefoxOptions;

import org.openqa.selenium.firefox.FirefoxProfile;

import org.openqa.selenium.remote.DesiredCapabilities;

import org.openqa.selenium.remote.LocalFileDetector;

import org.openqa.selenium.remote.RemoteWebDriver;

import org.openqa.selenium.support.ui.WebDriverWait;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;




import java.net.MalformedURLException;

import java.net.URL;

import java.util.HashMap;

import java.util.Map;

import java.util.concurrent.TimeUnit;
public class BaseTest {
  protected static WebDriver driver;
  protected static WebDriverWait webDriverWait;
  private static Logger logger = LoggerFactory.getLogger(BaseTest.class);
  DesiredCapabilities capabilities = new DesiredCapabilities();
  ChromeOptions options = new ChromeOptions();
  @BeforeScenario
  public void setUp() throws  Exception {
    String baseUrl = "https://www.google.com/";
    //WebDriverManager.chromedriver().setup();
    if (StringUtils.isEmpty(System.getenv("key"))){
      capabilities = DesiredCapabilities.chrome();
      capabilities.setBrowserName("chrome");
      capabilities.setPlatform(Platform.WINDOWS);
      options.setExperimentalOption("w3c", false);
      options.addArguments("disable-translate");
      options.addArguments("--disable-notifications");
      options.addArguments("--start-fullscreen");
      Map<String, Object> prefs = new HashMap<>();
      options.setExperimentalOption("prefs",prefs);
      capabilities.setCapability(ChromeOptions.CAPABILITY, options);
      capabilities.setCapability("key", System.getenv("key"));
      driver = new RemoteWebDriver(new URL("http://192.168.60.191:4444/wd/hub"), capabilities);
      logger.info("driver ayakta 1");
    }
    else {
      //capabilities = DesiredCapabilities.chrome();
      capabilities = DesiredCapabilities.chrome();
      capabilities.setBrowserName("chrome");
      capabilities.setPlatform(Platform.WINDOWS);
      options.setExperimentalOption("w3c", false);
      options.addArguments("disable-translate");
      options.addArguments("--disable-notifications");
      options.addArguments("--start-fullscreen");
      Map<String, Object> prefs = new HashMap<>();
      options.setExperimentalOption("prefs",prefs);
      capabilities.setCapability(ChromeOptions.CAPABILITY, options);
      capabilities.setCapability("key", System.getenv("key"));
      driver = new RemoteWebDriver(new URL("http://192.168.60.191:4444/wd/hub"), capabilities);
      logger.info("driver ayakta 2");
    }
    driver.manage().timeouts().pageLoadTimeout(60,TimeUnit.SECONDS);
    //driver.manage().window().fullscreen();
    driver.manage().window().maximize();
    driver.get(baseUrl);
  }
  @AfterScenario
  public void tearDown() {
    driver.quit();
  }
}
