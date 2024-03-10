Scenario - Validation for example flow.
Given Method is Post and Endpoint is /example/{path}
When User sends @RequestParam as queryParam
And User sends @RequestBody as body
And User sends @PathVariable as path
Then queryParam can not be null, empty
And body.id can not be blank
And path can not be blank
And path must be validated by \w*
