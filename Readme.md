To run tests you have to update next vars in browserstack.yml and just runt test or suite

userName: YOUR_USERNAME

accessKey: YOUR_ACCESS_KEY

Example of run you can see on screen shoot browserstack_run_example.jpg 
It failed, but goal was to run tests on cloud browser, negative result is also result, so it is success :)

TestRail integration:

to upload result to testrail update ***trcliconfig.yaml*** with your vars and run ***push_to_testrail.sh***

Also you need to specify on testrail custom filed according to the documentation https://support.testrail.com/hc/en-us/articles/12609674354068-Code-first-workflow#h_01H9GE9TJ6RD39FCR0FEW0FBBE. 
Your tests will match by this field with existing test cases.
