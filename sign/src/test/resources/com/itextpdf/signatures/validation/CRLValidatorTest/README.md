The certificates and some CRL responses were created with [certomancer](https://github.com/MatthiasValvekens/certomancer).
Test keys are stored in the keys folder to keep them in sync between the tests.
There is a yaml config per test that will have the test methods name except the test postfix.
All generated data will be in a subfolder per test with the same name as the yaml file. 

A script, createTestData.cmd, is provided to generate all the data. 
