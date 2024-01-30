package WebAutomationBase.step;

import WebAutomationBase.base.BaseTest;
import WebAutomationBase.helper.ElementHelper;
import WebAutomationBase.helper.StoreHelper;
import WebAutomationBase.model.ElementInfo;
import com.thoughtworks.gauge.Step;

import java.sql.*;
import java.util.*;
import java.util.NoSuchElementException;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.slf4j.LoggerFactory;
import org.slf4j.impl.Log4jLoggerAdapter;


import javax.swing.text.Document;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Fail.fail;
import static org.junit.Assert.assertTrue;

public class BaseSteps extends BaseTest {

  public static int DEFAULT_MAX_ITERATION_COUNT = 150;
  public static int DEFAULT_MILLISECOND_WAIT_AMOUNT = 100;

  private static Log4jLoggerAdapter logger = (Log4jLoggerAdapter) LoggerFactory
          .getLogger(BaseSteps.class);

  private static String SAVED_ATTRIBUTE;

  private Actions actions = new Actions(driver);
  private String compareText;


  private ApiTestingPost apiTestingpost = new ApiTestingPost();

  public BaseSteps() {

    PropertyConfigurator
            .configure(BaseSteps.class.getClassLoader().getResource("log4j.properties"));
  }

  private WebElement findElement(String key) {
    ElementInfo elementInfo = StoreHelper.INSTANCE.findElementInfoByKey(key);
    By infoParam = ElementHelper.getElementInfoToBy(elementInfo);
    WebDriverWait webDriverWait = new WebDriverWait(driver, 60);
    WebElement webElement = webDriverWait
            .until(ExpectedConditions.presenceOfElementLocated(infoParam));
    ((JavascriptExecutor) driver).executeScript(
            "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center', inline: 'center'})",
            webElement);
    return webElement;
  }

  private List<WebElement> findElements(String key) {
    ElementInfo elementInfo = StoreHelper.INSTANCE.findElementInfoByKey(key);
    By infoParam = ElementHelper.getElementInfoToBy(elementInfo);
    return driver.findElements(infoParam);
  }

  private void clickElement(WebElement element) {
    element.click();
  }

  private void clickElementBy(String key) {
    findElement(key).click();
  }

  private void hoverElement(WebElement element) {
    actions.moveToElement(element).build().perform();
  }

  private void hoverElementBy(String key) {
    WebElement webElement = findElement(key);
    actions.moveToElement(webElement).build().perform();
  }

  private void sendKeyESC(String key) {
    findElement(key).sendKeys(Keys.ESCAPE);

  }

  private boolean isDisplayed(WebElement element) {
    return element.isDisplayed();
  }

  private boolean isDisplayedBy(By by) {
    return driver.findElement(by).isDisplayed();
  }

  private String getPageSource() {
    return driver.switchTo().alert().getText();
  }

  public static String getSavedAttribute() {
    return SAVED_ATTRIBUTE;
  }

  public String randomString(int stringLength) {

    Random random = new Random();
    char[] chars = "ABCDEFGHIJKLMNOPQRSTUWVXYZabcdefghijklmnopqrstuwvxyz0123456789".toCharArray();
    String stringRandom = "";
    for (int i = 0; i < stringLength; i++) {

      stringRandom = stringRandom + chars[random.nextInt(chars.length)];
    }

    return stringRandom;
  }

  public WebElement findElementWithKey(String key) {
    return findElement(key);
  }

  public String getElementText(String key) {
    return findElement(key).getText();
  }

  public String getElementAttributeValue(String key, String attribute) {
    return findElement(key).getAttribute(attribute);
  }

  @Step("Print page source")
  public void printPageSource() {
    System.out.println(getPageSource());
  }

  public void javaScriptClicker(WebDriver driver, WebElement element) {

    JavascriptExecutor jse = ((JavascriptExecutor) driver);
    jse.executeScript("var evt = document.createEvent('MouseEvents');"
            + "evt.initMouseEvent('click',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);"
            + "arguments[0].dispatchEvent(evt);", element);
  }

  public void javascriptclicker(WebElement element) {
    JavascriptExecutor executor = (JavascriptExecutor) driver;
    executor.executeScript("arguments[0].click();", element);
  }

