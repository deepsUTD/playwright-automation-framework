import { test } from "@playwright/test";



test.describe("Practice Describe Tests", () => {
   
     test.afterEach("Practice Test - After Each Hook", async ({ page }) => {
  console.log("Annotation Practice - After Each Hook");
})


    test("Practice Test1", async ({ page, browserName }) => {
    test.skip(browserName === 'firefox', 'Not supported in Firefox');
    console.log("Annotation Practice Test1 started");
    console.log("Annotation Practice Test1 completed");
  })

  test("Practice Test2", async ({ page, browserName }) => {
    test.skip(browserName === 'firefox', 'Not supported in Firefox');
    console.log("Annotation Practice Test2 started");
    console.log("Annotation Practice Test2 completed");
  })

  test("Practice Test3", async ({ page, browserName }) => {
    test.skip(browserName === 'firefox', 'Not supported in Firefox');
    console.log("Annotation Practice Test3 started");
    console.log("Annotation Practice Test3 completed");
  })

  

  });

  

  test.skip("Practice Test4", async ({ page }) => {
    console.log("Annotation Practice Test4 started");
    console.log("Annotation Practice Test4 completed");
  });

  test("Practice Test5", async ({ page }) => {
    console.log("Annotation Practice Test5 started");
    console.log("Annotation Practice Test5 completed");
  });

  test("Practice Test6", async ({ page }) => {
    test.fail();
    console.log("Annotation Practice Test6 started");
    console.log("Annotation Practice Test6 completed");
  });   

  test.afterAll("Practice Test - Before All Hook", async () => {
    console.log("Annotation Practice - Before All Hook");
  });