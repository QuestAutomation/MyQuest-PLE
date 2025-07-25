package com.simplifyqa.codeeditor;

import com.simplifyqa.abstraction.driver.IQAWebDriver;
import com.simplifyqa.pluginbase.argument.IArgument;
import com.simplifyqa.pluginbase.codeeditor.annotations.AutoInjectCurrentObject;
import com.simplifyqa.pluginbase.codeeditor.annotations.AutoInjectWebDriver;
import com.simplifyqa.pluginbase.codeeditor.annotations.SyncAction;
import com.simplifyqa.pluginbase.common.enums.TechnologyType;
import com.simplifyqa.pluginbase.common.models.Attribute;
import com.simplifyqa.pluginbase.common.models.Configuration;
import com.simplifyqa.pluginbase.common.models.SqaObject;
import com.simplifyqa.pluginbase.plugin.annotations.ObjectTemplate;
import com.simplifyqa.pluginbase.plugin.execution.SubStep;
import com.simplifyqa.pluginbase.plugin.execution.models.Status;
import com.simplifyqa.web.base.search.FindBy;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONArray;

public class SampleClass {
  @AutoInjectWebDriver
  private IQAWebDriver driver;
  
  @AutoInjectCurrentObject
  private SqaObject currentObject;
  
  @AutoInjectCurrentObject
  FindBy find;
  
  private static final Logger log = Logger.getLogger(SampleClass.class.getName());
  
  public String getAttributeValue(String name) {
    try {
      List<Attribute> attributes = this.currentObject.attributes();
      for (Attribute attribute : attributes) {
        if (attribute.name().equals(name))
          return attribute.value(); 
      } 
    } catch (Exception e) {
      this.driver.getExecutionLogReporter().error(e.toString());
    } 
    return null;
  }
  
  @SyncAction(uniqueId = "MyProject-Sample-001", groupName = "Assertions", description = "Compares 2 Strings", objectTemplate = @ObjectTemplate(name = TechnologyType.GENERIC, description = "This action belongs to Generic"))
  public boolean compareStringValues(String actual, String expected) {
    boolean bstatus = false;
    try {
      if (actual.trim().equalsIgnoreCase(expected.trim())) {
        bstatus = true;
        this.driver.getExecutionLogReporter().info(actual + " is equal to " + actual);
      } else {
        bstatus = false;
        this.driver.getExecutionLogReporter().info(actual + " is not equal to " + actual);
      } 
    } catch (Exception e) {
      this.driver.getExecutionLogReporter().error(e.toString());
      bstatus = false;
    } 
    return bstatus;
  }
  
  @SyncAction(uniqueId = "MyProject-Sample-002", groupName = "File Handlers", description = "Gets Downloaded File Path", objectTemplate = @ObjectTemplate(name = TechnologyType.GENERIC, description = "This action belongs to Generic"))
  public boolean getDownloadedFilePath(IArgument downloadPath) {
    boolean bStatus = false;
    try {
      String dirPath = this.driver.getConfiguration().DOWNLOAD_PATH();
      Configuration configuration = this.driver.getConfiguration();
      Thread.sleep(1000L);
      this.driver.launchApplication("chrome://downloads");
      String a = this.driver.captureScreenshot();
      System.out.println(a);
      String fileName = this.driver.executeScript("return document.querySelector('downloads-manager').shadowRoot.querySelector('#downloadsList downloads-item').shadowRoot.querySelector('div#content #file-link').text", new Object[0]).toString();
      dirPath = dirPath + "\\" + dirPath;
      downloadPath.updateValue(dirPath);
      this.driver.back();
      Thread.sleep(2000L);
      bStatus = true;
    } catch (Exception e) {
      this.driver.getExecutionLogReporter().error(e.toString());
      bStatus = false;
    } 
    return bStatus;
  }
  
