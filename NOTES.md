# 1. What you changed and why
- Refactored campaign `getStats` function to make use count function instead of calling JPA `findBy` query in for loop. This will affect perfomance and eventually leading to long API respond time.
- Refactored `redeem()` function by annotating with @Transactional. `redeem()` function involves with multiple tables update, so @Transactional is needed to rollback commit when any error is thrown halfway. This will prevent DB data mismatch when one table gets updated but remaining table did not.
- Added a new table `user_campaign_redemption` and spring related JPA models and repo for user level capping tracking.
- Added flow to check for user level redemption capping on `redeem()` function.
- Added few missing test cases for `redeem()` to test out the newly added capping feature and existing validation.
- Constructed constraint like Foreign Key and Minimum Number Constraint on the tables. FK to make sure redemption and voucher are tying to the actual campaign ID. Also, to make sure campaign could not be removed when voucher and redemption existed. This is to make sure DB data consistency so that when unexpected request was called using the API, these error will be thrown instead of saving into the DB.

# 2. Anything you noticed but deliberately **did not** change, and your reasoning
- Audit client is sending a POST method to external endpoint, and this will lead to my testing error. I did not go and fix this logic as it is just server connection issue. Instead, I temporarily commented the post method call to disable this logic.
- Project did not implement lombok, but I did no changes to it since the changes I made to model is minimal.

# 3. What you'd do next if you had another day
- Add servlet filter to log out request and response in console to ease up development debugging.
- Add a new flow to handle **no capping** configuration by setting 0 as the `user_redemption_limit` for campaign. This will give another option to disable the capping for certain campaigns.
- Also, we can add a capping reset scheduler / batch (maybe not done in a day), to reset the  `redemption_count` of campaigns based on configuration. So in the future, we can have capping based on cycle dates.

# 4. Reflection answers
a) What did you get wrong first, and how did you notice?
- This is my first time using a H2 db, so when I first tried to connect to DBeaver, I was having issue on connecting to the Spring APP's DB. Instead, I was only connected to another instance of DB in my own DBeaver. Later, I tried the solution from AI by serving the H2 in server using TCP but lead to no avail. Finally, I was able to connect it after researching on H2 for awhile on the web and came to a solution. I connected to H2 using local h2-console and that allows me to debug on DB.
- When I first implement the capping feature, I made a mistake by assuming MySQL has the same functions as H2. When I was able to call the query in H2 console but the API failed with query syntax error, I found out that I will have to modify the query to cater for H2 usage.

b) Which AI suggestion did you reject, and why?
- AI added index for the tables, but I did not follow because I think index should be introduced when performance issue is encountered during querying.
- When working on test cases, AI also gave me test cases that includes of modifying existing data. Instead, I created new test data just for the test case and did not follow AI suggestion to amend the data value in the test case.
- When constructing constraint, AI suggest to use ALTER sql. However, I thought to put within the existing CREATE sql since our H2 DB tables is generated everytime on boot. And since we are not using any version control, we can keep it minimal to include in CREATE query only.

c) What took me longest?
- Connecting to the DB in order to write query for my own debug & Writing the logic for user redemption capping since there is multiple files to work on.

 