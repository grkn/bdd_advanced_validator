Scenario - Validation for example flow.
Given Method is Post and Endpoint is /tgf/{data}
When User sends @RequestParam as param
And User sends @RequestBody as sampleDto
And User sends @PathVariable as data
Then param can not be empty
And sampleDto.id can not be null
And data can not be blank
