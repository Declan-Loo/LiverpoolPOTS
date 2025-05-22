## About
Multi-threaded code to vote **Trent Alexander-Arnold** for Liverpool POTY as he is leaving for Real Madrid at the end of the season.

## Prerequisites
- Java (v9 and over)
- Git

Lib files contain `Selenium` code which runs the web scraper

## How to Clone
`git clone 

## MacOS
In project folder, compile
`javac -cp ".:lib./*" *.java`

then run:
`java -cp ".:lib/*" Trent`

## Windows

In `VoteThread.java`, change:
```
// FROM (Mac):
System.setProperty("webdriver.chrome.driver", "./chromedriver");

// TO (Windows):
System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
```

to compile:
`javac -cp ".;lib/*" *.java`

then run:
`java -cp ".;lib/*" Trent`

ensure you download chromedriver exe and put it in your project directory via:
[https://googlechromelabs.github.io/chrome-for-testing/]