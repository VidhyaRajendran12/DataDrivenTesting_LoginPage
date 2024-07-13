package testCase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class DataDrivenLoginTest {

	@DataProvider
	public Object[][] dataProvideMethod() throws IOException {
		File file = new File("C:\\myPlace\\DataProviderProject\\InputFile\\LoginTest.xlsx");
		FileInputStream fis = new FileInputStream(file);
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFSheet sheet = workbook.getSheetAt(0);

		int rowCount = sheet.getPhysicalNumberOfRows();
		int columnCount = sheet.getRow(0).getPhysicalNumberOfCells();

		Object[][] data = new Object[rowCount - 1][columnCount];

		for (int i = 1; i < rowCount; i++) {
			XSSFRow row = sheet.getRow(i);
			for (int j = 0; j < columnCount; j++) {
				XSSFCell cell = row.getCell(j);
				data[i - 1][j] = cell.getStringCellValue();
			}
		}

		workbook.close();
		fis.close();
		return data;
	}

	@Test(dataProvider = "dataProvideMethod")
	void LoginTest(String username, String password) {

		WebDriverManager.chromedriver().setup();
		WebDriver driver = new ChromeDriver();

		try {
			driver.get("https://id.heroku.com/login");

			WebElement usernameInput = driver.findElement(By.id("email"));
			usernameInput.sendKeys(username);

			WebElement passwordInput = driver.findElement(By.id("password"));
			passwordInput.sendKeys(password);

			driver.findElement(By.xpath("//button[@type='submit']")).click();

			// Capture screenshot
			TakesScreenshot ts = (TakesScreenshot) driver;
			File screenshotFile = ts.getScreenshotAs(OutputType.FILE);

			// Ensure directory exists
			File screenshotsDir = new File("C:\\myPlace\\DataProviderProject\\ScreenShot\\");
			screenshotsDir.mkdirs();

			Date currentDate = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
			String formattedDate = dateFormat.format(currentDate); // Get System Data and Time
			String screenshotName = "Screenshot_" + username + "_" + formattedDate + ".png";

			// Save screenshot to the specified location
			FileUtils.copyFile(screenshotFile, new File(screenshotsDir, screenshotName));

			System.out.println("Screenshot captured: " + screenshotName);
		}

		catch (Exception e) {
			System.out.println("Exception occurred: " + e.getMessage());
		}

		finally {

			driver.quit();

		}
	}

}
