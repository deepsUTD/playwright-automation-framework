const { Builder, By, until } = require('selenium-webdriver');
const chrome = require('selenium-webdriver/chrome');
const { Select } = require('selenium-webdriver');
const { expect } = require('chai');

const baseUrl = 'https://the-internet.herokuapp.com';
const manualPauseMs = 5000;
let driver;

function pauseForManualCheck() {
  return new Promise((resolve) => setTimeout(resolve, manualPauseMs));
}

describe('The Internet Herokuapp smoke tests (Mocha + Chai + Selenium)', function () {
  this.timeout(60000);

  before(async function () {
    const options = new chrome.Options().addArguments('--window-size=1440,1000');
    if (process.env.HEROKUAPP_HEADLESS === 'true') {
      options.addArguments('--headless=new');
    }
    driver = await new Builder().forBrowser('chrome').setChromeOptions(options).build();
  });

  after(async function () {
    if (driver) {
      await driver.quit();
    }
  });

  afterEach(async function () {
    await pauseForManualCheck();
  });

  describe('Homepage examples', function () {
    it('lists the available example use cases', async function () {
      await driver.get(`${baseUrl}/`);
      await driver.wait(until.titleIs('The Internet'), 10000);
      const links = await driver.findElements(By.css('#content ul li a'));
      expect(links.length).to.be.at.least(40);
      expect(await driver.findElement(By.css('h2')).getText()).to.equal('Available Examples');
    });
  });

  describe('Add and remove elements', function () {
    it('adds elements and removes the selected one', async function () {
      await driver.get(`${baseUrl}/add_remove_elements/`);
      const addButton = By.css('button[onclick="addElement()"]');
      await driver.findElement(addButton).click();
      await driver.findElement(addButton).click();
      expect(await driver.findElements(By.className('added-manually'))).to.have.length(2);
      await driver.findElement(By.className('added-manually')).click();
      expect(await driver.findElements(By.className('added-manually'))).to.have.length(1);
    });
  });

  describe('Checkboxes', function () {
    it('toggles one checkbox independently from the other', async function () {
      await driver.get(`${baseUrl}/checkboxes`);
      const boxes = await driver.findElements(By.css('input[type="checkbox"]'));
      expect(boxes).to.have.length(2);
      const initialFirst = await boxes[0].isSelected();
      await boxes[0].click();
      expect(await boxes[0].isSelected()).to.equal(!initialFirst);
      expect(await boxes[1].isSelected()).to.equal(true);
    });
  });

  describe('Dropdown', function () {
    it('selects each available option', async function () {
      await driver.get(`${baseUrl}/dropdown`);
      const dropdown = new Select(await driver.findElement(By.id('dropdown')));
      for (const value of ['1', '2']) {
        await dropdown.selectByValue(value);
        expect(await dropdown.getFirstSelectedOption().getAttribute('value')).to.equal(value);
      }
    });
  });

  describe('JavaScript alerts', function () {
    it('accepts an alert and reports the result', async function () {
      await driver.get(`${baseUrl}/javascript_alerts`);
      await driver.findElement(By.xpath("//button[normalize-space()='Click for JS Alert']")).click();
      const alert = await driver.wait(until.alertIsPresent(), 5000);
      expect(await alert.getText()).to.equal('I am a JS Alert');
      await alert.accept();
      expect(await driver.findElement(By.id('result')).getText()).to.contain('You successfully clicked an alert');
    });
  });
});