  @Step({"Wait <value> seconds",
          "<int> saniye bekle"})
  public void waitBySeconds(int seconds) {
    try {
      logger.info(seconds + " saniye bekleniyor.");
      Thread.sleep(seconds * 1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Step({"Wait <value> milliseconds",
          "<long> milisaniye bekle"})
  public void waitByMilliSeconds(long milliseconds) {
    try {
      logger.info(milliseconds + " milisaniye bekleniyor.");
      Thread.sleep(milliseconds);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Step({"Wait for element then click <key>",
          "Elementi bekle ve sonra tıkla <key>"})
  public void checkElementExistsThenClick(String key) {
    getElementWithKeyIfExists(key);
    clickElement(key);
    logger.info(key + " elementine tıklandı.");
  }

  @Step({"Click to element <key>",
          "Elementine tıkla <key>"})
  public void clickElement(String key) {
    if (!key.equals("")) {
      WebElement element = findElement(key);
      hoverElement(element);
      waitByMilliSeconds(500);
      clickElement(element);
      logger.info(key + " elementine tıklandı.");
    }
  }

  @Step({"Click to element <key> with focus",
          "<key> elementine focus ile tıkla"})
  public void clickElementWithFocus(String key) {
    actions.moveToElement(findElement(key));
    actions.click();
    actions.build().perform();
    logger.info(key + " elementine focus ile tıklandı.");
  }

  @Step({"Check if element <key> exists",
          "Wait for element to load with key <key>",
          "Element var mı kontrol et <key>",
          "Elementin yüklenmesini bekle <key>"})
  public WebElement getElementWithKeyIfExists(String key) {
    WebElement webElement;
    int loopCount = 0;
    while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
      try {
        webElement = findElementWithKey(key);
        logger.info(key + " elementi bulundu.");
        return webElement;
      } catch (WebDriverException e) {
      }
      loopCount++;
      waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
    }
    Assert.fail("Element: '" + key + "' doesn't exist.");
    return null;
  }

  @Step({"Go to <url> address",
          "<url> adresine git"})
  public void goToUrl(String url) {
    driver.get(url);
    logger.info(url + " adresine gidiliyor.");
  }

  @Step({"Wait for element to load with css <css>",
          "Elementin yüklenmesini bekle css <css>"})
  public void waitElementLoadWithCss(String css) {
    int loopCount = 0;
    while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
      if (driver.findElements(By.cssSelector(css)).size() > 0) {
        logger.info(css + " elementi bulundu.");
        return;
      }
      loopCount++;
      waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
    }
    Assert.fail("Element: '" + css + "' doesn't exist.");
  }

  @Step({"Wait for element to load with xpath <xpath>",
          "Elementinin yüklenmesini bekle xpath <xpath>"})
  public void waitElementLoadWithXpath(String xpath) {
    int loopCount = 0;
    while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
      if (driver.findElements(By.xpath(xpath)).size() > 0) {
        logger.info(xpath + " elementi bulundu.");
        return;
      }
      loopCount++;
      waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
    }
    Assert.fail("Element: '" + xpath + "' doesn't exist.");
  }

  @Step({"Check if element <key> exists else print message <message>",
          "Element <key> var mı kontrol et yoksa hata mesajı ver <message>"})
  public void getElementWithKeyIfExistsWithMessage(String key, String message) {
    ElementInfo elementInfo = StoreHelper.INSTANCE.findElementInfoByKey(key);
    By by = ElementHelper.getElementInfoToBy(elementInfo);

    int loopCount = 0;
    while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
      if (driver.findElements(by).size() > 0) {
        logger.info(key + " elementi bulundu.");
        return;
      }
      loopCount++;
      waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
    }
    Assert.fail(message);
  }

  @Step({"Check if element <key> not exists",
          "Element yok mu kontrol et <key>"})
  public void checkElementNotExists(String key) {
    ElementInfo elementInfo = StoreHelper.INSTANCE.findElementInfoByKey(key);
    By by = ElementHelper.getElementInfoToBy(elementInfo);

    int loopCount = 0;
    while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
      if (driver.findElements(by).size() == 0) {
        logger.info(key + " elementinin olmadığı kontrol edildi.");
        return;
      }
      loopCount++;
      waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
    }
    Assert.fail("Element '" + key + "' still exist.");
  }