  @SyncAction(uniqueId = "MyProject-Sample-003", groupName = "Date Handlers", objectTemplate = @ObjectTemplate(name = TechnologyType.WEB, description = "This action belongs to WEB"))
  public boolean questDatePicker(String numberString, String format, IArgument store) {
    boolean bStatus = false;
    String xpath = getAttributeValue("xpath");
    try {
      SimpleDateFormat sdf = new SimpleDateFormat(format);
      Calendar calendar = Calendar.getInstance();
      int number = Integer.parseInt(numberString);
      calendar.add(5, number);
      String date = sdf.format(calendar.getTime());
      store.updateValue(date);
      this.driver.findElement(FindBy.xpath(xpath)).click();
      Thread.sleep(2000L);
      this.driver.findElement(FindBy.xpath("//span[contains(@id,'mat-calendar-button')]")).click();
      Thread.sleep(2000L);
      String[] date1 = date.split("/");
      String year = date1[2];
      this.driver.findElement(FindBy.xpath("//tr[@role='row']/td/div[normalize-space(text())='" + year + "']")).click();
      Thread.sleep(2000L);
      String month = date1[1];
      this.driver.findElement(FindBy.xpath("(//tr[@role    ='row']/td/div[1])[" + month + "]")).click();
      Thread.sleep(2000L);
      String datevalue = date1[0];
      this.driver.findElement(FindBy.xpath("(//tr[@role='row']/td/div[1])[" + datevalue + "]")).click();
      Thread.sleep(2000L);
      this.driver.getExecutionLogReporter().addTestData("Date", date);
      bStatus = true;
    } catch (Exception e) {
      this.driver.getExecutionLogReporter().error(e.toString());
      bStatus = false;
    } 
    return bStatus;
  }
  
  @SyncAction(uniqueId = "MyProject-Sample-004", groupName = "Get and Store", description = "Stores the Element Value", objectTemplate = @ObjectTemplate(name = TechnologyType.WEB, description = "This action belongs to Web"))
  public boolean getValue(IArgument value) {
    boolean bstatus = false;
    try {
      String xpath = getAttributeValue("xpath");
      String sh = this.driver.executeScript("function getElementByXpath(path) {return document.evaluate(path, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;}var a = getElementByXpath(\"" + xpath + "\");return a.value;", new Object[0]).toString();
      this.driver.getExecutionLogReporter().reportSubStep(Status.PASSED, "executeScript", List.of(xpath));
      SubStep sa = SubStep.builder().status(Status.PASSED).description("xpath").testDataValues(List.of(sh, sh)).build();
      this.driver.getExecutionLogReporter().reportSubStep(sa);
      value.updateValue(sh);
      bstatus = true;
    } catch (Exception e) {
      this.driver.getExecutionLogReporter().error(e.toString());
      bstatus = false;
    } 
    return bstatus;
  }
  
  @SyncAction(uniqueId = "MyProject-Sample-005", groupName = "Get and Store", description = "Get Elements Count", objectTemplate = @ObjectTemplate(name = TechnologyType.WEB, description = "This action belongs to Web"))
  public boolean getElementCount(IArgument store) {
    boolean bStatus = false;
    try {
      String xpath = getAttributeValue("xpath");
      int lenCount = this.driver.findElements(FindBy.xpath(xpath)).size();
      store.updateValue("" + lenCount);
      this.driver.getExecutionLogReporter().info("" + lenCount);
      bStatus = true;
    } catch (Exception e) {
      this.driver.getExecutionLogReporter().error(e.toString());
      bStatus = false;
    } 
    return bStatus;
  }
  
  @SyncAction(uniqueId = "MyProject-Sample-006", groupName = "Assertions", description = "Reads the content in the email body", objectTemplate = @ObjectTemplate(name = TechnologyType.GENERIC, description = "This action belongs to Generic"))
  public boolean readEmailContent(String clientId, String tenantId, String clientSecret, String userEmail, String subject, IArgument store) throws IOException, JSONException {
    boolean bstatus = false;
    try {
      String accessToken = getAccessToken(clientId, tenantId, clientSecret);
      String emailContent = getEmailContent(userEmail, subject, accessToken);
      store.updateValue(emailContent);
      this.driver.getExecutionLogReporter().info("Value = " + emailContent);
      bstatus = true;
    } catch (Exception e) {
      bstatus = false;
    } 
    return bstatus;
  }
  
