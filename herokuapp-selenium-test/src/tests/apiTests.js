const { Builder } = require('selenium-webdriver');
const chrome = require('selenium-webdriver/chrome');
const axios = require('axios');
const { expect } = require('chai');

const BASE_URL = 'https://jsonplaceholder.typicode.com';

describe('API Tests using Axios and Selenium', function () {
  let driver;

  before(async function () {
    const options = new chrome.Options().addArguments('--headless=new', '--no-sandbox');
    driver = await new Builder().forBrowser('chrome').setChromeOptions(options).build();
    await driver.get(`${BASE_URL}/posts/1`);
  });

  after(async function () {
    if (driver) {
      await driver.quit();
    }
  });

  it('should perform a GET request and validate the response', async function () {
    const response = await axios.get(`${BASE_URL}/posts/1`);
    expect(response.status).to.equal(200);
    expect(response.headers['content-type']).to.include('application/json');
    expect(response.data).to.have.all.keys('userId', 'id', 'title', 'body');
    expect(response.data.id).to.equal(1);
    expect(response.data.title).to.be.a('string').that.is.not.empty;
  });

  it('should return the same data in the browser as via axios', async function () {
    const response = await axios.get(`${BASE_URL}/posts/1`);
    const pageText = await driver.executeScript('return document.body.innerText');
    expect(JSON.parse(pageText)).to.deep.equal(response.data);
  });

  it('should perform a POST request and validate the response', async function () {
    const newPost = { title: 'foo', body: 'bar', userId: 1 };
    const response = await axios.post(`${BASE_URL}/posts`, newPost);
    expect(response.status).to.equal(201);
    expect(response.data).to.include(newPost);
    expect(response.data.id).to.equal(101);
  });

  it('should perform a PUT request and validate the response', async function () {
    const updated = { id: 1, title: 'updated', body: 'updated body', userId: 1 };
    const response = await axios.put(`${BASE_URL}/posts/1`, updated);
    expect(response.status).to.equal(200);
    expect(response.data).to.deep.equal(updated);
  });

  it('should perform a DELETE request and validate the response', async function () {
    const response = await axios.delete(`${BASE_URL}/posts/1`);
    expect(response.status).to.equal(200);
    expect(response.data).to.deep.equal({});
  });
});
