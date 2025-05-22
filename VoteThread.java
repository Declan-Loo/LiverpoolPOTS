import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.JavascriptExecutor;
import java.time.Duration;
import java.util.Random;
import java.util.List;

class VoteThread implements Runnable {
  private final int threadId;
  private Random random = new Random();
  private static volatile boolean shouldStop = false;
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
  private List<String> voteReasons = List.of("This means more", "Best RB on the pitch", "Absolute legend", "YNWA", "What a player", "Liverpool legend", "It means more to us", "The scouser in the team", "He's Alexander Arnold", "Corner taken quickly", "Warra legend", "Warra baller","Legend","66","I will miss him");

  private String getTimestamp() {
    return "[" + LocalDateTime.now().format(formatter) + "]";
  }

  public VoteThread(int threadId){
    this.threadId = threadId;
  }

  public static void stopAllThreads() {
    shouldStop = true;
  }

  private String generateRandomName() {
    String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    int length = random.nextInt(18) + 6; // Random length 6-23
    StringBuilder result = new StringBuilder();
    
    for (int i = 0; i < length; i++) {
        result.append(chars.charAt(random.nextInt(chars.length())));
    }
    
    return result.toString();
  }


  public void run() {
    System.out.println(getTimestamp() + " Thread " + threadId + " has started");
    int counter = 0;
    while(!shouldStop){
      WebDriver driver = null;
      try{
        counter++; // INCREMENT COUNTER HERE
        // initialise webdriver
        System.setProperty("webdriver.chrome.driver", "./chromedriver");
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless"); // Optional: run headless
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable-dev-shm-usage");

        // go to the link
        driver = new ChromeDriver(chromeOptions);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.get("https://matchcentre.liverpoolfc.com/competitions/index.html?h=cdn.monterosa.cloud&p=30dfcd4e-b319-4e6a-82df-9188b181ca73&e=ab2a8bd9-a890-4d02-970b-a184ac252120#/");
        
        if(shouldStop) break;

        // Wait for page to load completely
        Thread.sleep(950);

        // Step 1: Navigate swiper to find Alexander-Arnold
        WebElement playerCard = findAlexanderArnoldCard(driver, wait, threadId);
        
        if(shouldStop) break;
        
        playerCard.click();

        if(shouldStop) break;
        
        // Step 2: Click SUBMIT button (first submit)
        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//button[contains(text(), 'SUBMIT')]")));
        submitButton.click();
        
        if(shouldStop) break;
        
        // Step 3: Fill out the form
        // Fill "Your name" field
        WebElement nameField = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.id("smb-id-7")));
        nameField.clear();
        nameField.sendKeys(generateRandomName());
        
        // Fill "Chosen player" field
        WebElement chosenPlayerField = driver.findElement(By.id("smb-id-9"));
        chosenPlayerField.clear();
        chosenPlayerField.sendKeys("Trent Alexander-Arnold");
        
        // Fill "Explain your vote" textarea
        WebElement explainVoteField = driver.findElement(By.id("smb-id-11"));
        explainVoteField.clear();
        explainVoteField.sendKeys(voteReasons.get(random.nextInt(voteReasons.size())));
        
        if(shouldStop) break;

        // Step 4: Submit the form
        WebElement formSubmitButton = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//span[@class='smb-btn smb-forward' and text()='Submit']")));
        formSubmitButton.click();
        System.out.println(getTimestamp() + " Thread " + threadId + " has voted for 66. Alexander-Arnold");
        
      } catch (Exception e) {
        System.err.println(getTimestamp() + " Thread " + threadId + " - Error on vote #" + counter + ": " + e.getMessage());
      } finally {
        // CLOSE DRIVER AFTER EACH VOTE
        if (driver != null) {
          driver.quit();
        }
      }
      
      // WAIT 1 SECOND BEFORE CREATING NEW DRIVER FOR NEXT VOTE
      try {
        Thread.sleep(950);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        break;
      }
    }
    
    System.out.println(getTimestamp() + " Thread " + threadId + " stopped after " + counter + " votes");
  }

  private WebElement findAlexanderArnoldCard(WebDriver driver, WebDriverWait wait, int threadId) throws Exception {
    // Method 1: Try direct JavaScript jump to slide 24
    try {
        Thread.sleep(1000); // Wait for swiper to load
        
        JavascriptExecutor js = (JavascriptExecutor) driver;
        
        // Jump directly to slide 24 (Alexander-Arnold's slide)
        Boolean success = (Boolean) js.executeScript(
            "try {" +
            "  const swiper = document.querySelector('.swiper').swiper;" +
            "  if (swiper) {" +
            "    swiper.slideTo(24, 500);" + // 500ms animation
            "    return true;" +
            "  }" +
            "  return false;" +
            "} catch(e) { return false; }"
        );
        
        if (success) {
            Thread.sleep(1000); // Wait for slide transition
            
            WebElement playerCard = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//div[@data-swiper-slide-index='24']//li")));
            
            return playerCard;
        }
        
    } catch (Exception e) {
    }
    
    // Method 2: Try to find Alexander-Arnold directly first
    try {
      List<WebElement> alexanderCards = driver.findElements(
          By.xpath("//h2[contains(text(), '66. Alexander-Arnold')]/ancestor::li"));
      
      for (WebElement card : alexanderCards) {
        try {
          if (card.isDisplayed() && card.isEnabled()) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", card);
            Thread.sleep(1000);
            return card;
          }
        } catch (Exception e) {
          continue;
        }
      }
    } catch (Exception e) {
    }
    
    // Method 3: Navigate in chunks of 5 slides
    try {
        WebElement nextButton = driver.findElement(
            By.xpath("//button[contains(@class, 'navigation-button__Button')]"));
        
        int maxChunks = 15; // Max chunks to try
        
        for (int chunk = 0; chunk < maxChunks; chunk++) {
            // Click 5 times quickly
            for (int i = 0; i < 5; i++) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", nextButton);
                Thread.sleep(50); // Very short delay
            }
            
            Thread.sleep(1000); // Wait for animation to finish
            
            // Check if Alexander-Arnold is now visible
            try {
                List<WebElement> visibleCards = driver.findElements(
                    By.xpath("//h2[contains(text(), '66. Alexander-Arnold') and not(ancestor::div[contains(@class, 'swiper-slide-duplicate')])]/ancestor::li"));
                
                for (WebElement card : visibleCards) {
                    if (card.isDisplayed()) {
                        return card;
                    }
                }
            } catch (Exception e) {
                continue;
            }
        }
    } catch (Exception e) {
        System.out.println("Thread " + threadId + " - Navigation failed: " + e.getMessage());
    }
    
    // Method 4: Final fallback - try by slide index
    try {
        WebElement playerCard = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//div[@data-swiper-slide-index='24']//li | //div[@data-swiper-slide-index='55']//li")));
        return playerCard;
    } catch (Exception e) {
        throw new Exception("Could not find Alexander-Arnold after all methods attempted");
    }
  }

}