  private static String getAccessToken(String clientId, String tenantId, String clientSecret) throws IOException, JSONException {
    String tokenUrl = String.format("https://login.microsoftonline.com/%s/oauth2/v2.0/token", new Object[] { tenantId });
    String params = "grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret + "&scope=https://graph.microsoft.com/.default";
    HttpsURLConnection conn = (HttpsURLConnection)(new URL(tokenUrl)).openConnection();
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    conn.setDoOutput(true);
    Exception exception1 = null, exception2 = null;
    try {
      OutputStream os = conn.getOutputStream();
      try {
        os.write(params.getBytes());
        os.flush();
      } finally {
        if (os != null)
          os.close(); 
      } 
    } finally {
      exception2 = null;
      if (exception1 == null) {
        exception1 = exception2;
      } else if (exception1 != exception2) {
        exception1.addSuppressed(exception2);
      } 
    } 
    InputStream is = conn.getInputStream();
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    StringBuilder response = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null)
      response.append(line); 
    reader.close();
    JSONObject jsonResponse = new JSONObject(response.toString());
    String accessToken = jsonResponse.getString("access_token");
    return accessToken;
  }
  
  private static String getEmailContent(String userEmail, String subject, String accessToken) throws IOException, JSONException {
    String url = String.format("https://graph.microsoft.com/v1.0/users/%s/messages", new Object[] { userEmail });
    HttpsURLConnection conn = (HttpsURLConnection)(new URL(url)).openConnection();
    conn.setRequestMethod("GET");
    conn.setRequestProperty("Authorization", "Bearer " + accessToken);
    conn.setRequestProperty("Accept", "application/json");
    int responseCode = conn.getResponseCode();
    if (responseCode == 200) {
      JSONArray messages = new JSONArray();
      StringBuilder response = new StringBuilder();
      Exception exception1 = null, exception2 = null;
      try {
        Scanner scanner = new Scanner(new InputStreamReader(conn.getInputStream()));
        try {
          while (scanner.hasNext()) {
            System.out.println("*");
            response.append(scanner.nextLine());
          } 
        } finally {
          if (scanner != null)
            scanner.close(); 
        } 
      } finally {
        exception2 = null;
        if (exception1 == null) {
          exception1 = exception2;
        } else if (exception1 != exception2) {
          exception1.addSuppressed(exception2);
        } 
      } 
      return "No email found with the specified subject.";
    } 
    throw new IOException("Failed to get email content: " + conn.getResponseMessage());
  }
  
  @SyncAction(uniqueId = "MyProject-Sample-007", groupName = "Assertions", description = "Verify the Ascending Sort Functionality", objectTemplate = @ObjectTemplate(name = TechnologyType.WEB, description = "Verify the Ascending Sort Functionality [object_name]"))
  public boolean verifyAscendingSort() throws InterruptedException {
    boolean bstatus = false;
    try {
      String rowXpath = getAttributeValue("xpath");
      String nextPage = "//button[@aria-label='Next page']";
      int lenCount = this.driver.findElements(FindBy.xpath(rowXpath)).size();
      List<String> strings = new ArrayList<>();
      for (int i = 1; i <= lenCount; i++) {
        String s1 = "(" + rowXpath + ")[" + i + "]";
        String sh = this.driver.executeScript("function getElementByXpath(path) {return document.evaluate(path, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;}var a = getElementByXpath(\"" + s1 + "\");return a.innerText;", new Object[0]).toString().trim().toLowerCase();
        strings.add(sh);
      } 
      String en = this.driver.executeScript("function getElementByXpath(path) {return document.evaluate(path, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;}var a = getElementByXpath(\"" + nextPage + "\");return a.disabled;", new Object[0]).toString().toLowerCase();
      while (en.contentEquals("false")) {
        this.driver.findElement(FindBy.xpath(nextPage)).click();
        Thread.sleep(2000L);
        int lenCount1 = this.driver.findElements(FindBy.xpath(rowXpath)).size();
        for (int j = 1; j <= lenCount1; j++) {
          String s1 = "(" + rowXpath + ")[" + j + "]";
          String sh = this.driver.executeScript("function getElementByXpath(path) {return document.evaluate(path, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;}var a = getElementByXpath(\"" + s1 + "\");return a.innerText;", new Object[0]).toString().trim().toLowerCase();
          strings.add(sh);
        } 
        en = this.driver.executeScript("function getElementByXpath(path) {return document.evaluate(path, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;}var a = getElementByXpath(\"" + nextPage + "\");return a.disabled;", new Object[0]).toString().toLowerCase();
      } 
      List<String> sortedStrings = new ArrayList<>(strings);
      Collections.sort(sortedStrings);
      Collections.reverse(sortedStrings);
      Collections.reverse(sortedStrings);
      if (strings.equals(sortedStrings)) {
        this.driver.getExecutionLogReporter().info("Sort Functionality passed");
        bstatus = true;
      } else {
        this.driver.getExecutionLogReporter().info("Sort Functionality failed");
        bstatus = false;
      } 
    } catch (Exception e) {
      bstatus = false;
      this.driver.getExecutionLogReporter().error(e.toString());
    } 
    return bstatus;
  }
  
  @SyncAction(uniqueId = "MyProject-Sample-008", groupName = "Assertions", description = "Verify the Ascending Sort Functionality", objectTemplate = @ObjectTemplate(name = TechnologyType.WEB, description = "This action belongs to Web"))
  public boolean verifyNumberAscendingSort() throws InterruptedException {
    boolean bstatus = false;
    try {
      String rowXpath = getAttributeValue("xpath");
      String nextPage = "//button[@aria-label='Next page']";
      int lenCount = this.driver.findElements(FindBy.xpath(rowXpath)).size();
      List<String> strings = new ArrayList<>();
      for (int i = 1; i <= lenCount; i++) {
        String s1 = "(" + rowXpath + ")[" + i + "]";
        String sh = this.driver.executeScript("function getElementByXpath(path) {return document.evaluate(path, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;}var a = getElementByXpath(\"" + s1 + "\");return a.innerText;", new Object[0]).toString().trim().toLowerCase();
        strings.add(sh);
      } 
      String en = this.driver.executeScript("function getElementByXpath(path) {return document.evaluate(path, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;}var a = getElementByXpath(\"" + nextPage + "\");return a.disabled;", new Object[0]).toString().toLowerCase();
      while (en.contentEquals("false")) {
        this.driver.findElement(FindBy.xpath(nextPage)).click();
        Thread.sleep(2000L);
        int lenCount1 = this.driver.findElements(FindBy.xpath(rowXpath)).size();
        for (int j = 1; j <= lenCount1; j++) {
          String s1 = "(" + rowXpath + ")[" + j + "]";
          String sh = this.driver.executeScript("function getElementByXpath(path) {return document.evaluate(path, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;}var a = getElementByXpath(\"" + s1 + "\");return a.innerText;", new Object[0]).toString().trim().toLowerCase();
          strings.add(sh);
        } 
        en = this.driver.executeScript("function getElementByXpath(path) {return document.evaluate(path, document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue;}var a = getElementByXpath(\"" + nextPage + "\");return a.disabled;", new Object[0]).toString().toLowerCase();
      } 
      List<String> stringList = new ArrayList<>(strings);
      List<Integer> intList = new ArrayList<>();
      for (String str : stringList)
        intList.add(Integer.valueOf(Integer.parseInt(str))); 
      List<Integer> sortedStrings = new ArrayList<>(intList);
      Collections.sort(sortedStrings);
      if (intList.equals(sortedStrings)) {
        this.driver.getExecutionLogReporter().info("Sort Functionality passed");
        bstatus = true;
      } else {
        this.driver.getExecutionLogReporter().info("Sort Functionality failed");
        bstatus = false;
      } 
    } catch (Exception e) {
      bstatus = false;
      this.driver.getExecutionLogReporter().error(e.toString());
    } 
    return bstatus;
  }
  
  @SyncAction(uniqueId = "MyProject-Sample-009", groupName = "Assertions", description = "Verifies Duplicates In Dropdown", objectTemplate = @ObjectTemplate(name = TechnologyType.WEB, description = "Verify no duplicates in the dropdown"))
  public boolean verifyNoDuplicatesInDropdown() {
    boolean bstatus = false;
    try {
      String xpath = getAttributeValue("xpath");
      this.driver.findElement(FindBy.xpath(xpath)).click();
      String xpath1 = "//li[@role='option']/span";
      int lenCount = this.driver.findElements(FindBy.xpath(xpath1)).size();
      LinkedHashSet<String> hashSet = new LinkedHashSet<>();
      for (int i = 1; i <= lenCount; i++) {
        String xpath2 = "(//li[@role='option']/span)[" + i + "]";
        String a = this.driver.findElement(FindBy.xpath(xpath2)).getText();
        hashSet.add(a);
      } 
      int newlen = hashSet.size();
      if (lenCount == newlen) {
        log.log(Level.SEVERE, "No Duplicates Found");
        this.driver.getExecutionLogReporter().info("No Duplicates Found");
        bstatus = true;
      } else {
        log.log(Level.SEVERE, "Duplicates Found");
        this.driver.getExecutionLogReporter().info("Duplicates Found");
        bstatus = false;
      } 
    } catch (Exception e) {
      this.driver.getExecutionLogReporter().error(e.toString());
      bstatus = false;
    } 
    return bstatus;
  }
  
  @SyncAction(uniqueId = "MyProject-Sample-010", groupName = "Assertions", description = "Verify no duplicates in the dropdown", objectTemplate = @ObjectTemplate(name = TechnologyType.WEB, description = "Verify no duplicates in the dropdown"))
  public boolean verifyNoDuplicatesInCheckboxDropdown() {
    boolean bstatus = false;
    try {
      String xpath = getAttributeValue("xpath");
      this.driver.findElement(FindBy.xpath(xpath)).click();
      String xpath1 = "//li[@class='p-ripple p-element p-multiselect-item']";
      int lenCount = this.driver.findElements(FindBy.xpath(xpath1)).size();
      LinkedHashSet<String> hashSet = new LinkedHashSet<>();
      for (int i = 1; i <= lenCount; i++) {
        String xpath2 = "(//li[@class='p-ripple p-element p-multiselect-item']//span[@class='ng-star-inserted'])[" + i + "]";
        String a = this.driver.findElement(FindBy.xpath(xpath2)).getText();
        hashSet.add(a);
      } 
      int newlen = hashSet.size();
      if (lenCount == newlen) {
        log.log(Level.SEVERE, "No Duplicates Found");
        this.driver.getExecutionLogReporter().info("No Duplicates Found");
        bstatus = true;
      } else {
        log.log(Level.SEVERE, "Duplicates Found");
        this.driver.getExecutionLogReporter().info("Duplicates Found");
        bstatus = false;
      } 
    } catch (Exception e) {
      this.driver.getExecutionLogReporter().error(e.toString());
      bstatus = false;
    } 
    return bstatus;
  }
  
  @SyncAction(uniqueId = "MyProject-Sample-011", groupName = "Assertions", description = "Validate Batch Count", objectTemplate = @ObjectTemplate(name = TechnologyType.ANDROID, description = "This action belongs to ANDROID"))
  public boolean validateBatchesCount(String pre_BCount, String operation) {
    try {
      String[] arr = pre_BCount.split(" ");
      char digit = arr[1].charAt(1);
      String Count = "" + digit;
      int pre_Count = Integer.parseInt(Count);
      int added_BCount = pre_Count + 1;
      int deleted_BCount = pre_Count - 1;
      if (operation.equalsIgnoreCase("Adding")) {
        if (added_BCount > pre_Count)
          return true; 
      } else if (operation.equalsIgnoreCase("Deleting") && 
        deleted_BCount < pre_Count) {
        return true;
      } 
      return true;
    } catch (Exception e) {
      return false;
    } 
  }
}
