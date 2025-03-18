import java.time.Duration;
import java.util.HashMap;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class FortinetTest {
	//few variables useful during the execution
	static WebDriver driver;
	static WebDriverWait wa;
	static String productName, password = null;
	static long startTime, endTime, elapsedTime;
	static HashMap<String, String> mapUsers;

	public static void main(String[] args) {
		//Main class where the execution starts
		setupEnv();
		getCredentials();
		login(mapUsers.get("standard"), password);
		clickElement("SortLTH");
		clickElement("1stAddToCart");
		clickElement("GoToCart");
		clickElement("RemoveButton");
		clickElement("ContinueShoppingButton");
		clickElement("2ndAddToCart");
		productName = driver.findElement(getLocator("2ndProductName")).getText();
		clickElement("GoToCart");
		boolean verifyCart = driver.findElement(getLocator("VerifyProductNameInCart")).isDisplayed();
		System.out.println("Cart Verification is: "+verifyCart);
		clickElement("CheckoutButton");
		sendKeys("FirstName", "Katy");
		sendKeys("LastName", "Lock");
		sendKeys("PostalCode", "V2Z5G6");
		clickElement("ContinueButton");
		clickElement("FinishButton");
		logout();
		login(mapUsers.get("locked"), password, "withError");
		login(mapUsers.get("performance"), password);
		elapsedTime = endTime - startTime ;
		System.out.println("Page Load Time (PerformanceGlitchUser): " + elapsedTime + " ms");
		logout();
		login(mapUsers.get("standard"), password);
		elapsedTime = endTime - startTime ;
		System.out.println("Page Load Time (StandardUser): " + elapsedTime + " ms");
		logout();
		closeSession();	
	}
	
	public static void setupEnv() {
		//Opening Target URL and setting browser size and Implicit wait
		System.out.println("Session Started...");
		driver = new ChromeDriver();
		driver.get("https://www.saucedemo.com/");
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		driver.manage().window().maximize();
		wa = new WebDriverWait(driver, Duration.ofSeconds(30));
	}
	
	public static void getCredentials() {
		//get all the credentials on the runtime. this should work given the logic remains intact
		String strRawUsers = driver.findElement(getLocator("AllUserList")).getText();
		String strRawPwds = driver.findElement(getLocator("AllPasswordList")).getText();
		String strUsers = (strRawUsers.split(":")[1]).trim();
		String strPwds = (strRawPwds.split(":")[1]).trim();
		String[] strUserList = strUsers.split("\n");
		password = strPwds.split("\n")[0];
		mapUsers = new HashMap<>();
		for (String str : strUserList) {
			String key = str.split("_")[0];
			mapUsers.put(key, str);				
		}
	}
		
	public static void login(String userName, String password) {
		//login to application without credentials error expectation  with validation
		By by2ndPrdAddButton = getLocator("2ndAddToCart");
		sendKeys("UserName", userName);
		sendKeys("Password", password);
		startTime = System.currentTimeMillis();
		clickElement("LoginButton");
		wa.until(ExpectedConditions.elementToBeClickable(by2ndPrdAddButton));
		endTime = System.currentTimeMillis();
		System.out.println("Logged IN Successfully!!!");			
	}

	public static void login(String userName, String password, String wError) {
		//login to application with expected credentials error  with validation
		By byError = getLocator("LoginError");
		sendKeys("UserName", userName);
		sendKeys("Password", password);
		clickElement("LoginButton");
		wa.until(ExpectedConditions.visibilityOfElementLocated(byError));
		String strError = driver.findElement(byError).getText();
		System.out.println("Login Error is: " + strError);
		System.out.println("Login Tried Successfully!!!");
	}
	
	public static void logout() {
		//logout from application with validation
		clickElement("UserOptions");
		clickElement("Logout");
		wa.until(ExpectedConditions.elementToBeClickable(getLocator("LoginButton")));
		System.out.println("Logged OUT Successfully!!!");
	}

	public static void clickElement(String strLocator) {
		//click on a WebElement
		By by = getLocator(strLocator);
		wa.until(ExpectedConditions.elementToBeClickable(by));
		driver.findElement(by).click();
		System.out.println("Clicked Element: "+by);
	}

	public static void sendKeys(String strLocator, String data) {
		//enter data into any field
		By by = getLocator(strLocator);
		driver.findElement(by).clear();
		driver.findElement(by).sendKeys(data);
		System.out.println("Data entered in element: "+data+ " in "+by);
	}

	public static By getLocator(String props) {
		//maintain and retrieve locators as necessary and easy to manage way
		switch (props) {
		
		case "AllUserList":
			return By.id("login_credentials");
		case "AllPasswordList":
			return By.className("login_password");
		case "UserName":
			return By.xpath("//input[@id=\"user-name\"]");
		case "Password":
			return By.xpath("//input[@id=\"password\"]");
		case "LoginButton":
			return By.id("login-button");
		case "LoginError":
			return By.xpath("//*[starts-with(text(),'Epic sadface')]");
		case "SortLTH":
			return By.xpath("//select[@class=\"product_sort_container\"]/option[text()=\"Price (low to high)\"]");
		case "1stAddToCart":
			return By.xpath("//*[@class=\"inventory_list\"]/div[1]//button");
		case "2ndAddToCart":
			return By.xpath("//*[@class=\"inventory_list\"]/div[2]//button");
		case "GoToCart":
			return By.xpath("//a[@class=\"shopping_cart_link\"]");
		case "RemoveButton":
			return By.xpath("//button[text()=\"Remove\"]");
		case "ContinueShoppingButton":
			return By.xpath("//button[text()=\"Continue Shopping\"]");
		case "2ndProductName":
			return By.xpath("//*[@class=\"inventory_list\"]/div[2]/div[2]//a/div");
		case "VerifyProductNameInCart":
			return By.xpath("//*[text()='"+productName+"']");
		case "CheckoutButton":
			return By.xpath("//button[text()=\"Checkout\"]");
		case "FirstName":
			return By.id("first-name");
		case "LastName":
			return By.id("last-name");
		case "PostalCode":
			return By.id("postal-code");
		case "ContinueButton":
			return By.id("continue");
		case "FinishButton":
			return By.id("finish");
		case "UserOptions":
			return By.id("react-burger-menu-btn");
		case "Logout":
			return By.xpath("//a[text()=\"Logout\"]");
		default:
			System.out.println("Locator NOT Found: "+props);
			return null;
		}
	}
	
	public static void closeSession() {
		//close session and close/quit browser session
		driver.quit();
		System.out.println("Session Terminated...");
		System.out.println("Thank you...");
	}
}