  @Step({"Upload file in project <path> to element <key>",
          "Proje içindeki <path> dosyayı <key> elemente upload et"})
  public void uploadFile(String path, String key) {
    String pathString = System.getProperty("user.dir") + "/";
    pathString = pathString + path;
    findElement(key).sendKeys(pathString);
    logger.info(path + " dosyası " + key + " elementine yüklendi.");
  }

  @Step({"Write value <text> to element <key>",
          "<text> textini <key> elemente yaz"})
  public void ssendKeys(String text, String key) {
    if (!key.equals("")) {
      findElement(key).sendKeys(text);
      logger.info(key + " elementine " + text + " texti yazıldı.");
    }
  }

  @Step({"Click with javascript to css <css>",
          "Javascript ile css tıkla <css>"})
  public void javascriptClickerWithCss(String css) {
    assertTrue("Element bulunamadı", isDisplayedBy(By.cssSelector(css)));
    javaScriptClicker(driver, driver.findElement(By.cssSelector(css)));
    logger.info("Javascript ile " + css + " tıklandı.");
  }

  @Step({"Click with javascript to xpath <xpath>",
          "Javascript ile xpath tıkla <xpath>"})
  public void javascriptClickerWithXpath(String xpath) {
    assertTrue("Element bulunamadı", isDisplayedBy(By.xpath(xpath)));
    javaScriptClicker(driver, driver.findElement(By.xpath(xpath)));
    logger.info("Javascript ile " + xpath + " tıklandı.");
  }

