import {test} from "@playwright/test";

test("name",() =>{
console.log("Example")
})

test("Kick_Start_with_PlayWright",()=>{
    console.log("My First Test")//
} );

test("My 2nd Test Script", ()=>{
    console.log("2nd Test Script")
});

test('has title', async({page})=>{
    await page.goto('https://playwright.dev/');
} );