  @Step({"Check if current URL contains the value <expectedURL>",
          "Şuanki URL <url> değerini içeriyor mu kontrol et"})
  public void checkURLContainsRepeat(String expectedURL) {
    int loopCount = 0;
    String actualURL = "";
    while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
      actualURL = driver.getCurrentUrl();

      if (actualURL != null && actualURL.contains(expectedURL)) {
        logger.info("Şuanki URL" + expectedURL + " değerini içeriyor.");
        return;
      }
      loopCount++;
      waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
    }
    Assert.fail(
            "Actual URL doesn't match the expected." + "Expected: " + expectedURL + ", Actual: "
                    + actualURL);
  }

  @Step({"Send TAB key to element <key>",
          "Elemente TAB keyi yolla <key>"})
  public void sendKeyToElementTAB(String key) {
    findElement(key).sendKeys(Keys.TAB);
    logger.info(key + " elementine TAB keyi yollandı.");
  }

  @Step({"Send BACKSPACE key to element <key>",
          "Elemente BACKSPACE keyi yolla <key>"})
  public void sendKeyToElementBACKSPACE(String key) {
    findElement(key).sendKeys(Keys.BACK_SPACE);
    logger.info(key + " elementine BACKSPACE keyi yollandı.");
  }

  @Step({"Send ESCAPE key to element <key>",
          "Elemente ESCAPE keyi yolla <key>"})
  public void sendKeyToElementESCAPE(String key) {
    findElement(key).sendKeys(Keys.ESCAPE);
    logger.info(key + " elementine ESCAPE keyi yollandı.");
  }

  @Step({"Check if element <key> has attribute <attribute>",
          "<key> elementi <attribute> niteliğine sahip mi"})
  public void checkElementAttributeExists(String key, String attribute) {
    WebElement element = findElement(key);
    int loopCount = 0;
    while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
      if (element.getAttribute(attribute) != null) {
        logger.info(key + " elementi " + attribute + " niteliğine sahip.");
        return;
      }
      loopCount++;
      waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
    }
    Assert.fail("Element DOESN't have the attribute: '" + attribute + "'");
  }

  @Step({"Check if element <key> not have attribute <attribute>",
          "<key> elementi <attribute> niteliğine sahip değil mi"})
  public void checkElementAttributeNotExists(String key, String attribute) {
    WebElement element = findElement(key);

    int loopCount = 0;

    while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
      if (element.getAttribute(attribute) == null) {
        logger.info(key + " elementi " + attribute + " niteliğine sahip olmadığı kontrol edildi.");
        return;
      }
      loopCount++;
      waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
    }
    Assert.fail("Element STILL have the attribute: '" + attribute + "'");
  }

  @Step({"Check if <key> element's attribute <attribute> equals to the value <expectedValue>",
          "<key> elementinin <attribute> niteliği <value> değerine sahip mi"})
  public void checkElementAttributeEquals(String key, String attribute, String expectedValue) {
    WebElement element = findElement(key);

    String actualValue;
    int loopCount = 0;
    while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
      actualValue = element.getAttribute(attribute).trim();
      if (actualValue.equals(expectedValue)) {
        logger.info(
                key + " elementinin " + attribute + " niteliği " + expectedValue + " değerine sahip.");
        return;
      }
      loopCount++;
      waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
    }
    Assert.fail("Element's attribute value doesn't match expected value");
  }

  @Step({"Check if <key> element's attribute <attribute> contains the value <expectedValue>",
          "<key> elementinin <attribute> niteliği <value> değerini içeriyor mu"})
  public void checkElementAttributeContains(String key, String attribute, String expectedValue) {
    WebElement element = findElement(key);

    String actualValue;
    int loopCount = 0;
    while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
      actualValue = element.getAttribute(attribute).trim();
      if (actualValue.contains(expectedValue)) {
        return;
      }
      loopCount++;
      waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
    }
    Assert.fail("Element's attribute value doesn't contain expected value");
  }

  @Step({"Write <value> to <attributeName> of element <key>",
          "<value> değerini <attribute> niteliğine <key> elementi için yaz"})
  public void setElementAttribute(String value, String attributeName, String key) {
    String attributeValue = findElement(key).getAttribute(attributeName);
    findElement(key).sendKeys(attributeValue, value);
  }

  @Step({"Write <value> to <attributeName> of element <key> with Js",
          "<value> değerini <attribute> niteliğine <key> elementi için JS ile yaz"})
  public void setElementAttributeWithJs(String value, String attributeName, String key) {
    WebElement webElement = findElement(key);
    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript("arguments[0].setAttribute('" + attributeName + "', '" + value + "')",
            webElement);
  }

  @Step({"Clear text of element <key>",
          "<key> elementinin text alanını temizle"})
  public void clearInputArea(String key) {
    findElement(key).clear();
  }

  @Step({"Clear text of element <key> with BACKSPACE",
          "<key> elementinin text alanını BACKSPACE ile temizle"})
  public void clearInputAreaWithBackspace(String key) {
    WebElement element = findElement(key);
    element.clear();
    element.sendKeys("a");
    actions.sendKeys(Keys.BACK_SPACE).build().perform();
  }

  @Step({"Save attribute <attribute> value of element <key>",
          "<attribute> niteliğini sakla <key> elementi için"})
  public void saveAttributeValueOfElement(String attribute, String key) {
    SAVED_ATTRIBUTE = findElement(key).getAttribute(attribute);
    System.out.println("Saved attribute value is: " + SAVED_ATTRIBUTE);
  }

  @Step({"Write saved attribute value to element <key>",
          "Kaydedilmiş niteliği <key> elementine yaz"})
  public void writeSavedAttributeToElement(String key) {
    findElement(key).sendKeys(SAVED_ATTRIBUTE);
  }

  @Step({"Check if element <key> contains text <expectedText>",
          "<key> elementi <text> değerini içeriyor mu kontrol et"})
  public void checkElementContainsText(String key, String expectedText) {

    Boolean containsText = findElement(key).getText().contains(expectedText);
    assertTrue("Expected text is not contained", containsText);
    logger.info(key + " elementi" + expectedText + "değerini içeriyor.");
  }

  @Step({"Write random value to element <key>",
          "<key> elementine random değer yaz"})
  public void writeRandomValueToElement(String key) {
    findElement(key).sendKeys(randomString(15));
  }

  @Step({"Write random value to element <key> starting with <text>",
          "<key> elementine <text> değeri ile başlayan random değer yaz"})
  public void writeRandomValueToElement(String key, String startingText) {
    String randomText = startingText + randomString(15);
    findElement(key).sendKeys(randomText);
  }

  @Step({"Print element text by css <css>",
          "Elementin text değerini yazdır css <css>"})
  public void printElementText(String css) {
    System.out.println(driver.findElement(By.cssSelector(css)).getText());
  }

  @Step({"Write value <string> to element <key> with focus",
          "<string> değerini <key> elementine focus ile yaz"})
  public void sendKeysWithFocus(String text, String key) {
    actions.moveToElement(findElement(key));
    actions.click();
    actions.sendKeys(text);
    actions.build().perform();
    logger.info(key+ " elementine "+text+" değeri focus ile yazıldı.");
  }

  @Step({"Refresh page",
          "Sayfayı yenile"})
  public void refreshPage() {
    driver.navigate().refresh();
  }


  @Step({"Change page zoom to <value>%",
          "Sayfanın zoom değerini değiştir <value>%"})
  public void chromeZoomOut(String value) {
    JavascriptExecutor jsExec = (JavascriptExecutor) driver;
    jsExec.executeScript("document.body.style.zoom = '" + value + "%'");
  }

  @Step({"Open new tab",
          "Yeni sekme aç"})
  public void chromeOpenNewTab() {
    ((JavascriptExecutor) driver).executeScript("window.open()");
  }

  @Step({"Focus on tab number <number>",
          "<number> numaralı sekmeye odaklan"})//Starting from 1
  public void chromeFocusTabWithNumber(int number) {
    ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
    driver.switchTo().window(tabs.get(number - 1));
  }

  @Step({"Focus on last tab",
          "Son sekmeye odaklan"})
  public void chromeFocusLastTab() {
    ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
    driver.switchTo().window(tabs.get(tabs.size() - 1));
  }

  @Step({"Focus on frame with <key>",
          "Frame'e odaklan <key>"})
  public void chromeFocusFrameWithNumber(String key) {
    WebElement webElement = findElement(key);
    driver.switchTo().frame(webElement);
  }

  @Step({"Accept Chrome alert popup",
          "Chrome uyarı popup'ını kabul et"})
  public void acceptChromeAlertPopup() {
    driver.switchTo().alert().accept();
  }


  //----------------------SONRADAN YAZILANLAR-----------------------------------

  public void randomSec(String key) {
    List<WebElement> elements = findElements(key);
    Random random = new Random();
    int index = random.nextInt(elements.size());
    elements.get(index).click();
  }

  private JavascriptExecutor getJSExecutor() {
    return (JavascriptExecutor) driver;
  }

  private Object executeJS(String script, boolean wait) {
    return wait ? getJSExecutor().executeScript(script, "") : getJSExecutor().executeAsyncScript(script, "");
  }

  private void scrollTo(int x, int y) {
    String script = String.format("window.scrollTo(%d, %d);", x, y);
    executeJS(script, true);
  }

  public WebElement scrollToElementToBeVisible(String key) {
    ElementInfo elementInfo = StoreHelper.INSTANCE.findElementInfoByKey(key);
    WebElement webElement = driver.findElement(ElementHelper.getElementInfoToBy(elementInfo));
    if (webElement != null) {
      scrollTo(webElement.getLocation().getX(), webElement.getLocation().getY() - 100);
    }
    return webElement;
  }

  @Step({"<key> alanına kaydır"})
  public void scrollToElement(String key) {
    scrollToElementToBeVisible(key);
  }


  @Step({"<key> alanına js ile kaydır"})
  public void scrollToElementWithJs(String key) {
    ElementInfo elementInfo = StoreHelper.INSTANCE.findElementInfoByKey(key);
    WebElement element = driver.findElement(ElementHelper.getElementInfoToBy(elementInfo));
    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
  }


  @Step({"<length> uzunlugunda random bir kelime üret ve <saveKey> olarak sakla"})
  public void createRandomString(int length, String saveKey) {
    StoreHelper.INSTANCE.saveValue(saveKey, randomString(length));

  }


  @Step({"<key> li elementi bul ve değerini <saveKey> saklanan degeri yazdir",
          "Find element by <key> and compare saved key <saveKey>"})
  public void equalsSendTextByKey(String key, String saveKey) throws InterruptedException {
    WebElement element;
    int waitVar = 0;
    element = findElementWithKey(key);
    while (true) {
      if (element.isDisplayed()) {
        logger.info("WebElement is found at: " + waitVar + " second.");
        element.clear();
        StoreHelper.INSTANCE.getValue(saveKey);
        element.sendKeys(StoreHelper.INSTANCE.getValue(saveKey));

        break;
      } else {
        waitVar = waitVar + 1;
        Thread.sleep(1000);
        if (waitVar == 20) {
          throw new NullPointerException(String.format("by = %s Web element list not found"));
        } else {
        }
      }
    }
  }

  private Long getTimestamp() {
    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    return (timestamp.getTime());
  }

  @Step({"<key> li elementi bul, temizle ve rasgele  email değerini yaz",
          "Find element by <key> clear and send keys  random email"})
  public void RandomeMail(String key) {
    Long timestamp = getTimestamp();
    WebElement webElement = findElementWithKey(key);
    webElement.clear();
    webElement.sendKeys("testotomasyon" + timestamp + "@sahabt.com");

  }

  @Step("<key> olarak <text> seçersem")
  public void implementation1(String key, String text) throws InterruptedException {
    List<WebElement> comboBoxElement = findElements(key);
    for (int i = 0; i < comboBoxElement.size(); i++) {
      String texts = comboBoxElement.get(i).getText();
      String textim = text;
      if (texts.contains(textim)) {
        comboBoxElement.get(i).click();
        break;
      }
    }
    logger.info(key + " comboboxından " + text + " değeri seçildi");


  }

  @Step("<key> olarak comboboxdan bir değer seçilir")
  public void comboboxRandom(String key) throws InterruptedException {

    List<WebElement> comboBoxElement = findElements(key);
    int randomIndex = new Random().nextInt(comboBoxElement.size());
    Thread.sleep(2000);
    JavascriptExecutor executor = (JavascriptExecutor) driver;
    executor.executeScript("arguments[0].click();", comboBoxElement.get(randomIndex));
    logger.info(key + " comboboxından herhangi bir değer seçildi");

  }


  @Step({"Choose <value> day later from <key>",
          "<key> degerinden <value> gün sonrasını sec"})
  public void chooseValueFromCalendar(String key, int value) throws InterruptedException {
    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
    String selected = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH) + value);
    //Bu methodun döndürdüğü değerler 1-Pazar 2-Pazartesi ... 6-Cuma 7-Cumartesi şeklindedir.
    List<WebElement> columns = findElements(key);
    calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(selected));
    System.out.println(calendar.get(Calendar.DAY_OF_WEEK));
    int i = 0;
    Thread.sleep(1000);
    for (WebElement cell : columns) {
      if (cell.getText().equals(selected)) {
        if (calendar.get(Calendar.DAY_OF_WEEK) != 1) {
          columns.get(i).click();
        } else {
          columns.get(i + 1).click();
          break;
        }
      }
      i++;
    }
    logger.info("Date selected successfully");
  }

  @Step("Url bilgisi <url> ve <path> bilgilerini gir ve  get  isteği yap")
  public void SendGetRequest(String path, String url) {
    RestAssured.baseURI = url;
    Response response = given().log().all().
            get(path).prettyPeek().then().statusCode(200).extract().response();
  }


  @Step("Siparis numarasını içeren <key> elementini kullanarak teklif al")
  public void SendPostRequestSetQuotePrice(String key) {

    String quoteNumber = TalepNoSecme(key);
    apiTestingpost.setUserName(quoteNumber);
    RestAssured.baseURI = "https://b2bqa.etasimacilik.com/TestQAService/api/v1";
    Response response = given()
            .header("Content-Type", "application/json")
            .body(apiTestingpost).log().all().
                    post("/qaservice/setQuotePrice").prettyPeek().then().statusCode(200).extract().response();
  }

  @Step("<username> kullanıcısını api üzerinden sil")
  public void ClearUserRequest(String username) {
    RestAssured.baseURI = "https://b2bqa.etasimacilik.com/TestQAService/api/v1";
    apiTestingpost.setUserName(username);
    Response response = given()
            .header("Content-Type", "application/json")
            .body(apiTestingpost).log().all()
            .when().post("/qaservice/cleanCustomerMembership")
            .prettyPeek().then().statusCode(200).extract().response();
  }

  public String TalepNoSecme(String key) {
    WebElement webElement = findElement(key);
    String talepNo = webElement.getText();
    String lastWord = talepNo.substring(talepNo.lastIndexOf(" ") + 1);
    return lastWord;
  }

  @Step("<taxNumber> Vergi numarasına sahip kullanıcı için post metodu ile <BenimTalepNumaram> Talep Numarası oluştur")
  public String talepOlusturVeFiyatOnayıVer(String taxNumber, String BenimTalepNumaram) {

    apiTestingpost.setAccountTaxNumber(taxNumber);
    RestAssured.baseURI = "https://b2bqa.etasimacilik.com/TestQAService/api/v1";
    Response response = given()
            .header("Content-Type", "application/json")
            .body(apiTestingpost).log().all().
                    post("/qaservice/createPriceConfirmedQuote").prettyPeek().then().statusCode(200).extract().response();
    String BenimTalepNo = response.path("Data.QuoteNumber");

    logger.info("BenimTalepNumaram olarak " + BenimTalepNo + " nolu talep oluşturuldu");
    StoreHelper.INSTANCE.saveValue(BenimTalepNumaram, BenimTalepNo);
    logger.info(BenimTalepNo + "  BenimTalepNumaram olarak kaydedildi");
    return BenimTalepNumaram;

  }


  @Step("Tarih olarak günün tarihinden <gun> gün sonrasını seç")
  public void implementation2(String gun) throws InterruptedException {
    String key = "Gun_Seçimi";
    int keyint = Integer.parseInt(gun);

    WebElement calenderButton = driver.findElement(By.xpath("//span[@class='k-icon k-i-calendar']"));
    calenderButton.click();
    String todayString = getCurrentDay();
    int todayint = Integer.parseInt(todayString);
    List<WebElement> allWorkingDays = findElements(key);
    Thread.sleep(3000);
    if (todayint + keyint > 30 || todayint == 31 || keyint >= allWorkingDays.size()) {
      javaScriptClicker(driver, driver.findElement(By.cssSelector(".k-link.k-nav-next")));
    }

    List<WebElement> newallWorkingDays = findElements(key);
    Thread.sleep(3000);
    WebElement clickDayElement = newallWorkingDays.get(keyint);

    javascriptclicker(clickDayElement);
  }

  private String getCurrentDay() {
    //Create a Calendar Object
    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
    //Get Current Day as a number
    int todayInt = calendar.get(Calendar.DAY_OF_MONTH);
    //Integer to String Conversion
    String todayStr = Integer.toString(todayInt);
    return todayStr;
  }

  @Step("<key> elementine javascript ile tıkla")
  public void elementeJSileTikla(String key) {
    WebElement element = findElement(key);
    javascriptclicker(element);
    logger.info(key + " elementine javascript ile tıklandı");
  }


  public void scrollToElementToBeVisiblest(WebElement webElement) {
    if (webElement != null) {
      scrollTo(webElement.getLocation().getX(), webElement.getLocation().getY() - 100);
    }
  }


  public void doubleclick(WebElement elementLocator) {
    Actions actions = new Actions(driver);
    actions.doubleClick(elementLocator).perform();
  }

  public void elementinibul(WebElement webElement) {
    int loopCount = 0;
    while (loopCount < DEFAULT_MAX_ITERATION_COUNT) {
      try {

      } catch (WebDriverException e) {
      }
      loopCount++;
      waitByMilliSeconds(DEFAULT_MILLISECOND_WAIT_AMOUNT);
    }
  }

  @Step("<key> alanını javascript ile temizle")
  public void clearWithJS(String key) {
    WebElement element = findElement(key);
    ((JavascriptExecutor) driver).executeScript("arguments[0].value ='';", element);

  }


  @Step("<key> elementleri arasından <text> kayıtlı değişkene tıkla")
  public void clickParticularElement(String key, String text) {

    System.out.println("minanananan"+text);
    List<WebElement> anchors = findElements(key);
    Iterator<WebElement> i = anchors.iterator();
    while (i.hasNext()) {
      WebElement anchor = i.next();
      if (anchor.getText().contains(StoreHelper.INSTANCE.getValue(text))) {
        scrollToElementToBeVisiblest(anchor);
        doubleclick(anchor);
        break;
      }
    }
  }

  @Step("<key> menu listesinden rasgele seç")
  public void listedenRasgeleTikla(String key)
  {
    for (int i=0;i<3;i++)
      randomSec(key);
  }

    @Step("<key> olarak <text> seçersemm")
    public void olarakSecersemm(String key, String text){

      List<WebElement> anchors = findElements(key);
      Iterator<WebElement> i = anchors.iterator();
      while (i.hasNext()) {
        WebElement anchor = i.next();
        if (anchor.getText().contains(text)) {
          anchor.click();
          break;
        }
      }
    }

  @Step("<Talep_durumu> olarak <index> indexi seçersem")
  public void olarakIndexiSecersemm(String key,String index){

    List<WebElement> anchors = findElements(key);
    WebElement anchor =anchors.get(Integer.parseInt(index));
    anchor.click();
  }

  @Step("<key> elementiyle <cardDetail> elemetindeki <keyToCompare> textini karsilastir")
  public void AracTalepleriTalepNumarasıKarsilastir(String key, String cardDetail, String keyToCompare) throws InterruptedException {
    WebElement webelement = findElement(key);
    String cardTalepNo= webelement.getText();
    logger.info("web element" +cardTalepNo + " texti bulundu ");
    cardTalepNo= cardTalepNo.substring(0,12);
    logger.info(cardTalepNo + " texti bulundu ");
    WebElement webelement1= findElement(cardDetail);
    webelement1.click();
    logger.info( " tıklandı bulundu ");
    Thread.sleep(4000);
    WebElement detail = findElement(keyToCompare);
    String detailPage= detail.getText();
    String compareText =detailPage.substring(16,28);
    Assert.assertTrue(cardTalepNo.equals(compareText));
    logger.info(cardTalepNo + " textiyle " + compareText + " texti karşılaştırıldı.");
  }

  @Step("<firstCard> in icerdiği <key> elementiyle <keyToCompare> elementini seferlerim icin karsilastir")
  public void SeferlerimTalepNumarasıKarsilastir(String firstCard,String key, String keyToCompare) throws InterruptedException {
    WebElement IlkKartElement=findElement(firstCard);
    WebElement webElement = findElement(key);
    String firstCardTalepNo= webElement.getText();
    logger.info(firstCardTalepNo + " texti bulundu");
    firstCardTalepNo= firstCardTalepNo.substring(9,19);
    logger.info(firstCardTalepNo + " texti bulundu");
    IlkKartElement.click();
    logger.info( " tıklandı bulundu ");
    Thread.sleep(4000);
    WebElement cardDetail = findElement(keyToCompare);
    String detailPage= cardDetail.getText();
    String compareText =detailPage.substring(14,24);
    Assert.assertTrue(firstCardTalepNo.equals(compareText));
    logger.info(firstCardTalepNo + " textiyle " + compareText + " texti karşılaştırıldı.");
  }

  @Step("Siparis durmununu <kartDurumu> elementinden bul")
  public void SiparisDurumuKarsilastir(String kartDurumu) throws InterruptedException {
    WebElement webElement = findElement(kartDurumu);
    logger.info(  " webelement bulundu");
    compareText= webElement.getText();
    logger.info(compareText + " texti bulundu");
  }

  @Step("<key> elementiyle karsilastir")
  public void Karsilastir(String key) throws InterruptedException {
    WebElement cardDetail = findElement(key);
    String tedarikDurumuDetay= cardDetail.getText();
    logger.info(tedarikDurumuDetay +" texti bulundu");
    Assert.assertTrue(compareText.equals(tedarikDurumuDetay));
    logger.info(compareText + " textiyle " + tedarikDurumuDetay + " texti karşılaştırıldı.");
  }

  @Step("<text> textini <key> elemente tek tek yaz")
  public void sendKeyOneByOne(String text, String key) throws InterruptedException {

    WebElement field = findElement(key);
    field.clear();
    if (!key.equals("")) {
      for (char ch: text.toCharArray())
      findElement(key).sendKeys(Character.toString(ch));
      Thread.sleep(10);
      logger.info(key + " elementine " + text + " texti karakterler tek tek girlilerek yazıldı.");
    }
  }

  /*@Step("<key> elementine 1,00 değerini js ile yaz")
  public void elementeJSileYaz(String key)
  {
    //findElement(key);
    JavascriptExecutor jse = (JavascriptExecutor)driver;
    jse.executeScript("arguments[0].value='1,00';",findElement(key));
  }*/

  @Step("<key> elementine <text> değerini js ile yaz")
  public void elementeJSileYaz(String key,String text)
  {
    WebElement element = findElement(key);
    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript("arguments[0].value=arguments[1]",element,text);
    logger.info(key+" elementine "+text+" değeri js ile yazıldı.");
  }


  public String tarihSec() {
    Calendar now= Calendar.getInstance();
    // Calendar simdi = Calendar.getInstance();
    int tarih = now.get(Calendar.DATE) + 2;
    // now.set(Calendar.DATE,tarih+2);
    return String.valueOf(tarih);
  }

  @Step("<key> tarihinden 2 gün sonraya al")
  public void tarihAl(String key) {
    List<WebElement> elements = findElements(key);
    for (int i = 0; i < elements.size(); i++) {
      if (elements.get(i).getText().equals(tarihSec())) {
        elements.get(i).click();
      }
    }
  }



}









